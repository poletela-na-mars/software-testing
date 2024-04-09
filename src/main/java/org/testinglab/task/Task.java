package org.testinglab.task;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task {
    final private Priority priority;
    final static private AtomicInteger initIdx = new AtomicInteger(0);
    private final int idx = initIdx.getAndIncrement();
    protected volatile State state = State.SUSPENDED;

    Task(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    abstract void run();

    final public void start() {
        if (state != State.READY) {
            throw new TaskException("Can not start, when state is not " + State.READY + ", but " + this);
        }
        state = State.RUNNING;
        run();
    }

    final public void activate() {
        if (state != State.SUSPENDED) {
            throw new TaskException("Can not activate, when state is not " + State.SUSPENDED);
        }
        state = State.READY;
    }

    final public void terminate() {
        if (state != State.RUNNING) {
            throw new TaskException("Can not terminate, when state is not " + State.RUNNING);
        }
        state = State.SUSPENDED;
    }

    final public void preempt() {
        if (state != State.RUNNING) {
            throw new TaskException("Can not preempt, when state is not " + State.RUNNING);
        }
        state = State.READY;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Task №" + idx + " with priority " + priority + " | " + state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx, priority, state);
    }
}
