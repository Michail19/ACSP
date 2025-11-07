import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockCondition {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition pingCondition = lock.newCondition();
    private static final Condition pongCondition = lock.newCondition();
    private static boolean pingTurn = true;
    private static final int MAX_ITERATIONS = 5;

    public static void main(String[] args) {
        Thread pingThread = new Thread(new PingTask());
        Thread pongThread = new Thread(new PongTask());

        pingThread.start();
        pongThread.start();

        try {
            pingThread.join();
            pongThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class PingTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                lock.lock();
                try {
                    while (!pingTurn) {
                        pingCondition.await();
                    }

                    System.out.print("Ping");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = false;
                    pongCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    static class PongTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                lock.lock();
                try {
                    while (pingTurn) {
                        pongCondition.await();
                    }

                    System.out.print("Pong");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = true;
                    pingCondition.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
