package side_channel;

import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SideChannelAttack {

    // Function to set the parity bit
    private static byte setParityBit(byte b) {
        // Check the number of 1 bits in b to make the parity odd
        int parity = Integer.bitCount(b) % 2 == 0 ? 1 : 0; // Odd parity
        return (byte) ((b & 0xFE) | parity); // Place the parity bit in the last bit
    }

    // Side channel attack simulation
    public static byte[] simulateSideChannelAttack(byte[] encryptedMessage) {
        byte[] recoveredKey = new byte[8]; // 8-byte key for DES
        Arrays.fill(recoveredKey, (byte) 0); // Initialize the key with zeros

        try {
            for (int byteIndex = 0; byteIndex < 8; byteIndex++) {
                long[] timingData = new long[128]; // Timing data for key guesses
                for (int keyGuess = 0; keyGuess < 128; keyGuess++) {
                    byte[] candidateKey = Arrays.copyOf(recoveredKey, recoveredKey.length);
                    candidateKey[byteIndex] = (byte) (keyGuess << 1); // Add parity later

                    SecretKeySpec candidateSecretKey = new SecretKeySpec(candidateKey, "DES");
                    Cipher decryptCipher = Cipher.getInstance("DES");

                    long totalDecryptionTime = 0;
                    int attempts = 1000; // Increase the number of attempts to 1000

                    for (int i = 0; i < attempts; i++) {
                        long startTime = System.nanoTime(); // Start timing
                        try {
                            decryptCipher.init(Cipher.DECRYPT_MODE, candidateSecretKey);
                            decryptCipher.doFinal(encryptedMessage);
                        } catch (Exception ignored) {
                            // Continue because the key is incorrect
                        }
                        long endTime = System.nanoTime(); // End timing
                        totalDecryptionTime += (endTime - startTime);
                    }

                    timingData[keyGuess] = totalDecryptionTime / attempts; // Average time
                }

                // Find the best guess
                int bestGuess = 0;
                long minTiming = Long.MAX_VALUE;
                for (int i = 0; i < timingData.length; i++) {
                    if (timingData[i] < minTiming) {
                        minTiming = timingData[i];
                        bestGuess = i;
                    }
                }

                recoveredKey[byteIndex] = setParityBit((byte) bestGuess); // Take the best guess and set parity
                System.out.printf("Recovered Byte[%d]: %02x%n", byteIndex, recoveredKey[byteIndex]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recoveredKey;
    }

    // Convert byte array to hexadecimal format
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
