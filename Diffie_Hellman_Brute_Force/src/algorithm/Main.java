package algorithm;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Diffie-Hellman and AES Encryption Testing
            System.out.println("Diffie-Hellman Key Exchange and AES Encryption Testing Begins\r\n");
            Algorithm.main(null); // Diffie-Hellman key exchange and AES encryption operations

            // 2. DES Brute-Force Test
            System.out.println("\n--- DES Brute-Force Test Starting ---");
            DESBruteForce.main(null); // DES brute-force attack

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}