package org.testinglab.scheduler;

import org.testinglab.task.ExtendedTask;
import org.testinglab.task.Priority;
import org.testinglab.task.State;
import org.testinglab.task.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Scheduler {
    public final int maxReadyTasks;

    private final AtomicInteger size = new AtomicInteger(0);

    private final HashMap<Priority, LinkedList<Task>> queue = new HashMap<>();

    public Scheduler(int maxReadyTasks) {
        this.maxReadyTasks = maxReadyTasks;

        for (var priority: Priority.values()) {
            queue.put(priority, new LinkedList<>());
        }
    }

    public Scheduler() {
        this(DEFAULT_MAX_READY_TASKS_COUNT);
    }

    public boolean isEmpty() {
        return size.get() == 0;
    }

    public boolean isFull() {
        return size.get() == maxReadyTasks;
    }

    public Priority currentMaxPriority() {
        if (isEmpty()) throw new EmptyQueueException("");
        synchronized (this) {
            for (var p : Priority.sortedReversedValues()) {
                if (!queue.get(p).isEmpty()) return p;
            }
        }
        throw new IllegalStateException("Unreachable state");
    }

    public Task getTask() throws SchedulingException {
        if (isEmpty()) throw new EmptyQueueException("Lack of tasks in queue");
        synchronized (this) {
            for (var priority : Priority.sortedReversedValues()) {
                if (!queue.get(priority).isEmpty()) {
                    size.decrementAndGet();
                    return queue.get(priority).pop();
                }
            }
        }

        throw new IllegalStateException("Unreachable state");
    }

    public void scheduleTask(Task task) throws SchedulingException {
        if (isFull()) throw new FullQueueException("Lack of tasks in queue");

        if (task.getState() == State.WAITING) ((ExtendedTask) task).release();
        else if (task.getState() == State.SUSPENDED) task.activate();

        assert task.getState() == State.READY;
        synchronized (this) {
            size.incrementAndGet();
            queue.get(task.getPriority()).push(task);
            System.out.println("Scheduled task: " + task);
        }
    }

//    public void subscribeOnHigherPriorityTask(Priority than, Consumer<Task> onHigherPriority) {
//        throw new RuntimeException("TODO");
//    }


    public void stop() {
        throw new RuntimeException("TODO: free queue");
    }

    public final static int DEFAULT_MAX_READY_TASKS_COUNT = 5;
}
