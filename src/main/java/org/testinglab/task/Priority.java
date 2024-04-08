package org.testinglab.task;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum Priority {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    HIGHEST(3);

    public final int value;

    private Priority(int value) {
        this.value = value;
    }

    public static List<Priority> sortedValues() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt((Priority p) -> p.value)).toList();
    }

    public static List<Priority> sortedReversedValues() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt((Priority p) -> p.value).reversed()).toList();
    }
}
