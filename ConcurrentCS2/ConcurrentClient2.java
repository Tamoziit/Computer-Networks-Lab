import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConcurrentClient2 {

    public static void main(String[] args) throws IOException {

        int PORT = 47000;
        String IP = "127.0.0.1";

        Socket cs = new Socket(IP, PORT);

        System.out.println("Connected to Server");

        DataInputStream dis = new DataInputStream(cs.getInputStream());
        DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

        Scanner sc = new Scanner(System.in);

        Thread readThread = new Thread(new ClientReadThread(dis));
        Thread writeThread = new Thread(new ClientWriteThread(dos, sc, cs));

        readThread.start();
        writeThread.start();
    }
}

class ClientReadThread implements Runnable {

    DataInputStream dis;

    ClientReadThread(DataInputStream dis) {
        this.dis = dis;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = dis.readUTF();

                if (msg.equalsIgnoreCase("exit")) {
                    System.out.println("Server disconnected.");
                    break;
                }

                System.out.println("\nFrom Server: " + msg);
            }

        } catch (Exception e) {
            System.out.println("Connection closed.");
        }
    }
}

class ClientWriteThread implements Runnable {

    DataOutputStream dos;
    Scanner sc;
    Socket s;

    ClientWriteThread(DataOutputStream dos, Scanner sc, Socket s) {
        this.dos = dos;
        this.sc = sc;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = sc.nextLine();

                dos.writeUTF(msg);

                if (msg.equalsIgnoreCase("exit")) {
                    s.close();
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Unable to send message.");
        }
    }
}