package side_channel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        try {
            // Encrypt the message
            System.out.println("--- Starting Encryption Process ---");
            Algorithm.main(null);

            // Read the encrypted message from the file
            byte[] encryptedMessage = Base64.getDecoder().decode(Files.readAllBytes(Paths.get("encryptedMessage.txt")));

            // Perform side channel attack
            System.out.println("\n--- Starting Side Channel Attack ---");
            byte[] recoveredKey = SideChannelAttack.simulateSideChannelAttack(encryptedMessage);
            System.out.println("Side Channel Attack Completed!");
            System.out.println("Recovered Key: " + SideChannelAttack.bytesToHex(recoveredKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}