package org.testinglab.task;

import java.util.function.BooleanSupplier;

public class ExtendedTask extends Task {
    private final BooleanSupplier job;

    /**
     * @param priority
     * @param waitableJob job of the task; returns true, if need wait
     */
    public ExtendedTask(Priority priority, BooleanSupplier waitableJob) {
        super(priority);
        this.job = waitableJob;
    }

    protected State state = State.SUSPENDED;

    @Override
    public void run() {
        state = job.getAsBoolean() ? State.WAITING : State.SUSPENDED;
    }

    public void waitEvent() {
        throw new RuntimeException("WAIT EVENT");
    }

    final public void waitMove() {
        if (state != State.RUNNING) {
            throw new TaskException("Can not wait, when state is not " + State.RUNNING);
        }
        state = State.WAITING;
    }

    final public void release() {
        if (state != State.WAITING) {
            throw new TaskException("Can not release, when state is not " + State.WAITING);
        }
        state = State.READY;
    }
}
