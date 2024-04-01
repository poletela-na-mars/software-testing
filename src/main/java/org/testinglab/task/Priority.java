package org.testinglab.task;


public enum Priority {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    IMMEDIATE(4);

    public final int value;

    private Priority(int value) {
        this.value = value;
    }
}

/*
public class PriorityLol {
    final public int MIN_PRIORITY = 0;
    final public int MAX_PRIORITY = 3;

    public int priority;

    public Priority(int priority) {
        this.priority = priority;
        if (MIN_PRIORITY > priority || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("Priority number should be in valid range: " +
                    MIN_PRIORITY + "-" + MAX_PRIORITY + ". " + priority + " is NOT allowed.");
        }
    }

    @Override
    public String toString() {
        return "" + priority;
    }
}
*/
