package org.testinglab;

import org.testinglab.scheduler.Scheduler;
import org.testinglab.task.*;

import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;

import static java.lang.System.exit;
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
                out.println(task + " received on execution by System");
                out.flush();
                var job = processor.assign(task);
                inner: while (!job.isDone()) {
                    if (!scheduler.isEmpty() && scheduler.currentMaxPriority().value > task.getPriority().value) {
                        // TODO: maybe protect sync?
                        var preemptedTask = task;
                        out.println("Preempt task " + preemptedTask);
                        out.flush();
                        processor.cancel();
                        preemptedTask.preempt();

                        new Thread(() -> {
                            scheduler.scheduleTask(preemptedTask);
                        }, "Schedule on preempt thread").start();

                        continue outer;
                    }
                }

                try {
                    job.get();
                    processor.clear();
                } catch (InterruptedException | ExecutionException e) {
                    out.println(e);
                    out.flush();
                    e.printStackTrace();
                }

                if (task instanceof ExtendedTask && ((ExtendedTask) task).isWaitingNeed()) {
                    out.println("Wait event on + " + task);
                    onTaskWaiting((ExtendedTask) task);
                } else onTaskCompleted(task);
            }
        }, "OS thread").start();
    }

    private void onTaskWaiting(ExtendedTask task) {
        new Thread(() -> {
            task.waitMove();
            schedule(task);
        }, "Waiting Event thread").start();
    }

    private void onTaskCompleted(Task task) {
        task.terminate();
        out.println(task + " completed");
        java.lang.System.out.flush();
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
