import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    @SuppressWarnings({ "CallToPrintStackTrace", "UseSpecificCatch" })
    public static void main(String[] args) {
        try {
            int PORT = 40000;
            DatagramSocket ss = new DatagramSocket(PORT);
            System.out.println("Server Started... Waiting for Client");

            byte r[] = new byte[1024];
            DatagramPacket rp = new DatagramPacket(r, r.length);

            while (true) {
                ss.receive(rp);
                r = rp.getData();

                String msg = new String(r);
                int cli_port = rp.getPort();
                InetAddress addr = rp.getAddress();
                String clip = addr.getHostAddress();

                System.out.println(clip + ":" + cli_port + " --> " + msg);

                byte w[] = new byte[1024 * 2];
                String sent = msg + "-ACK";
                w = sent.getBytes();

                DatagramPacket wp = new DatagramPacket(w, sent.length(), addr, cli_port);
                ss.send(wp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
