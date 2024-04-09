package org.testinglab;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var sys = new System();
        sys.start();

        var taskGenerator = new TaskGenerator(sys);
        var random = new Random();

        while (true) {
            taskGenerator.generate();
            Thread.sleep(random.nextInt(100));
        }
    }
}
