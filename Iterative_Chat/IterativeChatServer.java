import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class IterativeChatServer {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		int PORT = 47000;
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("Server running on PORT " + PORT);

		while (true) {
			Socket s = ss.accept(); // new connection when a client disconnects
			System.out.println("Server connected to Client");

			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			while (true) {
				String received = dis.readUTF();
				System.out.println("From Client: " + received);

				if (received.equalsIgnoreCase("exit")) {
					dis.close();
					dos.close();
					s.close(); // closing only this client
					System.out.println("Client disconnected");
					break;
				}

				System.out.println("Enter message:");
				String sent = sc.nextLine();
				dos.writeUTF(sent);
			}

			System.out.println("Terminate Server? y/n"); // reached only when innerloop terminates. i.e, a client disconnects
			String prompt = sc.nextLine();

			if (prompt.equalsIgnoreCase("y")) {
				System.out.println("Server Terminated");
				break;
			}
		}

		ss.close();
		sc.close();
	}
}
