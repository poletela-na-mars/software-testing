package org.testinglab;

import org.testinglab.task.TaskJob;

import java.util.Random;
import java.util.function.BooleanSupplier;

public class JobGenerator {
    private final Random random = new Random();
    private TaskJob getJob(int ms) {
        return () -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {}
        };
    }

    public TaskJob generate() {
        return getJob(random.nextInt(MAX_MS_EXECUTABLE_JOB) + 1);
    }

    public BooleanSupplier generateWaitable() {
        return () -> {
          generate();
          return random.nextBoolean();
        };
    }

    public final static int MAX_MS_EXECUTABLE_JOB = 1_000;
}
