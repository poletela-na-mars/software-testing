package org.testinglab;

import org.testinglab.task.Priority;
import org.testinglab.task.TaskJob;

import java.util.Random;

public class TaskGenerator {
    private final Random random = new Random(100);
    private final JobGenerator jobGenerator = new JobGenerator();
    private final System system;

    public TaskGenerator(System system) {
        this.system = system;
    }

    public Priority randomPriority() {
        int x = random.nextInt(Priority.values().length);
        return Priority.values()[x];
    }

    public void generate() {
        TaskJob taskJob = jobGenerator.generate();
        Priority priority = randomPriority();
        var isBasicTask = random.nextBoolean();

        if (isBasicTask) {
            system.newBasicTask(priority, taskJob);
        } else {
            system.newExtendedTask(priority, jobGenerator.generateWaitable());
        }
    }
}
