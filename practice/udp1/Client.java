package practice.udp1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try {
            Scanner sc = new Scanner(System.in);

            int PORT = 47000;
            InetAddress IP = InetAddress.getByName("192.168.222.130");
            DatagramSocket cs = new DatagramSocket();
            System.out.println("Client Active for UDP");

            System.out.println("Enter message:");
            String msg = sc.nextLine();

            byte[] b = new byte[1024];
            b = msg.getBytes();

            DatagramPacket dp = new DatagramPacket(b, msg.length(), IP, PORT);
            cs.send(dp);

            cs.close();
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
