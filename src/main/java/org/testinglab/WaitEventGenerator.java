package org.testinglab;

import java.util.Random;

public class WaitEventGenerator {
    private final Random random = new Random();

    public int generate() {
        return random.nextInt(100) + 1;
    }
}
