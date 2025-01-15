package mitm;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Base64;

public class MITMServer {
    public static void main(String[] args) {
        try (ServerSocket mitmServer = new ServerSocket(5000)) {
            System.out.println("MITM Server is ready...");

            // Bob'un bağlantısını bekle
            Socket bobSocket = mitmServer.accept();
            System.out.println("Bob connected to MITM Server!");

            // Alice'e bağlan
            Socket aliceSocket = new Socket("localhost", 6000);
            System.out.println("MITM connected to Alice!");

            DataInputStream aliceIn = new DataInputStream(aliceSocket.getInputStream());
            DataOutputStream aliceOut = new DataOutputStream(aliceSocket.getOutputStream());
            DataInputStream bobIn = new DataInputStream(bobSocket.getInputStream());
            DataOutputStream bobOut = new DataOutputStream(bobSocket.getOutputStream());

            // Alice'den parametreleri al ve Bob'a ilet
            String p = aliceIn.readUTF();
            String g = aliceIn.readUTF();
            String A = aliceIn.readUTF();

            System.out.println("MITM intercepted Alice's public key: " + A);

            BigInteger fakeA = new BigInteger(A).add(BigInteger.ONE); // Sahte anahtar oluştur
            bobOut.writeUTF(p);
            bobOut.writeUTF(g);
            bobOut.writeUTF(fakeA.toString());

            // Bob'un herkese açık anahtarını al ve Alice'e ilet
            String B = bobIn.readUTF();
            aliceOut.writeUTF(B);

            System.out.println("MITM intercepted Bob's public key: " + B);

            // Alice'den şifreli mesajı al ve manipüle etmeden Bob'a ilet
            String encryptedMessage = aliceIn.readUTF();
            System.out.println("MITM intercepted encrypted message: " + encryptedMessage);

            bobOut.writeUTF(encryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
