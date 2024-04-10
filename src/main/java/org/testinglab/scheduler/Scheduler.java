package org.testinglab.scheduler;

import org.testinglab.task.ExtendedTask;
import org.testinglab.task.Priority;
import org.testinglab.task.State;
import org.testinglab.task.Task;

import java.util.*;

public class Scheduler {
    public final int maxReadyTasks;
    private volatile int size = 0;

    private final HashMap<Priority, LinkedList<Task>> queue = new HashMap<>();

    public Scheduler(int maxReadyTasks) {
        this.maxReadyTasks = maxReadyTasks;

        for (var priority : Priority.values()) {
            queue.put(priority, new LinkedList<>());
        }
    }

    public Scheduler() {
        this(DEFAULT_MAX_READY_TASKS_COUNT);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == maxReadyTasks;
    }

    public int getSize() {
        return size;
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
                    size--;
                    return queue.get(priority).pop();
                }
            }
        }

        throw new IllegalStateException("Unreachable state");
    }

    public void scheduleTask(Task task) throws SchedulingException {
        if (isFull()) throw new FullQueueException("Queue is full");

        if (task.getState() == State.WAITING) ((ExtendedTask) task).release();
        else if (task.getState() == State.SUSPENDED) task.activate();

        assert task.getState() == State.READY;
        synchronized (this) {
            size++;
            var taskPriority = task.getPriority();
            queue.get(taskPriority).push(task);
            System.out.println("Scheduled task: " + task + " from Queue №" + taskPriority.value);
            System.out.flush();
        }
    }

    public void stop() {
        queue.clear();
    }

    public final static int DEFAULT_MAX_READY_TASKS_COUNT = 5;
}
