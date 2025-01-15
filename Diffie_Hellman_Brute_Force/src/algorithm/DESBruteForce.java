package algorithm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

public class DESBruteForce {
    public static void main(String[] args) {
        try {
            // Read the encrypted message from file
            String base64FromFile;
            try (BufferedReader reader = new BufferedReader(new FileReader("encryptedMessage.txt"))) {
                base64FromFile = reader.readLine();
            }

            byte[] encryptedMessage = Base64.getDecoder().decode(base64FromFile);

            // Brute-Force the DES key
            bruteForceDES(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bruteForceDES(byte[] encryptedMessage) {
        byte[] baseKey = new byte[8]; // DES key is 8 bytes
        long startTime = System.currentTimeMillis();
        long attempts = 0;

        // Iterate through all possible 56-bit keys
        for (long i = 0; i < (1L << 56); i++) {
            // Generate the key
            for (int j = 0; j < 7; j++) {
                baseKey[j] = (byte) (i >> (8 * (6 - j)) & 0xFF);
            }

            // Add parity bit to the last byte (simplified)
            baseKey[7] = (byte) 0x01;

            try {
                SecretKeySpec candidateKey = new SecretKeySpec(baseKey, "DES");
                Cipher decryptCipher = Cipher.getInstance("DES");
                decryptCipher.init(Cipher.DECRYPT_MODE, candidateKey);

                byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);
                String decryptedText = new String(decryptedMessage);

                // Check if the decrypted text is meaningful
                if (isMeaningful(decryptedText)) {
                    System.out.println("Key found! -> " + bytesToHex(baseKey));
                    System.out.println("Decrypted Message: " + decryptedText);
                    System.out.println("Total Attempts: " + attempts);
                    return;
                }
            } catch (Exception ignored) {
                // Invalid key, continue
            }

            attempts++;

            // Display progress every 5 seconds
            if ((System.currentTimeMillis() - startTime) / 1000 >= 5) {
                System.out.printf("Attempts: %d, Elapsed Time: %d seconds\n", attempts, (System.currentTimeMillis() - startTime) / 1000);
                startTime = System.currentTimeMillis();
            }
        }

        System.out.println("Key not found!");
    }

    public static boolean isMeaningful(String text) {
        // Simple meaningfulness check
        if (!text.matches("[\\x20-\\x7E]+")) {
            return false;
        }
        String[] keywords = {"hello", "bob", "secure", "message"};
        for (String word : keywords) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
