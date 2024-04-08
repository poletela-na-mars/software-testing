package org.testinglab;

import org.testinglab.scheduler.Scheduler;
import org.testinglab.task.*;

import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;

import static java.lang.System.out;

public class System {
    private final Processor processor;
    private final Scheduler scheduler;
    private boolean isClosed = false;

    public System(Processor processor, Scheduler scheduler) {
        this.processor = processor;
        this.scheduler = scheduler;
    }

    public System() {
        this(new Processor(), new Scheduler());
    }

    public void start() {
        new Thread(() -> {
            outer: while (!isClosed) {
                var task = getTask();
                out.println("Task " + task + " received on execution by System");
                var job = processor.assign(task);
                while (!job.isDone()) {
//                    out.println("max: " + scheduler.currentMaxPriority().value);
//                    out.println("current: " + task.getPriority().value);
                    if (!scheduler.isEmpty() && scheduler.currentMaxPriority().value > task.getPriority().value) {
                        // TODO: maybe protect sync?
                        out.println("Preempt task " + task);
                        processor.cancel();
                        task.preempt();

                        new Thread(() -> {
                            scheduler.scheduleTask(task);
                        }).start();
                        continue outer;
                    }
                }

                try {
                    job.get();
                } catch (InterruptedException | ExecutionException e) {
                    out.println("1?");
                    e.printStackTrace();
                }

                if (task.getState() == State.WAITING) onTaskWaiting((ExtendedTask) task);
                else onTaskCompleted(task);
            }
        }).start();
    }

    private void onTaskWaiting(ExtendedTask task) {
        new Thread(() -> {
            task.waitEvent();
            schedule(task);
        }).start();
    }

    private void onTaskCompleted(Task task) {
        out.println("Task " + task + " completed");
//        start();
    }

    public void newBasicTask(Priority priority, TaskJob job) {
        var task = new BasicTask(priority, job);

        schedule(task);
    }

    public void newExtendedTask(Priority priority, BooleanSupplier waitableJob) {
        var task = new ExtendedTask(priority, waitableJob);
        schedule(task);
    }

    public void stop() {
        if (isClosed) return;
        isClosed = true;
        scheduler.stop();
        processor.stop();
    }

    private void schedule(Task task) {
        while (scheduler.isFull()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        scheduler.scheduleTask(task);
    }

    private Task getTask() {
        while (scheduler.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return scheduler.getTask();
    }
}
