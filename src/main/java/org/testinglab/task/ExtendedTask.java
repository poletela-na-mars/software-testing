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

    private boolean needWaiting = false;

    public boolean isWaitingNeed() {
        return needWaiting;
    }

    @Override
    public void run() {
        needWaiting = false;
        needWaiting = job.getAsBoolean();
    }

    public void waitEvent() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

    final public void waitMove() {
        if (state != State.RUNNING) {
            throw new TaskException("Can not wait, when state is not " + State.RUNNING + ", but " + this);
        }
        if (!needWaiting) throw new TaskException("No need waiting");
        state = State.WAITING;
        needWaiting = false;
        waitEvent();
    }

    final public void release() {
        if (state != State.WAITING) {
            throw new TaskException("Can not release, when state is not " + State.WAITING);
        }
        needWaiting = false;

        state = State.READY;
    }
}
