package practice.udp1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        try {
            int PORT = 47000;
            DatagramSocket ss = new DatagramSocket(PORT);
            System.out.println("Server running PORT: " + PORT);

            byte[] b = new byte[1024];
            DatagramPacket dp = new DatagramPacket(b, b.length);

            while (true) {
                ss.receive(dp);
                b = dp.getData();

                String msg = new String(b);
                int cli_port = dp.getPort();
                InetAddress clip = dp.getAddress();
                String ip = clip.getHostAddress();

                System.out.println("[CLIENT " + ip + ":" + cli_port + "] --> " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
