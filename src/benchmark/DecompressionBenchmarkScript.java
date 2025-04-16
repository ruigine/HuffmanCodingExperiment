package benchmark;

import experiment.ExperimentMetrics;
import huffman.binarytree.HuffmanBinaryTreeStrategy;
import huffman.javapq.HuffmanJavaPriorityQueueStrategy;
import huffman.minheap.CompressedData;
import huffman.minheap.HuffmanMinHeapStrategy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class DecompressionBenchmarkScript {
    public static void main(String[] args) {
        String inputText = ExperimentMetrics.generateInputText(50000);
        int NUM_RUNS_HOT = 10;

        System.out.println("Constant input:");

        // === MinHeap ===
        HuffmanMinHeapStrategy minHeapHuffman = new HuffmanMinHeapStrategy(inputText);
        CompressedData compressed = minHeapHuffman.compressAndReturn(inputText);
        measure(() -> {
            var result = minHeapHuffman.decompress(compressed);
            Blackhole.consume(result);
        }, NUM_RUNS_HOT, "MinHeap");

        // === BinaryTree ===
        HuffmanBinaryTreeStrategy.HuffmanResult smallResult = HuffmanBinaryTreeStrategy.huffman(inputText);
        measure(() -> {
            var result = HuffmanBinaryTreeStrategy.decode(smallResult.byteData, smallResult.encodingMap, smallResult.padding);
            Blackhole.consume(result);
        }, NUM_RUNS_HOT, "BinaryTree");

        // === Java Priority Queue ===
        HuffmanJavaPriorityQueueStrategy javaPQ = new HuffmanJavaPriorityQueueStrategy(inputText);
        CompressedData javaPQCompressed = javaPQ.compressAndReturn(inputText);
        measure(() -> {
            var result = javaPQ.decompress(javaPQCompressed);
            Blackhole.consume(result);
        }, NUM_RUNS_HOT, "JavaPriorityQueue");

        System.out.println();

        exportCsv(
            (String s) -> {
                HuffmanMinHeapStrategy strategy = new HuffmanMinHeapStrategy(s);
                return new Object[] { strategy, strategy.compressAndReturn(s) };
            },
            (Object[] arr) -> {
                HuffmanMinHeapStrategy strategy = (HuffmanMinHeapStrategy) arr[0];
                CompressedData data = (CompressedData) arr[1];
                var result = strategy.decompress(data);
                Blackhole.consume(result);
            },
            NUM_RUNS_HOT,
            "MinHeap"
        );

        exportCsv(HuffmanBinaryTreeStrategy::huffman, (input) -> {
            var result = HuffmanBinaryTreeStrategy.decode(input.byteData, input.encodingMap, input.padding);
            Blackhole.consume(result);
        }, NUM_RUNS_HOT, "BinaryTree");

        exportCsv(
            (String s) -> {
                HuffmanJavaPriorityQueueStrategy strategy = new HuffmanJavaPriorityQueueStrategy(s);
                return new Object[] { strategy, strategy.compressAndReturn(s) };
            },
            (Object[] arr) -> {
                HuffmanJavaPriorityQueueStrategy strategy = (HuffmanJavaPriorityQueueStrategy) arr[0];
                CompressedData data = (CompressedData) arr[1];
                var result = strategy.decompress(data);
                Blackhole.consume(result);
            },
            NUM_RUNS_HOT,
            "JavaPriorityQueue"
        );
    }

    private static void measure(Runnable work, int numRuns, String name) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numRuns; i++) {
            System.gc();
            work.run();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / numRuns;
        System.out.println(name + " execution time: " + duration / 1000000 + " milliseconds");
    }

    private static <T> void exportCsv(Function<String, T> provider, Consumer<T> work, int numRuns, String name) {
        String fileName = name + "_decomp_benchmark.csv";
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            // Write CSV header
            csvWriter.append("InputLength,DurationNanos\n");
            int MAX_INPUT = 1000;
            ArrayList<Long> durations = new ArrayList<>(MAX_INPUT);

            // Run benchmarks and write results
            for (int inputLength = 1; inputLength <= MAX_INPUT; inputLength++) {
                String inputTextVariable = ExperimentMetrics.generateInputText(inputLength);
                T input = provider.apply(inputTextVariable);
                long startTime = System.nanoTime();
                for (int i = 0; i < numRuns; i++) {
                    System.gc();
                    work.accept(input);
                }
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / numRuns;
                durations.add(duration);

                // Write this data point to CSV
                csvWriter.append(String.valueOf(inputLength)).append(",").append(String.valueOf(duration)).append("\n");

                // Optional: flush every N entries to avoid memory issues with large files
                if (inputLength % 1000 == 0) {
                    csvWriter.flush();
                }
            }

            System.out.println("Growing inputs for " + name + ":");
            System.out.println(durations);
            System.out.println();

            System.out.println("CSV file created for " + name + ": " + fileName);

        } catch (IOException e) {
            System.err.println("Error writing CSV for " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
