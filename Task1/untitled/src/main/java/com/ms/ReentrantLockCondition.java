package com.ms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Пример использования ReentrantLock и Condition для синхронизации потоков.
 */
public final class ReentrantLockCondition {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Condition PING_CONDITION = LOCK.newCondition();
    private static final Condition PONG_CONDITION = LOCK.newCondition();
    private static boolean pingTurn = true;
    private static final int MAX_ITERATIONS = 5;

    /**
     * Приватный конструктор для утилитного класса.
     */
    private ReentrantLockCondition() {
        // Утилитный класс
    }

    /**
     * Основной метод приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        Thread pingThread = new Thread(new PingTask());
        Thread pongThread = new Thread(new PongTask());

        pingThread.start();
        pongThread.start();

        try {
            pingThread.join();
            pongThread.join();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // PMD: создание бесполезного объекта
        new String("Hello PMD");

        // SpotBugs: возможное деление на ноль
        int x = (int) (Math.random() * 2) - 1;
        int y = 10 / (x + 1);

        // Checkstyle: плохое форматирование и магическое число
        if (y>5){System.out.println("Bad style " + y);}

        // ---- "Ошибка" для VisualVM ----
        // 1. Утечка памяти: бесконечно растущий список
        java.util.List<byte[]> leakList = new java.util.ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            leakList.add(new byte[1024 * 50]); // 50 KB каждый
            if (i % 1000 == 0) {
                System.out.println("Allocated " + i + " chunks");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 2. Нагрузка CPU: "пустой" цикл
        while (true) {
            Math.sqrt(System.nanoTime()); // постоянная нагрузка
        }
    }

    // Обычный доступ к вложенному классу
    static class PingTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                LOCK.lock();
                try {
                    while (!pingTurn) {
                        PING_CONDITION.await();
                    }

                    // Use System.out for demonstration purposes
                    System.out.print("Ping");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = false;
                    PONG_CONDITION.signal();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }

    // Обычный доступ к вложенному классу
    static class PongTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                LOCK.lock();
                try {
                    while (pingTurn) {
                        PONG_CONDITION.await();
                    }

                    // Use System.out for demonstration purposes
                    System.out.print("Pong");
                    if (i < MAX_ITERATIONS - 1) {
                        System.out.print(" ");
                    }

                    pingTurn = true;
                    PING_CONDITION.signal();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }
}
