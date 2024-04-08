package org.testinglab;

import org.testinglab.task.Priority;
import org.testinglab.task.TaskJob;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var sys = new System();
        sys.start();

        TaskJob job = () -> {
            try {
//                long doing = Integer.MAX_VALUE;
//                while (doing > 0) doing--;
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
//            } catch (Exception e) {}
        };
        sys.newBasicTask(Priority.NORMAL, job);
        sys.newBasicTask(Priority.LOW, job);
        sys.newBasicTask(Priority.LOW, job);
        Thread.sleep(100);
        sys.newBasicTask(Priority.HIGHEST, job);
    }
}
