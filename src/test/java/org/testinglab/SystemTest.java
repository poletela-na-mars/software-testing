package org.testinglab;

import org.junit.jupiter.api.Test;
import org.testinglab.scheduler.Scheduler;
import org.testinglab.task.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testinglab.JobGenerator.MAX_MS_EXECUTABLE_JOB;

public class SystemTest {

    Scheduler scheduler = new Scheduler();
    Processor processor = new Processor();
    System system = new System(processor, scheduler);

    TaskGenerator taskGenerator = new TaskGenerator(system);
    JobGenerator jobGenerator = new JobGenerator();

    TestUtils testUtils = new TestUtils();

    BasicTask basicTask = new BasicTask(taskGenerator.randomPriority(), jobGenerator.generate());

    /**
     * if start() by System for SUSPENDED task, task's job will be done and it will become SUSPENDED again
     */
    @Test
    void testOnStartSuspendedTask() {
        assertTrue(scheduler.isEmpty());
        assertEquals(basicTask.getState(), State.SUSPENDED);

        scheduler.scheduleTask(basicTask);
        assertEquals(scheduler.getSize(), 1);

        system.start();
        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB);
        } catch (InterruptedException ignored) {
        }

        assertEquals(basicTask.getState(), State.SUSPENDED);
        assertNull(processor.getCurrentJob());
    }

    private TaskJob generateLongTaskJob() {
        return () -> {
            try {
                Thread.sleep(MAX_MS_EXECUTABLE_JOB);
            } catch (InterruptedException e) {}
        };
    }

    /**
     * if start() by System for task, which is preempting, task with higher priority will be finished sooner;
     * only after it, preempted task will try to start again
     */
    @Test
    void testOnStartPreemptingTask() {
        BasicTask highPriorityTask = new BasicTask(Priority.HIGH, jobGenerator.generate());
        BasicTask lowPriorityTask = new BasicTask(Priority.LOW, generateLongTaskJob());

        assertTrue(scheduler.isEmpty());
        assertEquals(lowPriorityTask.getState(), State.SUSPENDED);

        // firstly, schedule low priority tak
        scheduler.scheduleTask(lowPriorityTask);
        assertEquals(scheduler.getSize(), 1);
        assertEquals(lowPriorityTask.getState(), State.READY);

        system.start();
        // do not let execute completely
        // secondly, schedule high priority task
        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB / 2);
        } catch (InterruptedException e) {}

        scheduler.scheduleTask(highPriorityTask);
        assertEquals(highPriorityTask.getState(), State.READY);

        // high priority task preempted
        while (!system.isPreempting) {}
        assertTrue(system.isPreempting);

        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB);
        } catch (InterruptedException ignored) {
        }

        // task with higher priority executes first of all
        assertEquals(highPriorityTask.getState(), State.SUSPENDED);
    }

    /**
     * if start() by System for task, which is waiting, other tasks can be served meanwhile
     */
    @Test
    void testOnStartWaitingTask() {
        ExtendedTask waitingTask = new ExtendedTask(Priority.LOW, () -> true);
        BasicTask task = new BasicTask(Priority.LOW, jobGenerator.generate());

        assertTrue(scheduler.isEmpty());
        assertEquals(waitingTask.getState(), State.SUSPENDED);

        system.start();

        scheduler.scheduleTask(waitingTask);
        assertEquals(scheduler.getSize(), 1);
        assertEquals(waitingTask.getState(), State.READY);

        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB);
        } catch (InterruptedException ignored) {
        }

        // waitingTask need wait
        while (!system.isWaiting) {}
        assertTrue(system.isWaiting);

        // can schedule another task for now
        scheduler.scheduleTask(task);
        assertEquals(task.getState(), State.READY);

        while (task.getState() != State.SUSPENDED);
        assertEquals(task.getState(), State.SUSPENDED);
    }

    /**
     * if onTaskCompleted() in System, task will be terminated (RUNNING -> SUSPENDED)
     */
    @Test
    void testOnTaskCompleted() {
        BasicTask runningTask = basicTask;
        assertEquals(runningTask.getState(), State.SUSPENDED);

        runningTask.activate();
        assertEquals(runningTask.getState(), State.READY);

        runningTask.start();
        assertEquals(runningTask.getState(), State.RUNNING);

        system.onTaskCompleted(runningTask);
        assertEquals(runningTask.getState(), State.SUSPENDED);
    }

    /**
     * if System stop(), it won't serve tasks cause Scheduler and Processor were stopped too
     */
    @Test
    void testStop() {
        assertTrue(scheduler.isEmpty());
        system.start();

        system.stop();

        system.start();

        assertTrue(system.isClosed);
        // can not fill queue cause of all entities stopped working
        assertThrows(NullPointerException.class, () -> testUtils.fullQueue(taskGenerator));
    }
}
