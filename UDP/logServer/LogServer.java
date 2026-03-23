import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class LogServer {
    @SuppressWarnings({ "CallToPrintStackTrace", "UseSpecificCatch" })
    public static void main(String[] args) {
        try {
            int PORT = 40000;
            DatagramSocket ss = new DatagramSocket(PORT);
            System.out.println("Server Started... Waiting for Client");

            byte r[] = new byte[1024];
            DatagramPacket rp = new DatagramPacket(r, r.length);

            int id = 1;
            File logFile = new File("logs.txt");
            if (logFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                    String line, lastLine = null;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            lastLine = line;
                        }
                    }
                    if (lastLine != null) {
                        try {
                            String firstPart = lastLine.split("\\|")[0].trim();
                            id = Integer.parseInt(firstPart) + 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            while (true) {
                ss.receive(rp);
                r = rp.getData();

                String msg = (new String(r)).trim();
                String[] parts = msg.split("\\|");

                String timestamp = parts.length > 0 ? parts[0] : "N/A";
                String username = parts.length > 1 ? parts[1] : "N/A";
                String os = parts.length > 2 ? parts[2] : "N/A";

                int cli_port = rp.getPort();
                InetAddress addr = rp.getAddress();
                String clip = addr.getHostAddress();

                try (PrintWriter pw = new PrintWriter(new FileWriter("logs.txt", true))) {
                    pw.printf("%d | %s | %s | %d | %s | %s%n", id++, timestamp, clip, cli_port, username, os);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Log entry " + (id - 1) + " saved for " + clip + ":" + cli_port);

                byte w[] = new byte[1024 * 2];
                Date date = new Date();
                String sent = date.toString();
                w = sent.getBytes();

                DatagramPacket wp = new DatagramPacket(w, sent.length(), addr, cli_port);
                ss.send(wp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
