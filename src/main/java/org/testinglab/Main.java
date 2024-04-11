package org.testinglab;

import org.testinglab.scheduler.FullQueueException;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var sys = new System();
        sys.start();

        var taskGenerator = new TaskGenerator(sys);
        var random = new Random(100);

        while (true) {
            try {
                taskGenerator.generate();
            } catch (FullQueueException e) {
                Thread.sleep(100);
            }
            Thread.sleep(random.nextInt(100));
        }
    }
}
