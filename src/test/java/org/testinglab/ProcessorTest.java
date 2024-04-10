package org.testinglab;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.testinglab.scheduler.Scheduler;
import org.testinglab.task.BasicTask;
import org.testinglab.task.ExtendedTask;

import java.util.concurrent.RejectedExecutionException;

import static org.testinglab.JobGenerator.MAX_MS_EXECUTABLE_JOB;

public class ProcessorTest {

    Scheduler scheduler = new Scheduler();
    Processor processor = new Processor();
    System system = new System(processor, scheduler);

    TaskGenerator taskGenerator = new TaskGenerator(system);
    JobGenerator jobGenerator = new JobGenerator();

    BasicTask basicTask = new BasicTask(taskGenerator.randomPriority(), jobGenerator.generate());
    ExtendedTask extendedTask = new ExtendedTask(taskGenerator.randomPriority(), jobGenerator.generateWaitable());


    /**
     * after assign() basic task's job by Processor (without interrupting) will be done
     */
    @RepeatedTest(10)
    void testBasicTaskAssign() {
        var currentJob = processor.assign(basicTask);
        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB);
        } catch (InterruptedException ignored) {}
        assertTrue(currentJob.isDone());
    }

    /**
     * after assign() extended task's job by Processor (without interrupting) will be done
     */
    @RepeatedTest(10)
    void testExtendedTaskAssign() {
        var currentJob = processor.assign(extendedTask);
        try {
            Thread.sleep(MAX_MS_EXECUTABLE_JOB);
        } catch (InterruptedException ignored) {}
        assertTrue(currentJob.isDone());
    }

    /**
     * after clear() by Processor its currentJob becomes null
     */
    @Test
    void testClear() {
        processor.assign(basicTask);
        processor.clear();
        assertNull(processor.getCurrentJob());
    }

    /**
     * if Processor assign task, while another task is already in work, IllegalStateException will be thrown
     */
    @Test
    void testMultipleAssign() {
        processor.assign(basicTask);
        assertThrows(IllegalStateException.class, () -> processor.assign(basicTask));
    }

    /**
     * after cancel() by Processor its currentJob becomes null and Future result of this job - cancelled
     */
    @Test
    void testCancel() {
        var currentJob = processor.assign(basicTask);
        processor.cancel();
        assertNull(processor.getCurrentJob());
        assertTrue(currentJob.isCancelled());
    }

    /**
     * after stop() by Processor it can not assign tasks anymore, RejectedExecutionException will be thrown
     */
    @Test
    void testStop() {
        processor.stop();
        assertThrows(RejectedExecutionException.class, () -> processor.assign(basicTask));
    }
}
