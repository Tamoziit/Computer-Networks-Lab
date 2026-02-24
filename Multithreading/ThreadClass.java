import java.util.Scanner;

// 1. Implementation using the Thread Class
class PingThread extends Thread {

    private int count;

    public PingThread(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            System.out.println("Thread Class: Ping");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }
}

class PongThread extends Thread {

    private int count;

    public PongThread(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            System.out.println("Thread Class: Pong");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }
}

// 2. Implementation using the Runnable Interface
class PingRunnable implements Runnable {

    private int count;

    public PingRunnable(int count) {
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < count; i++) {
            System.out.println("Runnable: Ping");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }
}

public class ThreadClass {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of times to print: ");
        int n = sc.nextInt();

        System.out.println("\n--- Starting Thread Class Implementation ---");
        PingThread t1 = new PingThread(n);
        PongThread t2 = new PongThread(n);
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
        }

        System.out.println("\n--- Starting Runnable Implementation ---");
        Thread r1 = new Thread(new PingRunnable(n));
        Thread r2 = new Thread(() -> { 
            for (int i = 0; i < n; i++) {
                System.out.println("Runnable: Pong");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        });

        r1.start();
        r2.start();
    }
}
