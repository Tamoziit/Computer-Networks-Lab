package practice.properConcurrency;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    // client map with their thread handler objects
    static ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues = new ConcurrentHashMap<>();

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server running on PORT: " + PORT);

        // ONE input thread owns the Scanner --> no race condition due to sharing
        new Thread(new ServerInputThread(clientQueues)).start();

        int count = 0;
        while (true) {
            Socket s = ss.accept();
            count++;

            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            clientQueues.put(count, queue);

            ClientHandlerThread handler = new ClientHandlerThread(s, count, queue, clientQueues);
            new Thread(handler).start();
        }
    }
}

// Owns the Scanner — dispatches "id: msg" or "all: msg" to queues
class ServerInputThread implements Runnable {
    ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues;
    Scanner sc = new Scanner(System.in);

    ServerInputThread(ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues) {
        this.clientQueues = clientQueues;
    }

    @Override
    public void run() {
        System.out.println("Format:  <id>: <message>   or   all: <message>");

        while (true) {
            String line = sc.nextLine().trim();
            if (!line.contains(":")) {
                System.out.println("Invalid format. Use  <id>: <msg>  or  all: <msg>");
                continue;
            }

            String[] parts = line.split(":", 2);
            String target = parts[0].trim();
            String msg = parts[1].trim();

            if (target.equalsIgnoreCase("all")) {
                clientQueues.values().forEach(q -> q.offer(msg));
            } else {
                try {
                    int id = Integer.parseInt(target);
                    BlockingQueue<String> q = clientQueues.get(id);
                    if (q != null)
                        q.offer(msg);
                    else
                        System.out.println("Client " + id + " not connected.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ClientHandlerThread implements Runnable {
    Socket s;
    int id;
    BlockingQueue<String> queue;
    ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues;

    ClientHandlerThread(
            Socket s,
            int id,
            BlockingQueue<String> queue,
            ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues) {
        this.s = s;
        this.id = id;
        this.queue = queue;
        this.clientQueues = clientQueues;
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

            new Thread(new ServerReadThread(s, dis, id, queue, clientQueues)).start();
            new Thread(new ServerWriteThread(s, dos, id, queue)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerReadThread implements Runnable {
    Socket s;
    DataInputStream dis;
    int id;
    BlockingQueue<String> queue;
    ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues;

    ServerReadThread(
            Socket s,
            DataInputStream dis,
            int id,
            BlockingQueue<String> queue,
            ConcurrentHashMap<Integer, BlockingQueue<String>> clientQueues) {
        this.s = s;
        this.dis = dis;
        this.id = id;
        this.queue = queue;
        this.clientQueues = clientQueues;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String received = dis.readUTF();
                System.out.println("[CLIENT " + id + "] " + received);

                if (received.equalsIgnoreCase("exit")) {
                    System.out.println("Client " + id + " disconnected.");
                    break;
                }
            }
        } catch (SocketException | EOFException e) {
            System.out.println("Client " + id + " lost connection.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientQueues.remove(id); // stopping ServerInputThread sending here
            queue.offer("__POISON__"); // unblocking WriteThread's queue.take()
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
    }
}

class ServerWriteThread implements Runnable {
    Socket s;
    DataOutputStream dos;
    int id;
    BlockingQueue<String> queue;

    ServerWriteThread(
            Socket s,
            DataOutputStream dos,
            int id,
            BlockingQueue<String> queue) {
        this.s = s;
        this.dos = dos;
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = queue.take(); // blocking Scanner cleanly — no Scanner race
                if (msg.equals("__POISON__"))
                    break; // ReadThread signals shutdown

                dos.writeUTF(msg);
            }
        } catch (SocketException e) {
            System.out.println("Write to Client " + id + " failed — disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}