import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class IterativeClient extends Thread {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int PORT = 45000;
        String IP = "127.0.0.1";
        Socket cs = new Socket(IP, PORT);
        System.out.println("Client connected to Server");

        DataInputStream dis = new DataInputStream(cs.getInputStream());
        DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

        System.out.println("Enter message:");
        String msg = sc.nextLine();
        dos.writeUTF(msg);

        if (msg.equalsIgnoreCase("exit")) {
            dis.close();
            dos.close();
            cs.close();
            sc.close();
            return;
        }

        System.out.println("From Server: " + dis.readUTF());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        }
    }
}
