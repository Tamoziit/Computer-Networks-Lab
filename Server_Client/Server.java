import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(40000);
            System.out.println("Server Started... Waiting for Client");
            Socket s = ss.accept();
            System.out.println("Client Connected");
            DataInputStream dis = new DataInputStream(s.getInputStream());

            String msg = (String) dis.readUTF();
            System.out.println("Message received from client: " + msg);
            
            dis.close();
            s.close();
            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
