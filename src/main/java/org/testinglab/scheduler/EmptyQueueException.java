package org.testinglab.scheduler;

public final class EmptyQueueException extends SchedulingException {
    public EmptyQueueException(String msg) {
        super(msg);
    }
}
