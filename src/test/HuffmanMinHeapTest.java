package test;

import experiment.ExperimentMetrics;
import huffman.minheap.CompressedData;
import huffman.minheap.HuffmanMinHeapStrategy;

public class HuffmanMinHeapTest {
    public static void main(String[] args) {
        int NUM_RUNS = 10;
        int INPUT_REPEAT = 50000;
        String INPUT_TEXT = ExperimentMetrics.generateInputText(INPUT_REPEAT);

        System.out.println("✅ Huffman Encoding Test Using MinHeap");
        System.out.printf("Input Text Length: %d characters (%.2f KB)%n",
                INPUT_TEXT.length(), INPUT_TEXT.getBytes().length / 1024.0);

        ExperimentMetrics metrics = new ExperimentMetrics("MinHeap Huffman", INPUT_TEXT.getBytes().length);

        for (int i = 0; i < NUM_RUNS; i++) {
            System.gc();
            long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startTime = System.nanoTime();

            // ==== Huffman Encoding using MinHeap ====
            HuffmanMinHeapStrategy huffman = new HuffmanMinHeapStrategy(INPUT_TEXT);
            CompressedData compressed = huffman.compressAndReturn(INPUT_TEXT);
            long encodedBits = compressed.bitLength;

            long endTime = System.nanoTime();
            long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            metrics.logRun(endTime - startTime, endMem - startMem, encodedBits);
        }

        metrics.printSummary(NUM_RUNS);
    }
}
