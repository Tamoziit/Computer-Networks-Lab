package geditPractice.p2;
import java.io.*;
import java.util.*;
import java.net.*;

public class LogClient {
	public static void main(String[] args) {
		try {
			int T_OUT = 5000;
			int PORT = 47000;
			InetAddress IP = InetAddress.getByName("192.168.222.130");
			
			DatagramSocket cs =  new DatagramSocket();
			cs.setSoTimeout(T_OUT);
			
			String timestamp = (new Date()).toString();
			String username = System.getProperty("user.name");
			String os = System.getProperty("os.name");
			String sent = timestamp + "|" + username + "|" + os;
			
			byte[] w = sent.getBytes();
			DatagramPacket wp = new DatagramPacket(w, sent.length(), IP, PORT);
			cs.send(wp);
			
			byte[] r = new byte[1024 * 2];
			DatagramPacket rp = new DatagramPacket(r, r.length);
			cs.receive(rp);
			
			String received = (new String(rp.getData(), 0, rp.getLength())).trim();
			
			int s_port = rp.getPort();
            InetAddress addr = rp.getAddress();
            String sip = addr.getHostAddress();
            System.out.println(sip + ":" + s_port + " --> " + received);

            cs.close();
		} catch (SocketTimeoutException e) {
            System.out.println("Connection Timed out: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
