import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ConcurrentServer2 {
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
}

class ClientHandlerThread implements Runnable {
    Socket s;
    Scanner sc;

    ClientHandlerThread(Socket s, Scanner sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    public void run() {
        try {
            InetAddress clip = s.getInetAddress();
            String IP = clip.getHostAddress();
            int PORT = s.getPort();

            System.out.println("Client Connected : " + IP + ":" + PORT);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            Thread readThread = new Thread(new ServerReadThread(dis, IP, PORT));
            Thread writeThread = new Thread(new ServerWriteThread(dos, sc, IP, PORT));

            readThread.start();
            writeThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerReadThread implements Runnable {
    DataInputStream dis;
    String IP;
    int PORT;

    ServerReadThread(DataInputStream dis, String IP, int PORT) {
        this.dis = dis;
        this.IP = IP;
        this.PORT = PORT;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = dis.readUTF();

                if (msg.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected : " + IP + ":" + PORT);
                    break;
                }

                System.out.println("\nClient " + IP + ":" + PORT + " -> " + msg);
            }
        } catch (Exception e) {
            System.out.println("Client read error : " + IP + ":" + PORT);
            e.printStackTrace();
        }
    }
}

class ServerWriteThread implements Runnable {
    DataOutputStream dos;
    final Scanner sc;
    String IP;
    int PORT;

    ServerWriteThread(DataOutputStream dos, Scanner sc, String IP, int PORT) {
        this.dos = dos;
        this.sc = sc;
        this.IP = IP;
        this.PORT = PORT;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg;

                synchronized (sc) {
                    msg = sc.nextLine();
                }

                dos.writeUTF(msg);

                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Client write error : " + IP + ":" + PORT);
            e.printStackTrace();
        }
    }
}
