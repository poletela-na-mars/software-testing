package org.testinglab;

import org.testinglab.task.Task;

import java.util.concurrent.*;

public class Processor {
    private final ExecutorService core = Executors.newSingleThreadExecutor((r) -> new Thread(r, "Core thread"));

    public Processor() {}

    private Future<Void> currentJob;

    public Future<Void> assign(Task task) {
        if (currentJob != null) throw new IllegalStateException("Can't take new task while last one still working");
        currentJob = core.submit(() -> { task.start(); return null; });
        return currentJob;
    }

    public void clear() {
        currentJob = null;
    }

    public void cancel() {
        currentJob.cancel(true);
        currentJob = null;
    }

    public void stop() {
        core.shutdownNow();
    }
}
