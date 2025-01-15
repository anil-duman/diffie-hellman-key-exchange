package mitm;

import java.io.*;
import java.net.*;

public class MITMClient {
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket("localhost", 5000)) {
            System.out.println("MITM Client connected to the server!");

            // MITM sunucusundan mesajı al
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            String interceptedMessage = in.readUTF();

            // Yakalanan mesajı göster
            System.out.println("Intercepted Message: " + interceptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

