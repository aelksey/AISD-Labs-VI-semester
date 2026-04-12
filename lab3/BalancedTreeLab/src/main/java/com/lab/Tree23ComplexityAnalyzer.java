package com.lab;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tree23ComplexityAnalyzer {

    private static class OperationResult {
        String operation;
        String treeType;
        int size;
        long timeNanos;

        OperationResult(String operation, String treeType, int size, long timeNanos) {
            this.operation = operation;
            this.treeType = treeType;
            this.size = size;
            this.timeNanos = timeNanos;
        }
    }

    public static void main(String[] args) {
        System.out.println("2-3 Tree Complexity Analyzer");
        System.out.println("==============================");

        List<OperationResult> results = new ArrayList<>();

        // Define sizes to test - start from larger sizes to avoid zero measurements
        int[] sizes = {1000, 2000, 5000, 10000, 20000, 50000, 100000};

        // Number of operations to average for each measurement
        int iterations = 50;

        System.out.println("Testing Random Trees...");
        // Test random trees
        for (int size : sizes) {
            System.out.println("  Testing size: " + size);

            // Insert test
            long insertTime = measureRandomTreeInsert(size, iterations);
            results.add(new OperationResult("Insert", "Random", size, insertTime));

            // Search test
            long searchTime = measureRandomTreeSearch(size, iterations);
            results.add(new OperationResult("Search", "Random", size, searchTime));

            // Delete test
            long deleteTime = measureRandomTreeDelete(size, iterations);
            results.add(new OperationResult("Delete", "Random", size, deleteTime));
        }

        System.out.println("\nTesting Degenerate Trees...");
        // Test degenerate trees
        for (int size : sizes) {
            System.out.println("  Testing size: " + size);

            // Insert test
            long insertTime = measureDegenerateTreeInsert(size, iterations);
            results.add(new OperationResult("Insert", "Degenerate", size, insertTime));

            // Search test
            long searchTime = measureDegenerateTreeSearch(size, iterations);
            results.add(new OperationResult("Search", "Degenerate", size, searchTime));

            // Delete test
            long deleteTime = measureDegenerateTreeDelete(size, iterations);
            results.add(new OperationResult("Delete", "Degenerate", size, deleteTime));
        }

        // Save results to CSV
        saveResultsToCSV(results, "tree23_complexity_results.csv");

    }

    private static long measureRandomTreeInsert(int size, int iterations) {
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            Tree23 tree = new Tree23();
            Random rand = new Random();

            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                double value = rand.nextDouble() * 1000000;
                TreeElement elem = createTreeElement(value);
                tree.insert(elem);
            }
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static long measureRandomTreeSearch(int size, int iterations) {
        // First build a tree
        Tree23 tree = new Tree23();
        Random rand = new Random();
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double value = rand.nextDouble() * 1000000;
            values.add(value);
            TreeElement elem = createTreeElement(value);
            tree.insert(elem);
        }

        // Measure search time
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            double searchValue = values.get(rand.nextInt(values.size()));
            TreeElement elem = createTreeElement(searchValue);

            long startTime = System.nanoTime();
            tree.find(elem);
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static long measureRandomTreeDelete(int size, int iterations) {
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            Tree23 tree = new Tree23();
            Random rand = new Random();
            List<Double> values = new ArrayList<>();

            // Build tree
            for (int i = 0; i < size; i++) {
                double value = rand.nextDouble() * 1000000;
                values.add(value);
                TreeElement elem = createTreeElement(value);
                tree.insert(elem);
            }

            // Measure delete time
            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                TreeElement elem = createTreeElement(values.get(i));
                tree.delete(elem);
            }
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static long measureDegenerateTreeInsert(int size, int iterations) {
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            Tree23 tree = new Tree23();

            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                double value = i * 10.0;
                TreeElement elem = createTreeElement(value);
                tree.insert(elem);
            }
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static long measureDegenerateTreeSearch(int size, int iterations) {
        // Build degenerate tree
        Tree23 tree = new Tree23();
        List<Double> values = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double value = i * 10.0;
            values.add(value);
            TreeElement elem = createTreeElement(value);
            tree.insert(elem);
        }

        // Measure search time
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            double searchValue = values.get(size / 2);
            TreeElement elem = createTreeElement(searchValue);

            long startTime = System.nanoTime();
            tree.find(elem);
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static long measureDegenerateTreeDelete(int size, int iterations) {
        long totalTime = 0;
        int validIterations = 0;

        for (int iter = 0; iter < iterations; iter++) {
            Tree23 tree = new Tree23();
            List<Double> values = new ArrayList<>();

            // Build degenerate tree
            for (int i = 0; i < size; i++) {
                double value = i * 10.0;
                values.add(value);
                TreeElement elem = createTreeElement(value);
                tree.insert(elem);
            }

            // Measure delete time
            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                TreeElement elem = createTreeElement(values.get(i));
                tree.delete(elem);
            }
            long endTime = System.nanoTime();

            long duration = endTime - startTime;
            if (duration > 0) {
                totalTime += duration;
                validIterations++;
            }
        }

        return validIterations > 0 ? totalTime / validIterations : 1;
    }

    private static void saveResultsToCSV(List<OperationResult> results, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("Operation,Tree Type,Size,Time (nanoseconds),Time (microseconds),Time (milliseconds)");

            // Write data
            for (OperationResult result : results) {
                double timeUs = result.timeNanos / 1000.0;
                double timeMs = result.timeNanos / 1000000.0;
                writer.printf("%s,%s,%d,%d,%.3f,%.6f%n",
                        result.operation,
                        result.treeType,
                        result.size,
                        result.timeNanos,
                        timeUs,
                        timeMs);
            }

            System.out.println("\n✓ Results saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }

    private static TreeElement createTreeElement(double value) {
        return new TreeElement() {
            @Override
            public double ExtractValue() {
                return value;
            }

            @Override
            public boolean Equal(TreeElement other) {
                return Math.abs(this.ExtractValue() - other.ExtractValue()) < 0.0001;
            }
        };
    }
}