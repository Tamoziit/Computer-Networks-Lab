import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	final static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		int PORT = 47000;
		String IP = "192.168.222.130";
		Socket cs = new Socket(IP, PORT);
		System.out.println("Client connected to Server");

		DataInputStream dis = new DataInputStream(cs.getInputStream());
		DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

		Thread rt = new Thread(new ClientReadThread(cs, dis));
		Thread wt = new Thread(new ClientWriteThread(cs, dis, dos, sc));

		rt.start();
		wt.start();
	}
}

class ClientReadThread implements Runnable {
	Socket cs;
	DataInputStream dis;

	ClientReadThread(Socket cs, DataInputStream dis) {
		this.cs = cs;
		this.dis = dis;
	}

	@Override
	public void run() {
		try {
			String received, date;

			while (true) {
				received = dis.readUTF();
				date = (new Date()).toString();
				System.out.println("[SERVER] " + received + " (" + date + ")");
			}
		} catch (SocketException e) {
			System.out.println("Connection closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ClientWriteThread implements Runnable {
	Socket cs;
	DataInputStream dis;
	DataOutputStream dos;
	Scanner sc;

	ClientWriteThread(Socket cs, DataInputStream dis, DataOutputStream dos, Scanner sc) {
		this.cs = cs;
		this.dis = dis;
		this.dos = dos;
		this.sc = sc;
	}

	@Override
	public void run() {
		try {
			String sent;

			while (true) {
				sent = sc.nextLine();
				dos.writeUTF(sent);

				if (sent.equalsIgnoreCase("exit")) {
					System.out.println("Disconnected from Server");
					break;
				}
			}

			dis.close();
			dos.close();
			cs.close();
			sc.close();
		} catch (SocketException e) {
			System.out.println("Connection closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
