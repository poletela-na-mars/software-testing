package org.testinglab;

import java.util.Random;

public class WaitEventGenerator {
    private final Random random = new Random(100);

    public int generate() {
        return random.nextInt(100) + 1;
    }
}
