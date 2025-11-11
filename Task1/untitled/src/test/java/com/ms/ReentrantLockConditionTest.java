package com.ms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ReentrantLockConditionTest {

    @Test
    public void testPingPongExecution() throws InterruptedException {
        // Тест для проверки корректности выполнения
        Thread testThread = new Thread(() -> {
            ReentrantLockCondition.main(new String[]{});
        });

        testThread.start();
        testThread.join(5000); // Ждем максимум 5 секунд

        assertFalse(testThread.isAlive(), "Программа должна завершиться");
    }
}
