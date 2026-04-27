package geditPractice.p2;
import java.io.*;
import java.util.*;
import java.net.*;

public class LogServer {
	public static void main(String[] args) {
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
				msg = (new String(rp.getData(), 0, rp.getLength())).trim();
				
				String[] parts = msg.split("\\|");
				String timestamp = parts.length > 0 ? parts[0] : "N/A";
				String username = parts.length > 1 ? parts[1] : "N/A";
				String os = parts.length > 2 ? parts[2] : "N/A";
				
				int cli_port = rp.getPort();
				InetAddress ip = rp.getAddress();
				String clip = ip.getHostAddress();
				
				try (PrintWriter pw = new PrintWriter(new FileWriter("logs.txt", true))) {
					pw.printf("%s | %s | %d | %s | %s%n", timestamp, clip, cli_port, username, os);
				} catch (FileNotFoundException | EOFException e) {
					System.out.println("Couldn't find Log file");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("[+] Log entry saved for " + clip + ":" + cli_port);
				
				sent = (new Date()).toString();
				w = sent.getBytes();
				wp = new DatagramPacket(w, sent.length(), ip, cli_port);
				ss.send(wp);
			}
		} catch (SocketException e) {
			System.out.println("Conn. closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
