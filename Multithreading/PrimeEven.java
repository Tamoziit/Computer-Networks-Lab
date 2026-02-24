import java.util.Scanner;

class Prime extends Thread {

    int x;
    int y;

    Prime(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        int f, i, j;
        for (i = x; i <= y; i++) {
            if (i < 2) {
                continue;
            }

            f = 0;
            for (j = 2; j <= Math.sqrt(i); j++) {
                if (i % j == 0) {
                    f++;
                    break;
                }
            }

            if (f == 0) {
                System.out.println("Prime: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }
}

class Even extends Thread {

    int x;
    int y;

    Even(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        int i;
        for (i = x; i <= y; i++) {
            if (i % 2 == 0) {
                System.out.println("Even: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }
}

public class PrimeEven {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter range:");
        int x = sc.nextInt();
        int y = sc.nextInt();

        Prime ob1 = new Prime(x, y);
        Even ob2 = new Even(x, y);

        ob1.start();
        ob2.start();
    }
}
