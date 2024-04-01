package org.testinglab.task;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BasicTask extends Task {
    private final TaskJob job;
    public BasicTask(Priority priority, TaskJob job) {
        super(priority);
        this.job = job;
    }

    @Override
    public void run() {
        job.execute();
        state = State.SUSPENDED;
    }

}
