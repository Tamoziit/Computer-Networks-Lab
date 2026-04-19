import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ChatServer {
    static HashMap<String, DataOutputStream> clients = new HashMap<>(); // <username, o/p stream thread>
    final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);

        System.out.println("Server running on PORT " + PORT);

        while (true) {
            Socket s = ss.accept();

            ClientHandlerThread ob = new ClientHandlerThread(s, sc);
            Thread t = new Thread(ob);
            t.start();
        }
    }

    static synchronized void register(String username, DataOutputStream dos) {
        clients.put(username, dos); // 2 threads shouldn't write to the hashmap at the same time
    }

    static synchronized void unregister(String username) {
        clients.remove(username);
    }

    // Routing message to a specific user only
    static synchronized void sendTo(String target, String msg, DataOutputStream senderDos) throws IOException {
        DataOutputStream dos = clients.get(target); // getting the stream to where dat needs to be routed

        if (dos != null) {
            dos.writeUTF(msg);
        } else {
            senderDos.writeUTF("[Server] User '" + target + "' is not connected.");
        }
    }
}

// Relay thread
class ClientHandlerThread implements Runnable {
    Socket s;
    Scanner sc;

    ClientHandlerThread(Socket s, Scanner sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run() {
        String username = null;
        DataOutputStream dos = null;

        try {
            InetAddress clip = s.getInetAddress();
            String IP = clip.getHostAddress();
            int PORT = s.getPort();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // 1st message is always regsitration with username
            username = dis.readUTF();
            ChatServer.register(username, dos);
            System.out.println("[+] Client Connected : " + IP + ":" + PORT + " - " + username);
            dos.writeUTF("[Server] Welcome, " + username);

            while (true) {
                String raw = dis.readUTF();

                if (raw.equalsIgnoreCase("exit")) {
                    dos.writeUTF(raw);
                    System.out.println("[-] " + username + " disconnected.");
                    break;
                }

                int sep = raw.indexOf(":");
                if (sep == -1) { // invalid mssg format
                    dos.writeUTF("[Server] Invalid format. Use  recipient:message");
                    continue;
                }

                String target = raw.substring(0, sep).trim();
                String message = raw.substring(sep + 1).trim();

                if (target.equalsIgnoreCase(username)) {
                    dos.writeUTF("[Server] You cannot message yourself");
                    continue;
                }

                String sent = "[From] " + username + ": " + message;
                ChatServer.sendTo(target, sent, dos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (username != null)
                ChatServer.unregister(username);

            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}