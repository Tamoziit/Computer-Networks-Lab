import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class CounterServer {
    private static int counter = 0;

    public static synchronized int increment() {
        return ++counter;
    }

    public static void main(String[] args) throws IOException {
        int PORT = 47000;
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server is running on PORT: " + PORT);

        int cli_count = 0;
        while (true) {
            Socket s = ss.accept();
            cli_count++;

            ClientHandlerThread ob = new ClientHandlerThread(s, cli_count);
            Thread t = new Thread(ob);
            t.start();
        }
    }
}

class ClientHandlerThread implements Runnable {
    Socket s;
    int cli_count;

    ClientHandlerThread(Socket s, int cli_count) {
        this.s = s;
        this.cli_count = cli_count;
    }

    @Override
    @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace"})
    public void run() {
        InetAddress clip = s.getInetAddress();
        String ip = clip.getHostAddress();
        int cli_port = s.getPort();
        System.out.println("[+] Client " + cli_count + " connected with " + ip + ":" + cli_port);

        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            while (true) {
                String received = dis.readUTF();
                String timestamp = (new Date()).toString();
                System.out.println("[CLIENT " + cli_count + "] " + received + " (" + timestamp + ")");

                if (received.equalsIgnoreCase("exit")) {
                    System.out.println("[-] Client " + cli_count + " disconnected");
                    break;
                }

                int current = CounterServer.increment();
                String sent = "Counter = " + current;
                dos.writeUTF(sent);
            }

            s.close();
            dis.close();
            dos.close();
        } catch (SocketException e) {
            System.out.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}