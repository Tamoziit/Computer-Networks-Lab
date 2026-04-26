package practice.timeServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Client {
    public static void main(String[] args) throws IOException {
        try {
            int T_OUT = 5000; // 5ms
            int PORT = 47000;
            InetAddress IP = InetAddress.getByName("192.168.222.130");

            DatagramSocket cs = new DatagramSocket();
            cs.setSoTimeout(T_OUT);
            System.out.println("Client active for UDP");

            String sent = "CONN";
            byte[] w = sent.getBytes();

            DatagramPacket wp = new DatagramPacket(w, sent.length(), IP, PORT);
            cs.send(wp);

            byte[] r = new byte[1024 * 2];
            DatagramPacket rp = new DatagramPacket(r, r.length);

            cs.receive(rp);
            String timestamp = new String(rp.getData(), 0, rp.getLength());
            InetAddress ip = rp.getAddress();
            String s_ip = ip.getHostAddress();
            int s_port = rp.getPort();

            System.out.println("[SERVER " + s_ip + ":" + s_port + "] --> " + timestamp);
            cs.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Conection timed out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
