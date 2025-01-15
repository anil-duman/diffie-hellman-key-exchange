package side_channel;

import java.math.BigInteger;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

public class Algorithm {

    public static void main(String[] args) {
        try {
            // 1. Diffie-Hellman Key Exchange
            BigInteger p = new BigInteger(2048, 100, new SecureRandom()); // Large prime
            BigInteger g = BigInteger.valueOf(2); // Base
            SecureRandom random = new SecureRandom();
            BigInteger a = new BigInteger(p.bitLength(), random); // Alice's private key
            BigInteger b = new BigInteger(p.bitLength(), random); // Bob's private key
            BigInteger A = g.modPow(a, p); // Alice's public key
            BigInteger B = g.modPow(b, p); // Bob's public key
            BigInteger sharedSecretAlice = B.modPow(a, p); // Alice's shared secret
            BigInteger sharedSecretBob = A.modPow(b, p);   // Bob's shared secret

            // Verify shared secrets match
            if (!sharedSecretAlice.equals(sharedSecretBob)) {
                throw new SecurityException("Shared secrets do not match!");
            }

            // 2. Key Derivation (for DES - Extract 56 bits)
            byte[] keyBytes = sharedSecretAlice.toByteArray();
            byte[] desKey = new byte[8];
            System.arraycopy(
                keyBytes,
                Math.max(0, keyBytes.length - 8),
                desKey,
                0,
                Math.min(keyBytes.length, 8)
            );
            desKey = setParityBits(desKey);
            SecretKeySpec secretKey = new SecretKeySpec(desKey, "DES");

            // 3. Message Encryption
            String plaintext = "Hello Bob, this is a secure message!";
            Cipher encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedMessage = encryptCipher.doFinal(plaintext.getBytes());
            String base64EncodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

            // Write encrypted message to file
            try (FileWriter writer = new FileWriter("encryptedMessage.txt")) {
                writer.write(base64EncodedMessage);
            }

            System.out.println("Original Key: " + bytesToHex(desKey));
            System.out.println("Encrypted message saved to file!");

            // 4. Read encrypted message from file
            String readEncryptedMessage = "";
            try (FileReader reader = new FileReader("encryptedMessage.txt")) {
                StringBuilder sb = new StringBuilder();
                int character;
                while ((character = reader.read()) != -1) {
                    sb.append((char) character);
                }
                readEncryptedMessage = sb.toString();
            }

            // 5. Decrypt the message
            String decryptedMessage = decryptMessage(readEncryptedMessage, secretKey);
            System.out.println("Decrypted message: " + decryptedMessage);

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String decryptMessage(String base64EncodedMessage, SecretKeySpec secretKey) throws Exception {
        // Base64 decode
        byte[] encryptedMessage = Base64.getDecoder().decode(base64EncodedMessage);

        // Decrypt
        Cipher decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);

        // Convert to string
        return new String(decryptedMessage);
    }

    private static byte[] setParityBits(byte[] key) {
        for (int i = 0; i < key.length; i++) {
            byte b = key[i];
            int onesCount = Integer.bitCount(b & 0xFF);
            if (onesCount % 2 == 0) {
                key[i] = (byte) (b ^ 0x01); // Flip the last bit to ensure odd parity
            }
        }
        return key;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
