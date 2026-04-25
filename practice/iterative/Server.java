package practice.iterative;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server running on PORT: " + PORT);

        Socket s = ss.accept();
        InetAddress clip = s.getInetAddress();
        String IP = clip.getHostAddress();
        int cli_port = s.getPort();
        System.out.println("Client Connected: " + IP + ":" + cli_port);

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        while (true) {
            String received = dis.readUTF();
            System.out.println("[CLIENT] " + received);

            if (received.equalsIgnoreCase("exit")) {
                System.out.println("Client disconnected");
                break;
            }

            System.out.println("Enter message:");
            String sent = sc.nextLine();
            dos.writeUTF(sent);
        }

        dis.close();
        dos.close();
        s.close();
        ss.close();
        sc.close();
    }
}
