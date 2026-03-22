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

            byte b[] = new byte[1024];
            DatagramPacket dp = new DatagramPacket(b, b.length);

            while (true) {
                ss.receive(dp);
                b = dp.getData();

                String msg = new String(b);
                int cli_port = dp.getPort();
                InetAddress addr = dp.getAddress();
                String clip = addr.getHostAddress();

                System.out.println(clip + ":" + cli_port + " --> " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
