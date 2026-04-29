import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class CounterClient {
    @SuppressWarnings({ "CallToPrintStackTrace", "ConvertToTryWithResources", "UseSpecificCatch" })
    public static void main(String[] args) throws IOException {
        try {
            Scanner sc = new Scanner(System.in);

            int PORT = 47000;
            String IP = "127.0.0.1";
            Socket cs = new Socket(IP, PORT);
            System.out.println("Client connected to Server");

            DataInputStream dis = new DataInputStream(cs.getInputStream());
            DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

            int requestNum = 0;
            String prompt;

            while (true) {
                requestNum++;
                String sent = "PING #" + requestNum;
                dos.writeUTF(sent);

                String received = dis.readUTF();
                System.out.println("[SERVER] " + received);

                Thread.sleep(5000);

                if (requestNum % 10 == 0) {
                    System.out.println("Continue pinging? y/n");
                    prompt = sc.nextLine();

                    if (prompt.equalsIgnoreCase("n")) {
                        sent = "exit";
                        dos.writeUTF(sent);

                        System.out.println("Stopping PING...");
                        break;
                    }
                }
            }

            cs.close();
            dis.close();
            dos.close();
            System.out.println("Client disconnected from Server");
        } catch (SocketException e) {
            System.out.println("Disconnected from Server unexpectedly: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("Disconnected from Server unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Disconnected from Server unexpectedly: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Client Terminated via Interruption " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
