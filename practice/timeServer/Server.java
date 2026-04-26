package practice.timeServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class Server {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        try {
            int PORT = 47000;
            DatagramSocket ss = new DatagramSocket(PORT);
            System.out.println("Server is running on PORT: " + PORT);

            byte[] r = new byte[1024];
            DatagramPacket rp = new DatagramPacket(r, r.length);
            String msg;

            byte[] w = new byte[1024 * 2];
            DatagramPacket wp = null;
            String sent;

            while (true) {
                ss.receive(rp);
                msg = new String(rp.getData(), 0, rp.getLength());

                InetAddress ip = rp.getAddress();
                String cli_ip = ip.getHostAddress();
                int cli_port = rp.getPort();
                System.out.println("[CLIENT " + cli_ip + ":" + cli_port + "] --> " + msg);

                sent = (new Date()).toString();
                w = sent.getBytes();

                wp = new DatagramPacket(w, sent.length(), ip, cli_port);
                ss.send(wp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
