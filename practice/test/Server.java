import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);

        InetAddress sip = ss.getInetAddress();
        String IP = sip.getHostAddress();
        System.out.println("Server is running on: " + IP + ":" + PORT);

        Socket s = ss.accept();
        System.out.println("Client Connected");

        InetAddress clip = s.getInetAddress();
        String ip = clip.getHostAddress();
        int cli_port = s.getPort();

        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        String received = dis.readUTF();
        System.out.println(ip + ":" + cli_port + " --> " + received);

        System.out.println("Enter message:");
        String sent = sc.nextLine();
        dos.writeUTF(sent);

        dis.close();
        dos.close();
        s.close();
        ss.close();
        sc.close();
    }
}
