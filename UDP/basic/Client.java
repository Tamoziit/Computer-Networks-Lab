import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
            String msg = sc.nextLine();

            byte b[] = new byte[1024];
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
