package com.lab;

import java.util.Random;

public class RandomGeneratorTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("RandomGenerator Test Suite - JUnit Style");
        System.out.println("=".repeat(60));
        System.out.println();

        runAllTests();

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
        System.out.println("=".repeat(60));

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runAllTests() {
        randomGeneratorBasicTests();
        randomGeneratorDegenerateTests();
        randomGeneratorValueArrayTests();
        randomGeneratorStressTests();
    }

    // Basic tests
    private static void randomGeneratorBasicTests() {
        System.out.println("--- Basic Random Tree Tests ---");

        test("Random: generateRandomTree(0) returns empty tree", () -> {
            Tree23 tree = RandomGenerator.generateRandomTree(0);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(-1, tree.getSmallestLeaf());
        });

        test("Random: generateRandomTree(1) returns valid tree", () -> {
            Tree23 tree = RandomGenerator.generateRandomTree(1);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertNotEquals(-1, tree.getSmallestLeaf());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
        });

        test("Random: generateRandomTree(100) builds valid tree", () -> {
            Tree23 tree = RandomGenerator.generateRandomTree(100);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(99.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Random: generateRandomTree(50) with custom Random", () -> {
            Tree23 tree = RandomGenerator.generateRandomTree(50, new Random(123));
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(49.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Random: throws on negative n", () -> {
            assertThrows(() -> RandomGenerator.generateRandomTree(-1));
        });
    }

    // Degenerate tests
    private static void randomGeneratorDegenerateTests() {
        System.out.println("--- Degenerate Tree Tests ---");

        test("Degenerate: generateDegenerateTree(0) returns empty", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(0);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(-1, tree.getSmallestLeaf());
        });

        test("Degenerate: generateDegenerateTree(1) returns valid tree", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(1);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
        });

        test("Degenerate: generateDegenerateTree(100) builds valid tree", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(100);
            assertNotNull(tree);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(99.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Degenerate: sorted order inserts create deep tree", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(5, false);
            int[] depths = tree.depths();
            assertTrue(tree.invariant());
            assertTrue(depths[1] > 1, "Degenerate tree depth should be > 1");
        });

        test("Degenerate: reverse order inserts", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(5, true);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(4.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Degenerate: throws on negative n", () -> {
            assertThrows(() -> RandomGenerator.generateDegenerateTree(-1));
        });
    }

    // Value array tests
    private static void randomGeneratorValueArrayTests() {
        System.out.println("--- Value Array Tests ---");

        test("Values: generateRandomValues returns correct values", () -> {
            int[] values = RandomGenerator.generateRandomValues(10);
            assertEquals(10, values.length);
            boolean[] found = new boolean[10];
            for (int v : values) {
                found[v] = true;
            }
            for (int i = 0; i < 10; i++) {
                assertTrue(found[i], "Value " + i + " should be found");
            }
        });

        test("Values: generateRandomValues(0) returns empty array", () -> {
            int[] values = RandomGenerator.generateRandomValues(0);
            assertNotNull(values);
            assertEquals(0, values.length);
        });

        test("Values: generateRandomValues with custom Random", () -> {
            int[] values = RandomGenerator.generateRandomValues(10, new Random(42));
            assertEquals(10, values.length);
            boolean[] found = new boolean[10];
            for (int v : values) {
                found[v] = true;
            }
            for (int i = 0; i < 10; i++) {
                assertTrue(found[i], "Value " + i + " should be found");
            }
        });

        test("Values: throws on negative n", () -> {
            assertThrows(() -> RandomGenerator.generateRandomValues(-1));
        });
    }

    // Stress tests
    private static void randomGeneratorStressTests() {
        System.out.println("--- Stress Tests ---");

        test("Stress: Large random tree (10000 elements)", () -> {
            Tree23 tree = RandomGenerator.generateRandomTree(10000);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(9999.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Stress: Large degenerate tree (10000 elements)", () -> {
            Tree23 tree = RandomGenerator.generateDegenerateTree(10000);
            assertTrue(tree.invariant());
            assertEquals(0.0, tree.getValue(tree.getSmallestLeaf()).ExtractValue(), 0.0);
            assertEquals(9999.0, tree.getValue(tree.getLargestLeaf()).ExtractValue(), 0.0);
        });

        test("Stress: Multiple random trees with different seeds", () -> {
            for (int seed = 0; seed < 5; seed++) {
                Tree23 tree = RandomGenerator.generateRandomTree(1000, new Random(seed));
                assertTrue(tree.invariant());
                assertNotEquals(-1, tree.find(new Element(0)));
                assertNotEquals(-1, tree.find(new Element(999)));
            }
        });
    }

    // Helper methods
    private static void test(String name, Runnable assertion) {
        try {
            assertion.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (AssertionError e) {
            failed++;
            System.out.println("[FAIL] " + name);
            System.out.println("       " + e.getMessage());
        } catch (Exception e) {
            failed++;
            System.out.println("[FAIL] " + name);
            System.out.println("       Exception: " + e.getMessage());
        }
    }

    // Assertion helpers
    private static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(String.format("Expected: %.2f, Actual: %.2f", expected, actual));
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(String.format("Expected: %d, Actual: %d", expected, actual));
        }
    }

    private static void assertNotEquals(int expected, int actual) {
        if (expected == actual) {
            throw new AssertionError(String.format("Expected not: %d, Actual: %d", expected, actual));
        }
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected non-null but was null");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(Runnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected exception but none was thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
