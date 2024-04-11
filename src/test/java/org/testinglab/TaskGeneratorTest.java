package org.testinglab;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static java.lang.System.out;

import org.testinglab.scheduler.Scheduler;
import static org.testinglab.scheduler.Scheduler.DEFAULT_MAX_READY_TASKS_COUNT;

public class TaskGeneratorTest {

    Scheduler scheduler = new Scheduler();
    Processor processor = new Processor();
    System system = new System(processor, scheduler);
    TaskGenerator taskGenerator = new TaskGenerator(system);

    /**
     * randomPriority() in TaskGenerator generates Priority in a needed range
     */
    @RepeatedTest(100)
    void testRandomPriority() {
        var generatedPriority = taskGenerator.randomPriority();
        out.println(generatedPriority);
        assertTrue(0 <= generatedPriority.value && generatedPriority.value <= 3,
                "Generated Priority is in range 0-3: " + generatedPriority.value);
    }

    /**
     * after generate() in TaskGenerator queue is not empty in Scheduler
     */
    @Test
    void testQueueIsNotEmpty() {
        taskGenerator.generate();
        assertFalse(scheduler.isEmpty());
    }

    void fullQueue() {
        int i = DEFAULT_MAX_READY_TASKS_COUNT;
        while (i > 0) {
            taskGenerator.generate();
            i--;
        }
    }

    /**
     * after 5 generate() in TaskGenerator queue is full in Scheduler (maxReadyTasks = DEFAULT_MAX_READY_TASKS_COUNT)
     */
    @Test
    void testQueueIsFull() {
        fullQueue();
        assertTrue(scheduler.isFull());
    }

    /**
     * full queue in Scheduler is queue with size = maxReadyTasks = DEFAULT_MAX_READY_TASKS_COUNT
     */
    @Test
    void testFullQueueSize() {
        fullQueue();
        assertEquals(scheduler.getSize(), scheduler.maxReadyTasks);
    }

    /**
     * increase in Scheduler queue after generate() in TaskGenerator only by one task
     */
    @Test
    void testQueueIncrease() {
        assertTrue(scheduler.isEmpty());
        taskGenerator.generate();
        assertEquals(scheduler.getSize(), 1);
    }
}
