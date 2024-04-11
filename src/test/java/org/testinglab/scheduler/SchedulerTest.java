package org.testinglab.scheduler;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.testinglab.*;
import org.testinglab.System;
import org.testinglab.task.BasicTask;
import org.testinglab.task.ExtendedTask;
import org.testinglab.task.Priority;
import org.testinglab.task.State;

public class SchedulerTest {

    Scheduler scheduler = new Scheduler();
    Processor processor = new Processor();
    System system = new System(processor, scheduler);

    TaskGenerator taskGenerator = new TaskGenerator(system);
    JobGenerator jobGenerator = new JobGenerator();

    TestUtils testUtils = new TestUtils();

    BasicTask basicTask = new BasicTask(taskGenerator.randomPriority(), jobGenerator.generate());

    /**
     * after scheduleTask() by Scheduler task in SUSPENDED state activates (becomes READY), also queue +1
     */
    @Test
    void testScheduleSuspendedTask() {
        assertTrue(scheduler.isEmpty());

        BasicTask suspendedTask = new BasicTask(taskGenerator.randomPriority(), jobGenerator.generate());
        assertEquals(suspendedTask.getState(), State.SUSPENDED);

        scheduler.scheduleTask(suspendedTask);

        assertEquals(suspendedTask.getState(), State.READY);
        assertEquals(scheduler.getSize(), 1);
    }

    /**
     * after scheduleTask() by Scheduler task in WAITING state releases (becomes READY), also queue +1
     */
    @Test
    void testScheduleWaitingTask() {
        assertTrue(scheduler.isEmpty());

        ExtendedTask waitingTask = new ExtendedTask(taskGenerator.randomPriority(), () -> true);
        assertEquals(waitingTask.getState(), State.SUSPENDED);

        waitingTask.activate();
        assertEquals(waitingTask.getState(), State.READY);

        waitingTask.start();
        assertEquals(waitingTask.getState(), State.RUNNING);

        waitingTask.waitMove();
        assertEquals(waitingTask.getState(), State.WAITING);

        scheduler.scheduleTask(waitingTask);

        assertEquals(waitingTask.getState(), State.READY);
        assertEquals(scheduler.getSize(), 1);
    }

    /**
     * if queue is full, when scheduleTask() by Scheduler, FullQueueException will be thrown
     */
    @Test
    void testFullQueueExceptionThrown() {
        testUtils.fullQueue(taskGenerator);
        assertThrows(FullQueueException.class, () -> scheduler.scheduleTask(basicTask));
    }

    /**
     * if getTask() by Scheduler with queue (has at least 1 task), queue decreases in size by 1 task
     */
    @Test
    void testGetTask() {
        assertTrue(scheduler.isEmpty());

        scheduler.scheduleTask(basicTask);
        assertEquals(scheduler.getSize(), 1);

        scheduler.getTask();
        assertTrue(scheduler.isEmpty());
    }

    /**
     * if getTask() by Scheduler with empty queue, EmptyQueueException will be thrown
     */
    @Test
    void testGetTaskEmptyQueueExceptionThrown() {
        assertTrue(scheduler.isEmpty());
        assertThrows(EmptyQueueException.class, () -> scheduler.getTask());
    }

    /**
     * currentMaxPriority() returns max priority in Scheduler's queue
     */
    @Test
    void testCurrentMaxPriority() {
        var lowPriorityTask = new BasicTask(Priority.LOW, jobGenerator.generate());
        scheduler.scheduleTask(lowPriorityTask);

        var normPriorityTask = new BasicTask(Priority.NORMAL, jobGenerator.generate());
        scheduler.scheduleTask(normPriorityTask);

        var highPriorityTask = new BasicTask(Priority.HIGH, jobGenerator.generate());
        scheduler.scheduleTask(highPriorityTask);

        var highestPriorityTask = new BasicTask(Priority.HIGHEST, jobGenerator.generate());
        scheduler.scheduleTask(highestPriorityTask);

        assertEquals(scheduler.currentMaxPriority(), Priority.HIGHEST);
    }

    /**
     * currentMaxPriority() in Scheduler throws EmptyQueueException, if queue is empty
     */
    @Test
    void testCurrentMaxPriorityEmptyQueueExceptionThrown() {
        assertTrue(scheduler.isEmpty());
        assertThrows(EmptyQueueException.class , () -> scheduler.currentMaxPriority());
    }

    /**
     * after stop() in Scheduler queue becomes empty
     */
    @Test
    void testStopScheduler() {
        assertTrue(scheduler.isEmpty());

        scheduler.scheduleTask(basicTask);
        assertEquals(scheduler.getSize(), 1);

        scheduler.stop();
        assertTrue(scheduler.isEmpty());
    }
}

