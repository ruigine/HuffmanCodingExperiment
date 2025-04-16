package test;
import java.util.Map;

import experiment.ExperimentMetrics;
import huffman.hashmap.*;

public class HuffmanHashMapTest {
    public static void main(String[] args) {
        int NUM_RUNS = 10;
        int INPUT_REPEAT = 50000;
        String INPUT_TEXT = ExperimentMetrics.generateInputText(INPUT_REPEAT);

        System.out.println("✅ Huffman Encoding Test Using HashMap");
        System.out.printf("Input Text Length: %d characters (%.2f KB)%n",
                INPUT_TEXT.length(), INPUT_TEXT.getBytes().length / 1024.0);
        System.out.printf("Number of Runs per Experiment: %d%n", NUM_RUNS);

        ExperimentMetrics metrics = new ExperimentMetrics("HashMap Huffman", INPUT_TEXT.getBytes().length);

        for (int i = 0; i < NUM_RUNS; i++) {
            System.gc();
            long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startTime = System.nanoTime();

            // ==== Huffman Encoding using HashMap ====
            Map<Character, String> huffmanMap = HuffmanHashMapStrategy.buildHuffmanEncoding(INPUT_TEXT);
            String encodedText = HuffmanHashMapStrategy.encodeText(INPUT_TEXT, huffmanMap);
            long encodedBits = encodedText.length();

            long endTime = System.nanoTime();
            long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            metrics.logRun(endTime - startTime, endMem - startMem, encodedBits);
        }

        metrics.printSummary(NUM_RUNS);
    }
}
