package com.ms;

public class Main {
    public static void main(String[] args) {
        Thread pingThread = new Thread(new PingPong.PingTask());
        Thread pongThread = new Thread(new PingPong.PongTask());

        pingThread.start();
        pongThread.start();

        try {
            pingThread.join();
            pongThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
