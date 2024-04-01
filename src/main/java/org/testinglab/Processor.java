package org.testinglab;

import org.testinglab.task.Task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class Processor {
    private final ExecutorService core = Executors.newSingleThreadExecutor();

    public Processor() {
    }

    private CompletableFuture<Void> currentJob;

    public void assign(Task task, Supplier<Void> onExecutionFinished) {
        if (currentJob != null) throw new IllegalStateException("Can't take new task while last one still working");
        currentJob = CompletableFuture.runAsync(task::start, core).thenAccept((Void) -> {
            currentJob = null;
            onExecutionFinished.get();
        });
        core.submit(() -> currentJob.get());
    }

    public void cancel() {
        currentJob.cancel(true);
    }

    public void stop() {
        core.shutdownNow();
    }
}
