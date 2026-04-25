package practice.concurrent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

            while (true) {
                String received = dis.readUTF();
                String date = (new Date()).toString();
                System.out.println("[CLIENT " + count + "] " + received + " (" + date + ")");

                if (received.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected");
                    dis.close();
                    dos.close();
                    s.close();
                    break;
                }

                String sent;

                synchronized (sc) {
                    System.out.println("Enter message:");
                    sent = sc.nextLine();
                }
                dos.writeUTF(sent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}