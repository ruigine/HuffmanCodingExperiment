package benchmark;

import benchmark.implementations.HuffmanBinaryTreeTest;
import benchmark.implementations.HuffmanHashMapTest;
import benchmark.implementations.HuffmanJavaPriorityQueueTest;
import benchmark.implementations.HuffmanMinHeapTest;
import benchmark.implementations.HuffmanAdaptiveTest;
import experiment.ExperimentMetrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CompressionBenchmarkScript {
    public static void main(String[] args) {
        CompressionBenchmark[] implementations = {
            new HuffmanAdaptiveTest(),
            new HuffmanBinaryTreeTest(),
            new HuffmanHashMapTest(),
            new HuffmanMinHeapTest(),
            new HuffmanJavaPriorityQueueTest()
        };

        String inputText = ExperimentMetrics.generateInputText(50000);
        int NUM_RUNS_HOT = 10;

        System.out.println("Constant input:");
        for (var implementation : implementations) {
            long startTime = System.nanoTime();
            implementation.compress(NUM_RUNS_HOT, inputText);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / NUM_RUNS_HOT;
            System.out.println(implementation.getClass().getName() + " execution time: " + duration / 1000000 + " milliseconds");
        }

        System.out.println();

        for (var implementation : implementations) {
            String className = implementation.getClass().getSimpleName();
            String fileName = className + "_benchmark.csv";

            try (FileWriter csvWriter = new FileWriter(fileName)) {
                // Write CSV header
                csvWriter.append("InputLength,DurationNanos\n");

                int MAX_INPUT = 1000;
                ArrayList<Long> durations = new ArrayList<>(MAX_INPUT);

                // Run benchmarks and write results
                for (int inputLength = 1; inputLength <= MAX_INPUT; inputLength++) {
                    String inputTextVariable = ExperimentMetrics.generateInputText(inputLength);
                    long startTime = System.nanoTime();
                    implementation.compress(NUM_RUNS_HOT, inputTextVariable);
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / NUM_RUNS_HOT;
                    durations.add(duration);

                    // Write this data point to CSV
                    csvWriter.append(String.valueOf(inputLength)).append(",").append(String.valueOf(duration)).append("\n");

                    // Optional: flush every N entries to avoid memory issues with large files
                    if (inputLength % 1000 == 0) {
                        csvWriter.flush();
                    }
                }

                System.out.println("Growing inputs for " + implementation.getClass().getName() + ":");
                System.out.println(durations);
                System.out.println();

                System.out.println("CSV file created for " + className + ": " + fileName);

            } catch (IOException e) {
                System.err.println("Error writing CSV for " + className + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
