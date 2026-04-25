package practice.concurrent2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Scanner;

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

            ClientHandlerThread ob = new ClientHandlerThread(s, sc, count);
            Thread t = new Thread(ob);
            t.start();
        }
    }
}

class ClientHandlerThread implements Runnable {
    Socket s;
    Scanner sc;
    int count;

    ClientHandlerThread(Socket s, Scanner sc, int count) {
        this.s = s;
        this.sc = sc;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            InetAddress clip = s.getInetAddress();
            String IP = clip.getHostAddress();
            int cli_port = s.getPort();
            System.out.println("Client Connected: " + IP + ":" + cli_port);

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
                    System.out.println("Client disconnected");
                    break;
                }
            }

            dis.close();
            dos.close();
            s.close();
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
                if (s.isClosed())
                    break;

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