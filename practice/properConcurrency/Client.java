package practice.properConcurrency;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        String IP = "192.168.222.130";
        Socket cs = new Socket(IP, PORT);
        System.out.println("Connected to Server");

        DataInputStream dis = new DataInputStream(cs.getInputStream());
        DataOutputStream dos = new DataOutputStream(cs.getOutputStream());
        AtomicBoolean running = new AtomicBoolean(true); // shared flag in heap-memory to be shared across threads

        Thread rt = new Thread(new ClientReadThread(dis, running));
        Thread wt = new Thread(new ClientWriteThread(cs, dis, dos, running));

        rt.setDaemon(true); // auto-dies when WriteThread exits
        rt.start();
        wt.start();
    }
}

class ClientReadThread implements Runnable {
    DataInputStream dis;
    AtomicBoolean running;

    ClientReadThread(DataInputStream dis, AtomicBoolean running) {
        this.dis = dis;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) { // while client is alive
                System.out.println("[SERVER] " + dis.readUTF());
            }
        } catch (SocketException | EOFException e) {
            if (running.get())
                System.out.println("Server closed connection.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientWriteThread implements Runnable {
    Socket cs;
    DataInputStream dis;
    DataOutputStream dos;
    AtomicBoolean running;
    Scanner sc = new Scanner(System.in); // exclusive scanner for each thread --> no race cond.

    ClientWriteThread(
            Socket cs,
            DataInputStream dis,
            DataOutputStream dos,
            AtomicBoolean running) {
        this.cs = cs;
        this.dis = dis;
        this.dos = dos;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                String msg = sc.nextLine(); // no synchronized required for exclusive Scanner
                dos.writeUTF(msg);

                if (msg.equalsIgnoreCase("exit")) {
                    running.set(false); // Client disconnects --> signals ReadThread to stop
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cs.close();
            } catch (IOException ignored) {
            }
        }
    }
}