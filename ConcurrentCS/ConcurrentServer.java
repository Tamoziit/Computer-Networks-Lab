import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ConcurrentServer {

    // Shared scanner for console input
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);

        System.out.println("Server running on PORT " + PORT);

        while (true) {
            Socket s = ss.accept();

            ClientThread ob = new ClientThread(s, sc);
            Thread t = new Thread(ob);
            t.start();
        }
    }
}

class ClientThread implements Runnable {

    Socket s;
    Scanner sc;

    ClientThread(Socket s, Scanner sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    public void run() {
        try {
            InetAddress clip = s.getInetAddress();
            String ip = clip.getHostAddress();
            int cli_port = s.getPort();

            System.out.println("Connected to Client: " + ip + ":" + cli_port);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            while (true) {

                String received = dis.readUTF();

                System.out.println("From Client: " + ip + ":" + cli_port + " --> " + received);

                if (received.equalsIgnoreCase("exit")) {
                    dis.close();
                    dos.close();
                    s.close();
                    System.out.println("Client: " + ip + ":" + cli_port + " disconnected");
                    break;
                }

                String sent;

                synchronized (sc) {
                // Synchronize console access
                    System.out.print("Reply to " + ip + ":" + cli_port + " -> ");
                    sent = sc.nextLine();
                }

                dos.writeUTF(sent);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}