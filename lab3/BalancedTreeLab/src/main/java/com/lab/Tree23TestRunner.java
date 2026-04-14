package com.lab;

import java.util.Random;

public class Tree23TestRunner {

    private static int passed = 0;
    private static int failed = 0;
    private static String currentTest = "";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Tree23 Test Suite - JUnit Style");
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
        emptyTreeOperations();
        singleElementTree();
        twoElementTree();
        deleteEdgeCases();
        insertEdgeCases();
        circularLinkedList();
        findFirstLargerEdgeCases();
        changeValueTests();
        previousNextEdgeCases();
        depthTests();
        stressTests();
        originalTests();
    }

    // Empty tree tests
    private static void emptyTreeOperations() {
        Tree23 tree = new Tree23();

        test("Empty: Find returns -1", () -> {
            assertEquals(-1, tree.find(new Element(0)));
        });

        test("Empty: Delete does not throw", () -> {
            assertDoesNotThrow(() -> tree.delete(new Element(0)));
        });

        test("Empty: GetSmallestLeaf returns -1", () -> {
            assertEquals(-1, tree.getSmallestLeaf());
        });

        test("Empty: GetLargestLeaf returns -1", () -> {
            assertEquals(-1, tree.getLargestLeaf());
        });

        test("Empty: FindFirstLargerLeaf returns -1", () -> {
            assertEquals(-1, tree.findFirstLargerLeaf(5.0));
        });

        test("Empty: Previous returns -1", () -> {
            assertEquals(-1, tree.previous(0));
        });

        test("Empty: Next returns -1", () -> {
            assertEquals(-1, tree.next(0));
        });

        test("Empty: Invariant returns true", () -> {
            assertTrue(tree.invariant());
        });

        test("Empty: Depths returns [0, 0]", () -> {
            int[] depths = tree.depths();
            assertEquals(0, depths[0]);
            assertEquals(0, depths[1]);
        });
    }

    // Single element tests
    private static void singleElementTree() {
        Tree23 tree = new Tree23();
        tree.insert(new Element(42));

        test("Single: Find returns correct element", () -> {
            int idx = tree.find(new Element(42));
            assertNotEquals(-1, idx);
            assertEquals(42.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("Single: Find returns -1 for non-existent", () -> {
            assertEquals(-1, tree.find(new Element(0)));
        });

        test("Single: GetSmallestLeaf returns the only element", () -> {
            int idx = tree.getSmallestLeaf();
            assertNotEquals(-1, idx);
            assertEquals(42.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("Single: GetLargestLeaf returns the only element", () -> {
            int idx = tree.getLargestLeaf();
            assertNotEquals(-1, idx);
            assertEquals(42.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("Single: Previous returns self (circular)", () -> {
            int idx = tree.find(new Element(42));
            int prev = tree.previous(idx);
            assertEquals(idx, prev);
        });

        test("Single: Next returns self (circular)", () -> {
            int idx = tree.find(new Element(42));
            int next = tree.next(idx);
            assertEquals(idx, next);
        });

        test("Single: Delete makes tree empty", () -> {
            tree.delete(new Element(42));
            assertEquals(-1, tree.find(new Element(42)));
            assertTrue(tree.invariant());
        });

        test("Single: Invariant holds", () -> {
            Tree23 t = new Tree23();
            t.insert(new Element(42));
            assertTrue(t.invariant());
        });
    }

    // Two element tests
    private static void twoElementTree() {
        Tree23 tree = new Tree23();
        tree.insert(new Element(10));
        tree.insert(new Element(20));

        test("Two: Find returns correct elements", () -> {
            assertNotEquals(-1, tree.find(new Element(10)));
            assertNotEquals(-1, tree.find(new Element(20)));
        });

        test("Two: GetSmallestLeaf returns 10", () -> {
            int idx = tree.getSmallestLeaf();
            assertEquals(10.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("Two: GetLargestLeaf returns 20", () -> {
            int idx = tree.getLargestLeaf();
            assertEquals(20.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("Two: Next/Previous link correctly", () -> {
            int idx10 = tree.find(new Element(10));
            int idx20 = tree.find(new Element(20));
            assertEquals(idx20, tree.next(idx10));
            assertEquals(idx10, tree.previous(idx20));
        });

        test("Two: Invariant holds", () -> {
            assertTrue(tree.invariant());
        });
    }

    // Delete edge cases
    private static void deleteEdgeCases() {
        Tree23 tree = new Tree23();
        for (int i = 0; i < 10; i++) {
            tree.insert(new Element(i));
        }

        test("Delete: Smallest element (0)", () -> {
            Tree23 t = createTree(0, 9);
            t.delete(new Element(0));
            assertEquals(-1, t.find(new Element(0)));
            assertTrue(t.invariant());
            int smallest = t.getSmallestLeaf();
            assertEquals(1.0, t.getValue(smallest).ExtractValue(), 0.0);
        });

        test("Delete: Largest element (9)", () -> {
            Tree23 t = createTree(0, 9);
            t.delete(new Element(9));
            assertEquals(-1, t.find(new Element(9)));
            assertTrue(t.invariant());
            int largest = t.getLargestLeaf();
            assertEquals(8.0, t.getValue(largest).ExtractValue(), 0.0);
        });

        test("Delete: Middle element (5)", () -> {
            Tree23 t = createTree(0, 9);
            t.delete(new Element(5));
            assertEquals(-1, t.find(new Element(5)));
            assertTrue(t.invariant());
        });

        test("Delete: Non-existent element does not throw", () -> {
            Tree23 t = createTree(0, 9);
            assertDoesNotThrow(() -> t.delete(new Element(100)));
            assertTrue(t.invariant());
        });

        test("Delete: All elements in order", () -> {
            Tree23 t = createTree(0, 9);
            for (int i = 0; i < 10; i++) {
                t.delete(new Element(i));
            }
            assertTrue(t.invariant());
            int[] depths = t.depths();
            assertEquals(0, depths[0]);
        });

        test("Delete: All elements in reverse", () -> {
            Tree23 t = createTree(0, 9);
            for (int i = 9; i >= 0; i--) {
                t.delete(new Element(i));
            }
            assertTrue(t.invariant());
            int[] depths = t.depths();
            assertEquals(0, depths[0]);
        });

        test("Delete: Every other element", () -> {
            Tree23 t = createTree(0, 9);
            for (int i = 0; i < 10; i += 2) {
                t.delete(new Element(i));
            }
            assertTrue(t.invariant());
            for (int i = 0; i < 10; i += 2) {
                assertEquals(-1, t.find(new Element(i)));
            }
            for (int i = 1; i < 10; i += 2) {
                assertNotEquals(-1, t.find(new Element(i)));
            }
        });
    }

    // Insert edge cases
    private static void insertEdgeCases() {
        test("Insert: Descending order", () -> {
            Tree23 t = new Tree23();
            for (int i = 20; i >= 0; i--) {
                t.insert(new Element(i));
            }
            for (int i = 0; i <= 20; i++) {
                assertNotEquals(-1, t.find(new Element(i)));
            }
            assertTrue(t.invariant());
        });

        test("Insert: Alternating order", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i < 20; i += 2) {
                t.insert(new Element(i));
            }
            for (int i = 1; i < 20; i += 2) {
                t.insert(new Element(i));
            }
            for (int i = 0; i < 20; i++) {
                assertNotEquals(-1, t.find(new Element(i)));
            }
            assertTrue(t.invariant());
        });

        test("Insert: Large number of elements", () -> {
            Tree23 t = new Tree23();
            int maxN = 10000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i));
            }
            for (int i = 0; i < maxN; i++) {
                assertNotEquals(-1, t.find(new Element(i)));
            }
            assertTrue(t.invariant());
        });
    }

    // Circular linked list tets
    private static void circularLinkedList() {
        Tree23 tree = new Tree23();
        for (int i = 0; i < 5; i++) {
            tree.insert(new Element(i));
        }

        test("Circular: First prev points to last", () -> {
            int first = tree.getSmallestLeaf();
            int last = tree.getLargestLeaf();
            int prev = tree.previous(first);
            assertEquals(last, prev);
        });

        test("Circular: Last next points to first", () -> {
            int first = tree.getSmallestLeaf();
            int last = tree.getLargestLeaf();
            int next = tree.next(last);
            assertEquals(first, next);
        });

        test("Circular: Traverse entire list with next()", () -> {
            int first = tree.getSmallestLeaf();
            int current = first;
            int count = 0;

            do {
                count++;
                current = tree.next(current);
            } while (current != first && current != -1);

            assertEquals(5, count);
        });

        test("Circular: Traverse entire list with previous()", () -> {
            int last = tree.getLargestLeaf();
            int current = last;
            int count = 0;

            do {
                count++;
                current = tree.previous(current);
            } while (current != last && current != -1);

            assertEquals(5, count);
        });
    }

    // Find first larger edge cases
    private static void findFirstLargerEdgeCases() {
        Tree23 tree = new Tree23();
        for (int i = 0; i <= 10; i++) {
            tree.insert(new Element(i * 2));
        }

        test("FindFirstLarger: Exact match returns that element or next", () -> {
            int idx = tree.findFirstLargerLeaf(4.0);
            assertNotEquals(-1, idx);
            double val = tree.getValue(idx).ExtractValue();
            assertTrue(val >= 4.0);
        });

        test("FindFirstLarger: Value less than all", () -> {
            int idx = tree.findFirstLargerLeaf(-5.0);
            assertNotEquals(-1, idx);
            assertEquals(0.0, tree.getValue(idx).ExtractValue(), 0.0);
        });

        test("FindFirstLarger: Value greater than all", () -> {
            int idx = tree.findFirstLargerLeaf(100.0);
            assertEquals(-1, idx);
        });

        test("FindFirstLarger: Gap value", () -> {
            int idx = tree.findFirstLargerLeaf(5.0);
            assertNotEquals(-1, idx);
            assertEquals(6.0, tree.getValue(idx).ExtractValue(), 0.0);
        });
    }

    // Change value tests
    private static void changeValueTests() {
        test("ChangeValue: With equal element", () -> {
            Tree23 t = new Tree23();
            t.insert(new Element(10));
            int idx = t.find(new Element(10));
            t.changeValue(idx, new Element(10));
            assertEquals(10.0, t.getValue(idx).ExtractValue(), 0.0);
            assertTrue(t.invariant());
        });

        test("ChangeValue: With different element does nothing", () -> {
            Tree23 t = new Tree23();
            t.insert(new Element(10));
            int idx = t.find(new Element(10));
            t.changeValue(idx, new Element(20));
            assertEquals(10.0, t.getValue(idx).ExtractValue(), 0.0);
        });

        test("ChangeValueUnsafe: Changes without equality check", () -> {
            Tree23 t = new Tree23();
            t.insert(new Element(10));
            int idx = t.find(new Element(10));
            t.changeValueUnsafe(idx, new Element(99));
            assertEquals(99.0, t.getValue(idx).ExtractValue(), 0.0);
        });
    }

    // Previous/next edge cases
    private static void previousNextEdgeCases() {
        Tree23 tree = new Tree23();
        for (int i = 0; i < 3; i++) {
            tree.insert(new Element(i));
        }

        test("PreviousNext: Previous on first returns last", () -> {
            int first = tree.getSmallestLeaf();
            int prev = tree.previous(first);
            int last = tree.getLargestLeaf();
            assertEquals(last, prev);
        });

        test("PreviousNext: Next on last returns first", () -> {
            int last = tree.getLargestLeaf();
            int next = tree.next(last);
            int first = tree.getSmallestLeaf();
            assertEquals(first, next);
        });

        test("PreviousNext: Previous on leaf returns linked list previous", () -> {
            int leaf = tree.find(new Element(1));
            int prev = tree.previous(leaf);
            assertNotEquals(-1, prev);
            assertTrue(tree.getValue(prev).ExtractValue() < 1.0);
        });
    }

    // Depth cases
    private static void depthTests() {
        test("Depth: Empty tree has depth 0", () -> {
            Tree23 t = new Tree23();
            int[] depths = t.depths();
            assertEquals(0, depths[0]);
            assertEquals(0, depths[1]);
        });

        test("Depth: All leaves have same depth after inserts", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i < 1000; i++) {
                t.insert(new Element(i));
            }
            int[] depths = t.depths();
            assertEquals(depths[0], depths[1]);
            assertTrue(depths[0] > 0);
        });
    }

    // Stress tests
    private static void stressTests() {
        test("Stress: Large random insert and delete", () -> {
            Tree23 t = new Tree23();
            int maxN = 5000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i));
            }
            Random r = new Random(42);
            for (int i = 0; i < 5000; i++) {
                int val = r.nextInt(maxN);
                t.delete(new Element(val));
                t.insert(new Element(val));
            }
            assertTrue(t.invariant());
        });

        test("Stress: Insert delete insert cycle", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i < 100; i++) {
                t.insert(new Element(i));
            }
            for (int cycle = 0; cycle < 3; cycle++) {
                for (int i = 0; i < 100; i++) {
                    t.delete(new Element(i));
                }
                for (int i = 0; i < 100; i++) {
                    t.insert(new Element(i));
                }
            }
            assertTrue(t.invariant());
        });

        test("Stress: Massive insert with random delete", () -> {
            Tree23 t = new Tree23();
            int maxN = 10000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i * 2));
            }
            Random r = new Random(123);
            for (int i = 0; i < 5000; i++) {
                int toDelete = r.nextInt(maxN) * 2;
                t.delete(new Element(toDelete));
            }
            assertTrue(t.invariant());
        });
    }

    // Original tests
    private static void originalTests() {
        test("Original: Previous/Next navigation", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i <= 20; i++) {
                t.insert(new Element(i));
            }
            int l = t.find(new Element(7));
            assertNotEquals(-1, l);

            int n = t.next(l);
            assertNotEquals(-1, n);
            assertTrue(t.getValue(n).ExtractValue() > 7);

            int p = t.previous(l);
            assertNotEquals(-1, p);
            assertTrue(t.getValue(p).ExtractValue() < 7);

            assertEquals(l, t.next(p));
            assertEquals(l, t.previous(n));

            assertTrue(t.invariant());
        });

        test("Original: FindFirstLargerLeaf various", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i <= 20; i++) {
                t.insert(new Element(i));
            }
            assertEquals(4.0, t.getValue(t.findFirstLargerLeaf(3.5)).ExtractValue(), 0.0);
            assertEquals(0.0, t.getValue(t.findFirstLargerLeaf(-3.5)).ExtractValue(), 0.0);
            assertEquals(20.0, t.getValue(t.findFirstLargerLeaf(20.0)).ExtractValue(), 0.0);
            assertEquals(14.0, t.getValue(t.findFirstLargerLeaf(13.000001)).ExtractValue(), 0.0);
            assertEquals(-1, t.findFirstLargerLeaf(20.000001));
            assertTrue(t.invariant());
        });

        test("Original: Find exact elements", () -> {
            Tree23 t = new Tree23();
            for (int i = 0; i <= 20; i++) {
                t.insert(new Element(i));
            }
            assertNotEquals(-1, t.find(new Element(13)));
            assertNotEquals(-1, t.find(new Element(7)));
            assertEquals(-1, t.find(new Element(23)));
            assertEquals(-1, t.find(new Element(-2)));
            assertTrue(t.invariant());
        });

        test("Original: Insert many and find all", () -> {
            Tree23 t = new Tree23();
            int maxN = 10000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i));
            }
            for (int i = 0; i < maxN; i++) {
                assertNotEquals(-1, t.find(new Element(i)));
            }
            assertTrue(t.invariant());
        });

        test("Original: Delete all in order", () -> {
            Tree23 t = new Tree23();
            int maxN = 10000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i));
            }
            for (int i = 0; i < maxN; i++) {
                t.delete(new Element(i));
            }
            int[] depths = t.depths();
            assertEquals(0, depths[0]);
        });

        test("Original: GetSmallestLeaf and GetLargestLeaf", () -> {
            Tree23 t = new Tree23();
            int maxN = 10000;
            for (int i = 0; i < maxN; i++) {
                t.insert(new Element(i));
            }
            int smallest = t.getSmallestLeaf();
            assertEquals(0.0, t.getValue(smallest).ExtractValue(), 0.0);
            int largest = t.getLargestLeaf();
            assertEquals((maxN - 1) * 1.0, t.getValue(largest).ExtractValue(), 0.0);
            assertTrue(t.invariant());
        });
    }

    // Helper methods
    private static Tree23 createTree(int from, int to) {
        Tree23 t = new Tree23();
        for (int i = from; i <= to; i++) {
            t.insert(new Element(i));
        }
        return t;
    }

    private static void test(String name, Runnable assertion) {
        currentTest = name;
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

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(String.format("Expected: %s, Actual: %s", expected, actual));
        }
    }

    private static void assertNotEquals(int expected, int actual) {
        if (expected == actual) {
            throw new AssertionError(String.format("Expected not: %d, Actual: %d", expected, actual));
        }
    }

    private static void assertNotEquals(Object expected, Object actual) {
        if (expected == null && actual == null) {
            throw new AssertionError("Expected not equal but both are null");
        }
        if (expected != null && expected.equals(actual)) {
            throw new AssertionError(String.format("Expected not: %s, Actual: %s", expected, actual));
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    private static void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception but got: " + e.getMessage());
        }
    }
}
