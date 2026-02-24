public class PingPongSync {

    private final Object lock = new Object();
    private boolean pingTurn = true;
    private int iterations;

    public PingPongSync(int n) {
        this.iterations = n;
    }

    public void play() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                synchronized (lock) {
                    while (!pingTurn) try {
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                    System.out.println("Ping");
                    pingTurn = false;
                    lock.notify();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < iterations; i++) {
                synchronized (lock) {
                    while (pingTurn) try {
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                    System.out.println("Pong");
                    pingTurn = true;
                    lock.notify();
                }
            }
        });

        t1.start();
        t2.start();
    }

    public static void main(String[] args) {
        new PingPongSync(5).play();
    }
}
