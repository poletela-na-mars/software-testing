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
    private volatile boolean isPreempting = false;
    private volatile boolean isWaiting = false;

    public System(Processor processor, Scheduler scheduler) {
        this.processor = processor;
        this.scheduler = scheduler;
    }

    public System() {
        this(new Processor(), new Scheduler());
    }

    public void start() {
        new Thread(() -> {
            Task task = null;
            outer: while (!isClosed) {
                if (isPreempting) {
                    synchronized (scheduler) {
                        var preemptedTask = task;
                        out.println("Preempt task " + preemptedTask);
                        out.flush();
                        processor.cancel();
                        preemptedTask.preempt();
                        task = scheduler.getTask();
                        scheduler.scheduleTask(preemptedTask);
                        isPreempting = false;
                    }
                } else if (isWaiting) {
                    while (scheduler.isEmpty()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    synchronized (scheduler) {
                        var waitingTask = (ExtendedTask) task;
                        out.println("Wait event on + " + waitingTask);
                        task = scheduler.getTask();
                        waitingTask.waitMove();
                        scheduler.scheduleTask(waitingTask);
                        isWaiting = false;
                    }
                } else {
                    task = getTask();
                }

                out.println(task + " received on execution by System");
                out.flush();
                var job = processor.assign(task);
                inner: while (!job.isDone()) {
                    if (!scheduler.isEmpty() && scheduler.currentMaxPriority().value > task.getPriority().value) {
                        isPreempting = true;
                        // wait if task is moving from ready to running right now
                        while (task.getState() == State.READY);
                        continue outer;
                    }
                }

                try {
                    job.get();
                    processor.clear();
                } catch (InterruptedException | ExecutionException e) {
                    out.flush();
                    e.printStackTrace();
                }

                if (task instanceof ExtendedTask && ((ExtendedTask) task).isWaitingNeed()) {
                    isWaiting = true;
                } else onTaskCompleted(task);
            }
        }, "OS thread").start();
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
        while (/*isPreempting || isWaiting ||*/ scheduler.isFull()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }

        synchronized (scheduler) {
            scheduler.scheduleTask(task);
        }
    }

    private Task getTask() {
        while (scheduler.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        synchronized (scheduler) {
            return scheduler.getTask();
        }
    }
}
