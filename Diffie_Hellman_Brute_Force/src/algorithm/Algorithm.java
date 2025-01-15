package algorithm;

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

            BigInteger secretAlice = B.modPow(a, p); // Alice's shared secret
            BigInteger secretBob = A.modPow(b, p);   // Bob's shared secret

            // Verify shared secrets match
            if (!secretAlice.equals(secretBob)) {
                throw new SecurityException("Shared secrets do not match!");
            }

            // 2. Key Derivation (for DES)
            byte[] keyBytes = secretAlice.toByteArray();
            byte[] desKey = new byte[8];
            System.arraycopy(
                keyBytes, 
                Math.max(0, keyBytes.length - 8), 
                desKey, 
                0, 
                Math.min(keyBytes.length, 8)
            );

            SecretKeySpec secretKey = new SecretKeySpec(desKey, "DES");

            // 3. Message Encryption
            String plaintext = "Hello Bob, this is a secure message!";
            Cipher encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedMessage = encryptCipher.doFinal(plaintext.getBytes());
            String base64EncodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

            // Write to file
            try (FileWriter writer = new FileWriter("encryptedMessage.txt")) {
                writer.write(base64EncodedMessage);
            }

            System.out.println("Encrypted message saved to file!");

            // 4. Message Decryption
            String base64FromFile;
            try (BufferedReader reader = new BufferedReader(new FileReader("encryptedMessage.txt"))) {
                base64FromFile = reader.readLine();
            }

            byte[] encryptedFromFile = Base64.getDecoder().decode(base64FromFile);
            Cipher decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedMessage = decryptCipher.doFinal(encryptedFromFile);
            System.out.println("Decrypted Message: " + new String(decryptedMessage));

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}