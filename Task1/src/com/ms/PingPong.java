package com.ms;

public class PingPong {
    private static final Object lock = new Object();
    private static boolean pingTurn = true;
    private static final int MAX_ITERATIONS = 5;

    static class PingTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                synchronized (lock) {
                    while (!pingTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    System.out.print("Ping");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = false;
                    lock.notify();
                }
            }
        }
    }

    static class PongTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                synchronized (lock) {
                    while (pingTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    System.out.print("Pong");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = true;
                    lock.notify();
                }
            }
        }
    }
}
