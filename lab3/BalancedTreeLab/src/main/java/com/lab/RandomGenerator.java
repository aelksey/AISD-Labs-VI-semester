package com.lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomGenerator {

    private static final Random DEFAULT_RANDOM = new Random(42);

    public static Tree23 generateRandomTree(int n) {
        return generateRandomTree(n, DEFAULT_RANDOM);
    }

    public static Tree23 generateRandomTree(int n, Random random) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        if (n == 0) {
            return new Tree23();
        }

        List<Integer> values = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            values.add(i);
        }
        Collections.shuffle(values, random);

        Tree23 tree = new Tree23();
        for (int value : values) {
            tree.insert(new Element(value));
        }
        return tree;
    }

    public static Tree23 generateDegenerateTree(int n) {
        return generateDegenerateTree(n, false);
    }

    public static Tree23 generateDegenerateTree(int n, boolean reverse) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        if (n == 0) {
            return new Tree23();
        }

        Tree23 tree = new Tree23();
        if (reverse) {
            for (int i = n - 1; i >= 0; i--) {
                tree.insert(new Element(i));
            }
        } else {
            for (int i = 0; i < n; i++) {
                tree.insert(new Element(i));
            }
        }
        return tree;
    }

    public static int[] generateRandomValues(int n) {
        return generateRandomValues(n, DEFAULT_RANDOM);
    }

    public static int[] generateRandomValues(int n, Random random) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        if (n == 0) {
            return new int[0];
        }

        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }
        return values;
    }
}