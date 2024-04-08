package org.testinglab.scheduler;

public final class FullQueueException extends SchedulingException {
    public FullQueueException(String msg) {
        super(msg);
    }
}
