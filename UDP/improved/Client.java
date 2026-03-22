import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client {
    @SuppressWarnings({ "CallToPrintStackTrace", "UseSpecificCatch" })
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            int PORT = 40000;
            InetAddress IP = InetAddress.getByName("192.168.56.1");

            DatagramSocket cs = new DatagramSocket();

            System.out.println("Enter message:");
            String sent = sc.nextLine();

            byte w[] = new byte[1024];
            w = sent.getBytes();

            DatagramPacket wp = new DatagramPacket(w, sent.length(), IP, PORT);
            cs.send(wp);

            int TOUT = 5000;
            cs.setSoTimeout(TOUT);

            byte r[] = new byte[1024 * 2];
            DatagramPacket rp = new DatagramPacket(r, r.length);
            cs.receive(rp);

            String received = new String(r);
            int s_port = rp.getPort();
            InetAddress addr = rp.getAddress();
            String sip = addr.getHostAddress();

            System.out.println(sip + ":" + s_port + " --> " + received);

            cs.close();
            sc.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Connection Timed out: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
