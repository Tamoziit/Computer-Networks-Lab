package practice.iterative2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        int PORT = 47000;
        String IP = "192.168.222.130";
        Socket cs = new Socket(IP, PORT);
        System.out.println("Client connected to Server");

        DataInputStream dis = new DataInputStream(cs.getInputStream());
        DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

        while (true) {
            System.out.println("Enter message:");
            String sent = sc.nextLine();
            dos.writeUTF(sent);

            if (sent.equalsIgnoreCase("exit")) {
                System.out.println("Disconnected from Server");
                break;
            }

            String received = dis.readUTF();
            System.out.println("[SERVER] " + received);
        }

        dis.close();
        dos.close();
        cs.close();
        sc.close();
    }
}
