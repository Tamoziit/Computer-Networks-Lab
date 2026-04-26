package practice.udp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        try {
            Scanner sc = new Scanner(System.in);

            int PORT = 47000;
            int T_OUT = 5000;
            InetAddress IP = InetAddress.getByName("192.168.222.130");

            DatagramSocket cs = new DatagramSocket();
            System.out.println("Client active for UDP");

            byte[] r = new byte[1024 * 2];
            DatagramPacket rp = new DatagramPacket(r, r.length);
            String msg;

            byte[] w = new byte[1024];
            DatagramPacket wp = null;
            String sent;

            while (true) {
                System.out.println("Enter message:");
                sent = sc.nextLine();
                w = sent.getBytes();

                wp = new DatagramPacket(w, sent.length(), IP, PORT);
                cs.send(wp);

                if (sent.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected");
                    break;
                }

                cs.setSoTimeout(T_OUT);

                cs.receive(rp);
                msg = new String(rp.getData(), 0, rp.getLength());

                InetAddress ip = rp.getAddress();
                String sip = ip.getHostAddress();
                int s_port = rp.getPort();

                System.out.println("[SERVER " + sip + ":" + s_port + "] --> " + msg);
            }

            cs.close();
            sc.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Connection Timed out: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
