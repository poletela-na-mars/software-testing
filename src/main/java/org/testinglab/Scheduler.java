package org.testinglab;

import org.testinglab.task.Priority;
import org.testinglab.task.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Scheduler {
    public final int maxReadyTasks;
    public Scheduler(int maxReadyTasks) {
        this.maxReadyTasks = maxReadyTasks;
    }

    public Scheduler() {
        this(DEFAULT_MAX_READY_TASKS_COUNT);
    }

    public Task getTask(Consumer<Task> onTask) {
        // if queue is empty, then subscribe on onTask, and call it after there is a task in a queue
        // 1-time subscription
        throw new RuntimeException("TODO");
    }

    public void scheduleTask(Task task, Consumer<Task> onTaskScheduled) {
        throw new RuntimeException("TODO");
    }

    public void subscribeOnHigherPriorityTask(Priority than, Consumer<Task> onHigherPriority) {
        throw new RuntimeException("TODO");
    }


    public void stop() {
        throw new RuntimeException("TODO: free queue");
    }

    public final static int DEFAULT_MAX_READY_TASKS_COUNT = 5;
}
