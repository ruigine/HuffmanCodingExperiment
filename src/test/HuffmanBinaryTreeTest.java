package test;

import experiment.ExperimentMetrics;
import huffman.binarytree.*;

public class HuffmanBinaryTreeTest {
    public static void main(String[] args) {
        int NUM_RUNS = 10;
        int INPUT_REPEAT = 50000;
        String INPUT_TEXT = ExperimentMetrics.generateInputText(INPUT_REPEAT);

        System.out.println("✅ Huffman Encoding Test Using Binary Tree with Priority Queue");
        System.out.printf("Input Text Length: %d characters (%.2f KB)%n",
                INPUT_TEXT.length(), INPUT_TEXT.getBytes().length / 1024.0);
        System.out.printf("Number of Runs per Experiment: %d%n", NUM_RUNS);

        ExperimentMetrics metrics = new ExperimentMetrics("Priority Queue Huffman", INPUT_TEXT.getBytes().length);

        for (int i = 0; i < NUM_RUNS; i++) {
            System.gc();
            long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startTime = System.nanoTime();

            // ==== Huffman Encoding using Binary Tree with Priority Queue ====
            HuffmanBinaryTreeStrategy.HuffmanResult result = HuffmanBinaryTreeStrategy.huffman(INPUT_TEXT);
            long encodedBits = result.bitString.length();

            long endTime = System.nanoTime();
            long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            metrics.logRun(endTime - startTime, endMem - startMem, encodedBits);
        }

        metrics.printSummary(NUM_RUNS);

        // Print additional information for the last run as an example
        HuffmanBinaryTreeStrategy.HuffmanResult sampleResult = HuffmanBinaryTreeStrategy.huffman(INPUT_TEXT);

        // Calculate compression ratio
        int originalSize = INPUT_TEXT.length() * 8;  // Assuming 8 bits per character (ASCII)
        int compressedSize = sampleResult.bitString.length();
        double compressionRatio = (double) (originalSize - compressedSize) / originalSize * 100;

        System.out.println("\nDetailed Results for Last Run:");
        System.out.println("Original size (bits): " + originalSize);
        System.out.println("Compressed size (bits): " + compressedSize);
        System.out.printf("Compression ratio: %.2f%%\n", compressionRatio);

        // Verify decoding works correctly for a small sample
        String sampleText = INPUT_TEXT.substring(0, Math.min(20, INPUT_TEXT.length()));
        HuffmanBinaryTreeStrategy.HuffmanResult smallResult = HuffmanBinaryTreeStrategy.huffman(sampleText);
        String decodedText = HuffmanBinaryTreeStrategy.decode(smallResult.byteData, smallResult.encodingMap, smallResult.padding);

        System.out.println("\nVerification with sample text:");
        System.out.println("Sample text: " + sampleText);
        System.out.println("Decoded text: " + decodedText);
        System.out.println("Decoding successful: " + decodedText.equals(sampleText));

        System.out.println("\nAlgorithm Complexity Analysis:");
        System.out.println("Time Complexity: O(n + k log k)");
        System.out.println("  where n = text length (" + INPUT_TEXT.length() + " characters)");
        System.out.println("  and k = unique characters (typically << n)");
        System.out.println("Memory Complexity: O(n) for storage of the encoded result");
    }
}
