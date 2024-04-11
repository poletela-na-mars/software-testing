package org.testinglab;

import static org.testinglab.scheduler.Scheduler.DEFAULT_MAX_READY_TASKS_COUNT;

public class TestUtils {
    public void fullQueue(TaskGenerator taskGenerator) {
        int i = DEFAULT_MAX_READY_TASKS_COUNT;
        while (i > 0) {
            taskGenerator.generate();
            i--;
        }
    }
}
