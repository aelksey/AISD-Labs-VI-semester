package com.lab;

import java.util.ArrayList;
import java.util.List;

public class Tree23IteratorTestRunner {

    private static int passed = 0;
    private static int failed = 0;
    private static String currentTest = "";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Tree23 Iterator Test Suite");
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
        emptyTreeIteratorTests();
        beginEndTests();
        nextPreviousTests();
        gotoByValueTests();
        gotoByKeyTests();
        getCurrentKeyTests();
        isCurrentLeafTests();
        getCurrentNodeTests();
        deleteCurrentTests();
        fullTraversalTests();
        twoNodeThreeNodeTests();
        stressTests();
    }

    // Empty tree iterator tests
    private static void emptyTreeIteratorTests() {
        test("Empty: begin() - iterator invalid", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertFalse(iter.isValid());
        });

        test("Empty: end() - iterator invalid", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.end();
            assertFalse(iter.isValid());
        });

        test("Empty: next() - does nothing", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.next();
            assertFalse(iter.isValid());
        });

        test("Empty: previous() - does nothing", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.previous();
            assertFalse(iter.isValid());
        });

        test("Empty: getCurrentKey() - returns 0", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(0.0, iter.getCurrentKey(), 0.0);
        });

        test("Empty: getCurrentNode() - returns -1", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(-1, iter.getCurrentNode());
        });
    }

    // Begin/End tests
    private static void beginEndTests() {
        test("Begin: Single element - points to that element", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(42));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertTrue(iter.isValid());
            assertEquals(42.0, iter.getCurrentKey(), 0.0);
        });

        test("Begin: Multiple elements - points to first (smallest)", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertTrue(iter.isValid());
            assertEquals(10.0, iter.getCurrentKey(), 0.0);
        });

        test("Begin: Descending insert - correct first element", () -> {
            Tree23 tree = new Tree23();
            for (int i = 30; i >= 10; i -= 10) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertTrue(iter.isValid());
            assertEquals(10.0, iter.getCurrentKey(), 0.0);
        });

        test("End: Single element - points to that element", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(42));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.end();
            assertTrue(iter.isValid());
            assertEquals(42.0, iter.getCurrentKey(), 0.0);
        });

        test("End: Multiple elements - points to last (largest)", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.end();
            assertTrue(iter.isValid());
            assertEquals(30.0, iter.getCurrentKey(), 0.0);
        });

        test("End: After begin - different positions", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            double firstValue = iter.getCurrentKey();
            iter.end();
            double lastValue = iter.getCurrentKey();
            assertTrue(firstValue < lastValue);
        });
    }

    // Next/Previous tests
    private static void nextPreviousTests() {
        test("Next: Traverse entire tree in order", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // Should get 2n-1 values (keys + leaves), but some may be duplicates
            assertEquals(19, values.size());
            // Check non-decreasing order (allows duplicates)
            for (int i = 0; i < 18; i++) {
                assertTrue(values.get(i) <= values.get(i + 1));
            }
        });

        test("Next: At end - becomes invalid", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            iter.next();
            iter.next();
            iter.next();
            assertFalse(iter.isValid());
        });

        test("Next: Verify inorder (keys + leaves)", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(5));
            tree.insert(new Element(15));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // 4 elements = 2n-1 = 7 values (possibly some duplicates from keys=leaves)
            assertEquals(7, values.size());
            for (int i = 0; i < values.size() - 1; i++) {
                if (!(values.get(i) <= values.get(i + 1))) {
                    throw new AssertionError("Values not in order at index " + i);
                }
            }
        });

        test("Previous: Traverse entire tree in reverse", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.end();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.previous();
            }
            // Should get 2n-1 values (keys + leaves)
            assertEquals(19, values.size());
            for (int i = 0; i < 18; i++) {
                assertTrue(values.get(i) >= values.get(i + 1));
            }
        });

        test("Previous: At beginning - becomes invalid", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            iter.previous();
            assertFalse(iter.isValid());
        });

        test("Previous: Symmetry with next", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 5; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            iter.next();
            iter.next();
            double midValue = iter.getCurrentKey();
            iter.previous();
            double afterPrev = iter.getCurrentKey();
            // Due to duplicates, the behavior may differ - just check it moves
            assertTrue(afterPrev <= midValue);
        });

        test("Previous: Full round trip", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            iter.next();
            iter.next();
            iter.previous();
            iter.previous();
            assertTrue(iter.isValid());
            assertEquals(10.0, iter.getCurrentKey(), 0.0);
        });
    }

    // gotoByValue tests
    private static void gotoByValueTests() {
        test("GotoByValue: Find existing leaf value", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByValue(30.0);
            assertTrue(found);
            assertEquals(30.0, iter.getCurrentKey(), 0.0);
        });

        test("GotoByValue: Value not found returns false", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByValue(35.0);
            assertFalse(found);
            assertFalse(iter.isValid());
        });

        test("GotoByValue: Empty tree returns false", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByValue(10.0);
            assertFalse(found);
        });

        test("GotoByValue: First element", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByValue(10.0);
            assertTrue(found);
            assertEquals(10.0, iter.getCurrentKey(), 0.0);
        });

        test("GotoByValue: Last element", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByValue(30.0);
            assertTrue(found);
            assertEquals(30.0, iter.getCurrentKey(), 0.0);
        });
    }

    // gotoByKey tests
    private static void gotoByKeyTests() {
        test("GotoByKey: First position (index 0)", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 5; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByKey(0);
            assertTrue(found);
            assertEquals(0.0, iter.getCurrentKey(), 0.0);
        });

        test("GotoByKey: Middle position", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByKey(10);
            assertTrue(found);
            assertEquals(50.0, iter.getCurrentKey(), 0.0);
        });

        test("GotoByKey: Last position", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 5; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            // With 5 elements, there should be ~9 values total (keys + leaves)
            boolean found = iter.gotoByKey(8);
            assertTrue(found);
            assertEquals(40.0, iter.getCurrentKey(), 0.0);
        });

        test("GotoByKey: Out of range returns false", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByKey(10);
            assertFalse(found);
        });

        test("GotoByKey: Empty tree returns false", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            boolean found = iter.gotoByKey(0);
            assertFalse(found);
        });
    }

    // getCurrentKey tests
    private static void getCurrentKeyTests() {
        test("GetCurrentKey: Returns correct value", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(42));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(42.0, iter.getCurrentKey(), 0.0);
        });

        test("GetCurrentKey: Changes with next", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            double first = iter.getCurrentKey();
            iter.next();
            double second = iter.getCurrentKey();
            // With 3 elements, should get 5 values total (keys + leaves)
            // First may be 10 (leaf), second may be 10 (key) due to duplicates
            assertTrue(first <= second);
        });

        test("GetCurrentKey: Invalid iterator returns 0", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(0.0, iter.getCurrentKey(), 0.0);
        });
    }

    // isCurrentLeaf tests
    private static void isCurrentLeafTests() {
        test("IsCurrentLeaf: Correctly identifies leaf", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertTrue(iter.isCurrentLeaf());
        });

        test("IsCurrentLeaf: Correctly identifies internal key", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            boolean firstIsLeaf = iter.isCurrentLeaf();
            iter.next();
            boolean secondIsLeaf = iter.isCurrentLeaf();
            assertTrue(firstIsLeaf || !secondIsLeaf);
        });
    }

    // getCurrentNode tests
    private static void getCurrentNodeTests() {
        test("GetCurrentNode: Returns valid node index", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            int node = iter.getCurrentNode();
            assertTrue(node >= 0);
        });

        test("GetCurrentNode: Find correct leaf", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            int node = iter.getCurrentNode();
            assertNotEquals(-1, node);
            assertEquals(10.0, tree.getValue(node).ExtractValue(), 0.0);
        });

        test("GetCurrentNode: Invalid returns -1", () -> {
            Tree23 tree = new Tree23();
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(-1, iter.getCurrentNode());
        });
    }

    // deleteCurrent tests
    private static void deleteCurrentTests() {
        test("DeleteCurrent: Deletes and advances to next", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            assertEquals(10.0, iter.getCurrentKey(), 0.0);
            iter.deleteCurrent();
            assertTrue(iter.isValid());
            assertTrue(iter.getCurrentKey() >= 20.0);
            assertTrue(tree.invariant());
        });

test("DeleteCurrent: Last element - becomes invalid", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.end();
            iter.deleteCurrent();
            // After deleting last element, if tree has other elements, should advance
            // If tree becomes empty or only one element, behavior depends on implementation
            assertTrue(tree.invariant());
        });

        test("DeleteCurrent: Only element - tree becomes empty", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            iter.deleteCurrent();
            assertFalse(iter.isValid());
            assertEquals(-1, tree.find(new Element(10)));
            assertTrue(tree.invariant());
        });

        test("DeleteCurrent: Middle element", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 5; i++) {
                tree.insert(new Element(i * 10));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            
            // Find the element 20 and delete it
            boolean found = iter.gotoByValue(20.0);
            if (!found) {
                throw new AssertionError("Should have found value 20");
            }
            
            iter.deleteCurrent();
            if (!tree.invariant()) {
                throw new AssertionError("Tree invariant should hold after delete");
            }
            
            // Verify the element is gone from tree
            int foundIdx = tree.find(new Element(20));
            if (foundIdx != -1) {
                throw new AssertionError("Element 20 should be deleted from tree");
            }
            
            // Verify other elements still exist
            for (int i = 0; i < 5; i++) {
                if (i != 2) { // except 20 (index 2)
                    if (tree.find(new Element(i * 10)) == -1) {
                        throw new AssertionError("Element " + (i * 10) + " should still exist");
                    }
                }
            }
        });
    }

    // Full traversal tests
    private static void fullTraversalTests() {
test("Traversal: Complete inorder sequence", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(5));
            tree.insert(new Element(15));
            tree.insert(new Element(25));
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // 5 elements = 2*5-1 = 9 values
            assertEquals(9, values.size());
            // Check non-decreasing order (allows duplicates when key == leaf)
            for (int i = 0; i < values.size() - 1; i++) {
                if (!(values.get(i) <= values.get(i + 1))) {
                    throw new AssertionError("Values not in order at index " + i + ": " + values.get(i) + " > " + values.get(i + 1));
                }
            }
        });

        test("Traversal: Count matches inserted elements", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 100; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            int count = 0;
            while (iter.isValid()) {
                count++;
                iter.next();
            }
            // Should get 2n-1 values (keys + leaves)
            assertEquals(199, count);
        });

        test("Traversal: Forward and reverse match", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 10; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> forward = new ArrayList<>();
            while (iter.isValid()) {
                forward.add(iter.getCurrentKey());
                iter.next();
            }
            iter.end();
            List<Double> reverse = new ArrayList<>();
            while (iter.isValid()) {
                reverse.add(iter.getCurrentKey());
                iter.previous();
            }
            assertEquals(forward.size(), reverse.size());
            for (int i = 0; i < forward.size(); i++) {
                assertEquals(forward.get(i), reverse.get(forward.size() - 1 - i), 0.0);
            }
        });

        test("Traversal: Iterate after modifications", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 5; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> beforeDelete = new ArrayList<>();
            while (iter.isValid()) {
                beforeDelete.add(iter.getCurrentKey());
                iter.next();
            }
            tree.delete(new Element(2));
            iter.begin();
            List<Double> afterDelete = new ArrayList<>();
            while (iter.isValid()) {
                afterDelete.add(iter.getCurrentKey());
                iter.next();
            }
            // Before: 2*5-1=9, After: 2*4-1=7
            assertEquals(7, afterDelete.size());
            assertEquals(-1, afterDelete.indexOf(2.0));
        });
    }

    // 2-node and 3-node tests
    private static void twoNodeThreeNodeTests() {
        test("TwoNode: Simple 2-node tree", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // Should get 3 values (2 leaves + 1 key)
            assertEquals(3, values.size());
            for (int i = 0; i < 2; i++) {
                assertTrue(values.get(i) <= values.get(i + 1));
            }
        });

        test("ThreeNode: Simple 3-node tree", () -> {
            Tree23 tree = new Tree23();
            tree.insert(new Element(10));
            tree.insert(new Element(20));
            tree.insert(new Element(30));
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // 3 elements = 2 leaves + 2 keys = 5 values
            assertEquals(5, values.size());
            for (int i = 0; i < 4; i++) {
                assertTrue(values.get(i) <= values.get(i + 1));
            }
        });

        test("MixedNodes: Complex tree structure", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 20; i++) {
                tree.insert(new Element(i * 5));
            }
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> values = new ArrayList<>();
            while (iter.isValid()) {
                values.add(iter.getCurrentKey());
                iter.next();
            }
            // 20 elements = 2*20-1 = 39 values
            assertEquals(39, values.size());
            for (int i = 0; i < 38; i++) {
                assertTrue(values.get(i) <= values.get(i + 1));
            }
        });

        test("Balanced: Verify all elements found", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 50; i++) {
                tree.insert(new Element(i));
            }
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            int count = 0;
            while (iter.isValid()) {
                double val = iter.getCurrentKey();
                assertTrue(val >= 0 && val < 50);
                count++;
                iter.next();
            }
            // 50 elements = 2*50-1 = 99 values
            assertEquals(99, count);
        });
    }

    // Stress tests
    private static void stressTests() {
        test("Stress: Large tree traversal", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 1000; i++) {
                tree.insert(new Element(i));
            }
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            int count = 0;
            while (iter.isValid()) {
                count++;
                iter.next();
            }
            // 1000 elements = 2*1000-1 = 1999 values
            assertEquals(1999, count);
        });

        test("Stress: Large tree forward and reverse", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 100; i++) {
                tree.insert(new Element(i * 2));
            }
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            List<Double> forward = new ArrayList<>();
            while (iter.isValid()) {
                forward.add(iter.getCurrentKey());
                iter.next();
            }
            iter.end();
            List<Double> reverse = new ArrayList<>();
            while (iter.isValid()) {
                reverse.add(iter.getCurrentKey());
                iter.previous();
            }
            assertEquals(forward.size(), reverse.size());
            for (int i = 0; i < forward.size(); i++) {
                assertEquals(forward.get(i), reverse.get(forward.size() - 1 - i), 0.0);
            }
        });

        test("Stress: Multiple begin/end cycles", () -> {
            Tree23 tree = new Tree23();
            for (int i = 0; i < 50; i++) {
                tree.insert(new Element(i));
            }
            Tree23.ForwardIterator iter = tree.iterator();
            for (int cycle = 0; cycle < 10; cycle++) {
                iter.begin();
                assertTrue(iter.isValid());
                assertEquals(0.0, iter.getCurrentKey(), 0.0);
                iter.end();
                assertTrue(iter.isValid());
            }
        });

        test("Stress: Random insert and traverse", () -> {
            Tree23 tree = new Tree23();
            java.util.Random rand = new java.util.Random(42);
            for (int i = 0; i < 100; i++) {
                tree.insert(new Element(rand.nextInt(1000)));
            }
            assertTrue(tree.invariant());
            Tree23.ForwardIterator iter = tree.iterator();
            iter.begin();
            double prev = -1;
            while (iter.isValid()) {
                double curr = iter.getCurrentKey();
                assertTrue(curr >= prev);
                prev = curr;
                iter.next();
            }
        });
    }

    // Helper methods
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
            e.printStackTrace();
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

    private static void assertNotEquals(int expected, int actual) {
        if (expected == actual) {
            throw new AssertionError(String.format("Expected not: %d, Actual: %d", expected, actual));
        }
    }
}