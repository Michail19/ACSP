package com.ms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ReentrantLockConditionTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Thread testThread;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        System.setOut(originalOut);
        if (testThread != null && testThread.isAlive()) {
            testThread.interrupt();
            testThread.join(1000);
        }
    }

    @Test
    public void testPingPongExecution() throws InterruptedException {
        testThread = new Thread(() -> ReentrantLockCondition.main(new String[]{}));
        testThread.start();
        testThread.join(5000);

        assertFalse(testThread.isAlive(), "Программа должна завершиться");

        String output = outContent.toString();
        assertTrue(output.contains("Ping") || output.contains("Pong"),
                "Вывод должен содержать Ping или Pong");
    }

    @Test
    @Timeout(5)
    public void testMainMethodCompletes() throws InterruptedException {
        // Тест для проверки что main метод не зависает
        testThread = new Thread(() -> ReentrantLockCondition.main(new String[]{}));
        testThread.start();
        testThread.join();

        assertFalse(testThread.isAlive());
    }

    @Test
    public void testPingTaskExecution() throws InterruptedException {
        // Тест отдельного выполнения PingTask
        ReentrantLockCondition.PingTask pingTask = new ReentrantLockCondition.PingTask();
        testThread = new Thread(pingTask);
        testThread.start();

        // Даем потоку время на выполнение
        Thread.sleep(100);

        // Прерываем выполнение так как это бесконечный цикл в основном методе
        testThread.interrupt();
        testThread.join(1000);

        String output = outContent.toString();
        assertTrue(output.contains("Ping"), "PingTask должен выводить Ping");
    }

    @Test
    public void testPongTaskExecution() throws InterruptedException {
        // Тест отдельного выполнения PongTask
        ReentrantLockCondition.PongTask pongTask = new ReentrantLockCondition.PongTask();
        testThread = new Thread(pongTask);
        testThread.start();

        Thread.sleep(100);
        testThread.interrupt();
        testThread.join(1000);

        String output = outContent.toString();
        assertTrue(output.contains("Pong"), "PongTask должен выводить Pong");
    }

    @Test
    public void testThreadInterruption() throws InterruptedException {
        // Тест обработки прерывания потоков
        AtomicBoolean pingInterrupted = new AtomicBoolean(false);
        AtomicBoolean pongInterrupted = new AtomicBoolean(false);

        Thread pingThread = new Thread(() -> {
            ReentrantLockCondition.PingTask pingTask = new ReentrantLockCondition.PingTask();
            try {
                pingTask.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException && e.getCause() instanceof InterruptedException) {
                    pingInterrupted.set(true);
                }
            }
        });

        Thread pongThread = new Thread(() -> {
            ReentrantLockCondition.PongTask pongTask = new ReentrantLockCondition.PongTask();
            try {
                pongTask.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException && e.getCause() instanceof InterruptedException) {
                    pongInterrupted.set(true);
                }
            }
        });

        pingThread.start();
        pongThread.start();

        // Даем потокам время начать выполнение
        Thread.sleep(100);

        // Прерываем потоки
        pingThread.interrupt();
        pongThread.interrupt();

        pingThread.join(1000);
        pongThread.join(1000);

        // Проверяем что потоки корректно завершились
        assertFalse(pingThread.isAlive(), "Ping поток должен завершиться после прерывания");
        assertFalse(pongThread.isAlive(), "Pong поток должен завершиться после прерывания");
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        // Тест конкурентного доступа
        final int threadCount = 3;
        Thread[] pingThreads = new Thread[threadCount];
        Thread[] pongThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            pingThreads[i] = new Thread(new ReentrantLockCondition.PingTask());
            pongThreads[i] = new Thread(new ReentrantLockCondition.PongTask());
        }

        // Запускаем все потоки
        for (int i = 0; i < threadCount; i++) {
            pingThreads[i].start();
            pongThreads[i].start();
        }

        // Даем время на выполнение
        Thread.sleep(500);

        // Останавливаем все потоки
        for (int i = 0; i < threadCount; i++) {
            pingThreads[i].interrupt();
            pongThreads[i].interrupt();
        }

        // Ждем завершения
        for (int i = 0; i < threadCount; i++) {
            pingThreads[i].join(1000);
            pongThreads[i].join(1000);
        }

        // Проверяем что все потоки завершились
        for (int i = 0; i < threadCount; i++) {
            assertFalse(pingThreads[i].isAlive(), "Ping поток " + i + " должен завершиться");
            assertFalse(pongThreads[i].isAlive(), "Pong поток " + i + " должен завершиться");
        }
    }
}
