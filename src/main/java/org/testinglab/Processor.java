package org.testinglab;

import org.testinglab.task.Task;

import java.util.concurrent.*;

public class Processor {
    private final ExecutorService core = Executors.newSingleThreadExecutor();

    public Processor() {
    }

    private CompletableFuture<Void> currentJob;

//    public Future<Void> assign(Task task/*, Supplier<Void> onExecutionFinished*/) {
//        if (currentJob != null) throw new IllegalStateException("Can't take new task while last one still working");
//        currentJob = CompletableFuture.runAsync(task::start, core).thenAccept((Void) -> {
//            currentJob = null;
////            onExecutionFinished.get();
//        });
//        return core.submit(() -> currentJob.get());
//    }

    public Future<Void> assign(Task task/*, Supplier<Void> onExecutionFinished*/) {
        if (currentJob != null) throw new IllegalStateException("Can't take new task while last one still working");
        currentJob = CompletableFuture.runAsync(task::start, core);
        return core.submit(() -> {
            try {
                currentJob.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            currentJob = null; return null; });
    }

    public void cancel() {
        currentJob.cancel(true);
        currentJob = null;
    }

    public void stop() {
        core.shutdownNow();
    }
}
