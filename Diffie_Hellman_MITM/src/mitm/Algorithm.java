package mitm;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Algorithm {
    public static void main(String[] args) {
        try {
            // Alice sunucusunu başlat
            new Thread(() -> startAlice()).start();

            // Bob istemcisini başlat
            new Thread(() -> startBob()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startAlice() {
        try (ServerSocket aliceServer = new ServerSocket(6000)) {
            System.out.println("Alice is ready and waiting for a connection...");

            // Bob veya MITM'nin bağlantısını bekle
            Socket socket = aliceServer.accept();
            System.out.println("Alice connected to MITM or Bob!");

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Diffie-Hellman parametreleri
            BigInteger p = new BigInteger(2048, 100, new SecureRandom()); // Büyük asal
            BigInteger g = BigInteger.valueOf(2); // Taban
            BigInteger a = new BigInteger(p.bitLength(), new SecureRandom()); // Alice'in özel anahtarı
            BigInteger A = g.modPow(a, p); // Alice'in herkese açık anahtarı

            // 1. Parametreleri gönder
            out.writeUTF(p.toString());
            out.writeUTF(g.toString());
            out.writeUTF(A.toString());

            // 2. Bob'un herkese açık anahtarını al
            BigInteger B = new BigInteger(in.readUTF());
            BigInteger sharedSecret = B.modPow(a, p);

            System.out.println("Alice calculated shared secret.");

            // 3. Mesajı şifrele ve gönder
            String plaintext = "Hello Bob, this is a secure message!";
            byte[] encryptedMessage = encryptMessage(plaintext, sharedSecret);
            out.writeUTF(Base64.getEncoder().encodeToString(encryptedMessage));

            System.out.println("Alice sent the encrypted message!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startBob() {
        try (Socket bobSocket = new Socket("localhost", 5000)) {
            System.out.println("Bob connected to MITM Server!");

            DataOutputStream out = new DataOutputStream(bobSocket.getOutputStream());
            DataInputStream in = new DataInputStream(bobSocket.getInputStream());

            // 1. Alice'in parametrelerini al
            BigInteger p = new BigInteger(in.readUTF());
            BigInteger g = new BigInteger(in.readUTF());
            BigInteger A = new BigInteger(in.readUTF());

            // 2. Bob'un herkese açık anahtarını hesapla ve gönder
            BigInteger b = new BigInteger(p.bitLength(), new SecureRandom()); // Bob'un özel anahtarı
            BigInteger B = g.modPow(b, p); // Bob'un herkese açık anahtarı
            out.writeUTF(B.toString());

            BigInteger sharedSecret = A.modPow(b, p);
            System.out.println("Bob calculated shared secret.");

            // 3. Şifreli mesajı al
            String encryptedMessage = in.readUTF();
            String decryptedMessage = decryptMessage(Base64.getDecoder().decode(encryptedMessage), sharedSecret);

            System.out.println("Bob received and decrypted message: " + decryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] encryptMessage(String plaintext, BigInteger sharedSecret) throws Exception {
        byte[] key = sharedSecret.toByteArray();
        byte[] desKey = new byte[8];
        System.arraycopy(key, Math.max(0, key.length - 8), desKey, 0, Math.min(key.length, 8));

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(desKey, "DES"));

        return cipher.doFinal(plaintext.getBytes());
    }

    private static String decryptMessage(byte[] encryptedMessage, BigInteger sharedSecret) throws Exception {
        byte[] key = sharedSecret.toByteArray();
        byte[] desKey = new byte[8];
        System.arraycopy(key, Math.max(0, key.length - 8), desKey, 0, Math.min(key.length, 8));

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(desKey, "DES"));

        return new String(cipher.doFinal(encryptedMessage));
    }
}
