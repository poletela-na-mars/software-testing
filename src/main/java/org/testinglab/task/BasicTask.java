package org.testinglab.task;

public class BasicTask extends Task {
    private final TaskJob job;
    public BasicTask(Priority priority, TaskJob job) {
        super(priority);
        this.job = job;
    }

    @Override
    public void run() {
        job.execute();
    }
}
