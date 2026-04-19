import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) throws IOException {

        int PORT = 47000;
        String IP = "127.0.0.1";

        Socket cs = new Socket(IP, PORT);

        System.out.println("Connected to Server");

        DataInputStream dis = new DataInputStream(cs.getInputStream());
        DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

        Scanner sc = new Scanner(System.in);

        // Registering with username
        System.out.print("Enter your username: ");
        String username = sc.nextLine();
        dos.writeUTF(username);
        System.out.println(dis.readUTF()); // initializing mssg from server

        Thread readThread = new Thread(new ClientReadThread(dis, cs));
        Thread writeThread = new Thread(new ClientWriteThread(dos, sc, cs));

        readThread.start();
        writeThread.start();
    }
}

class ClientReadThread implements Runnable {

    DataInputStream dis;
    Socket s;

    ClientReadThread(DataInputStream dis, Socket s) {
        this.dis = dis;
        this.s   = s;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = dis.readUTF();

                if (msg.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    s.close();
                    break;
                }

                System.out.println(msg);
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