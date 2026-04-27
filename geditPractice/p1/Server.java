
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    final static Scanner sc = new Scanner(System.in);

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server is running on PORT: " + PORT);

        int count = 0;
        while (true) {
            Socket s = ss.accept();
            count++;

            InetAddress ip = s.getInetAddress();
            String clip = ip.getHostAddress();
            int cli_port = s.getPort();
            System.out.println("[+] Client " + count + " connected: " + clip + ":" + cli_port);

            ClientHandlerThread ob = new ClientHandlerThread(s, count, sc);
            Thread t = new Thread(ob);
            t.start();
        }
    }
}

class ClientHandlerThread implements Runnable {

    Socket s;
    int count;
    Scanner sc;

    ClientHandlerThread(
            Socket s,
            int count,
            Scanner sc
    ) {
        this.s = s;
        this.count = count;
        this.sc = sc;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            Thread rt = new Thread(new ServerReadThread(s, dis, dos, count));
            Thread wt = new Thread(new ServerWriteThread(s, dos, sc));

            rt.start();
            wt.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerReadThread implements Runnable {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    int count;

    ServerReadThread(Socket s, DataInputStream dis, DataOutputStream dos, int count) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            String received, date;

            while (true) {
                received = dis.readUTF();
                date = (new Date()).toString();
                System.out.println("[CLIENT " + count + "] " + received + " (" + date + ")");
                if (received.equalsIgnoreCase("exit")) {
                    System.out.println("[-] Client " + count + " disconnected");
                    break;
                }
            }

            dis.close();
            dos.close();
            s.close();
        } catch (SocketException e) {
            // clean exit
            System.out.println("Connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerWriteThread implements Runnable {

    Socket s;
    DataOutputStream dos;
    Scanner sc;

    ServerWriteThread(Socket s, DataOutputStream dos, Scanner sc) {
        this.s = s;
        this.dos = dos;
        this.sc = sc;
    }

    @Override
    public void run() {
        try {
            String sent;

            while (true) {
                if (s.isClosed()) {
                    break;
                }

                synchronized (sc) {
                    sent = sc.nextLine();
                }
                dos.writeUTF(sent);
            }
        } catch (SocketException e) {
            // clean exit
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
