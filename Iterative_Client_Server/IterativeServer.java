import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class IterativeServer {

    public static void main(String[] args) throws IOException {
        int PORT = 45000;
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server is running on PORT: " + PORT);
        DataInputStream dis = null;
        DataOutputStream dos = null;

        while (true) {
            Socket s = ss.accept();
            System.out.println("Server connected to Client");

            InetAddress clip = s.getInetAddress();
            String ip = clip.getHostAddress();
            int cli_port = s.getPort();
            System.out.println(ip + ":" + cli_port);

            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            
            String msg = dis.readUTF();
            System.out.println("From Client: " + msg);

            if (msg.equalsIgnoreCase("exit")) {
                dis.close();
                dos.close();
                s.close();
                break;
            }

            dos.writeUTF("Hello from Server");
        }

        ss.close();
    }
}
