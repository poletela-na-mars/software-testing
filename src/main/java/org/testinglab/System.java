package org.testinglab;

import org.testinglab.task.*;

import java.util.function.BooleanSupplier;

public class System {
    private final Processor processor;
    private final Scheduler scheduler;

    public System(Processor processor, Scheduler scheduler) {
        this.processor = processor;
        this.scheduler = scheduler;
    }

    public System() {
        this(new Processor(), new Scheduler());
    }

    public void start() {
        scheduler.getTask(task -> processor.assign(task, () -> {
            if (task.getState() == State.WAITING) onTaskWaiting((ExtendedTask) task);
            else onTaskCompleted(task);
            onTaskCompleted(task);
            return null;
        }));
    }

    private void onTaskWaiting(ExtendedTask task) {
        new Thread(() -> {
            task.start(); // wait event
            scheduler.scheduleTask(task, _t -> task.release());
        }).start();
        start();
    }

    private void onTaskCompleted(Task task) {
        start();
    }

    private void onTaskScheduled(Task task) {
        task.activate();
    }

    public void newBasicTask(Priority priority, TaskJob job) {
        var task = new BasicTask(priority, job);
        scheduler.scheduleTask(task, this::onTaskScheduled);
    }

    public void newExtendedTask(Priority priority, BooleanSupplier waitableJob) {
        var task = new ExtendedTask(priority, waitableJob);
        scheduler.scheduleTask(task, this::onTaskScheduled);
    }

    public void stop() {
        scheduler.stop();
        processor.stop();
    }
}
