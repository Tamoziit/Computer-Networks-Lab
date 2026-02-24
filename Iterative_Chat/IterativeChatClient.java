import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class IterativeChatClient {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		int PORT = 47000;
		String IP = "127.0.0.1";
		Socket cs = new Socket(IP, PORT);
		System.out.println("Server connected to Client");

		DataInputStream dis = new DataInputStream(cs.getInputStream());
		DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

		while (true) {
			System.out.println("Enter message:");
			String sent = sc.nextLine();
			dos.writeUTF(sent);

			if (sent.equalsIgnoreCase("exit")) {
				dis.close();
				dos.close();
				cs.close();
				break;
			}

			String received = dis.readUTF();
			System.out.println("From Server: " + received);
		}

		cs.close();
		sc.close();
	}
}
