package test;

import experiment.ExperimentMetrics;
import huffman.adaptive.SelfAdaptiveCompressor;

public class HuffmanAdaptiveTest {
    public static void main(String[] args) {
        int NUM_RUNS = 10;
        int INPUT_REPEAT = 50000;
        String INPUT_TEXT = ExperimentMetrics.generateInputText(INPUT_REPEAT);

        System.out.println("✅ Self-Adaptive Huffman Encoding Test");
        System.out.printf("Input Text Length: %d characters (%.2f KB)%n",
                INPUT_TEXT.length(), INPUT_TEXT.getBytes().length / 1024.0);
        System.out.printf("Number of Runs per Experiment: %d%n", NUM_RUNS);

        // Track total and average time
        long totalTime = 0;

        for (int i = 0; i < NUM_RUNS; i++) {
            System.gc();

            // Measure execution time
            long startTime = System.nanoTime();

            // Execute compression
            SelfAdaptiveCompressor compressor = new SelfAdaptiveCompressor(false);
            compressor.compress(INPUT_TEXT);

            long endTime = System.nanoTime();
            long runTime = endTime - startTime;
            totalTime += runTime;

            System.out.printf("Run %d: %.4f ms%n", i + 1, runTime / 1_000_000.0);
        }

        // Print summary
        double avgTime = totalTime / (double) NUM_RUNS;
        System.out.println("\n===== Performance Summary =====");
        System.out.printf("Average Execution Time: %.4f ms%n", avgTime / 1_000_000.0);
    }
}
