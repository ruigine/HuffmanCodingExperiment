package huffman.hashmap;
// Huffman Coding Experiment with HashMap

// Objective
// We are implementing Huffman Coding using a HashMap for storing character-to-binary-code mappings. Our goal is to measure the efficiency of this data structure in terms of time, memory usage, and compression ratio

//  Experimental Setup
//  Configuration
// Number of Runs: 10 (to get average and variance)
// Input Size: 50,000 repetitions of a standard paragraph

// Metrics Measured
// 1. Execution Time (ns)
// 2. Memory Usage (bytes)
// 3. Total Bits Encoded
// 4. Compression Ratio
// 5. Throughput (bytes/sec)

// Implementation
// java

import huffman.adaptive.CompressionStrategy;
import java.util.*;

public class HuffmanHashMapStrategy implements CompressionStrategy {

    private boolean verbose = false;

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void compress(String input) {
        Map<Character, String> encodingMap = buildHuffmanEncoding(input);
        String encoded = encodeText(input, encodingMap);

        if (verbose) {
            System.out.println("HashMap Encoding Map: " + encodingMap);
            System.out.println("Encoded Bit String (truncated): " + encoded.substring(0, Math.min(64, encoded.length())) + "...");
            System.out.println("Encoded Length (bits): " + encoded.length());
        }
    }

    @Override
    public String getName() {
        return "HashMapHuffman";
    }


    public static Map<Character, String> buildHuffmanEncoding(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var entry : frequencyMap.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(null, left.freq + right.freq, left, right);
            pq.add(parent);
        }

        Map<Character, String> huffmanMap = new HashMap<>();
        generateCodes(pq.poll(), "", huffmanMap);
        return huffmanMap;
    }

    private static void generateCodes(Node node, String code, Map<Character, String> huffmanMap) {
        if (node == null) return;
        if (node.ch != null) huffmanMap.put(node.ch, code);
        generateCodes(node.left, code + "0", huffmanMap);
        generateCodes(node.right, code + "1", huffmanMap);
    }

    public static String encodeText(String text, Map<Character, String> huffmanMap) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(huffmanMap.get(c));
        }
        return sb.toString();
    }

    static class Node {
        Character ch;
        int freq;
        Node left, right;
        Node(Character ch, int freq) { this.ch = ch; this.freq = freq; }
        Node(Character ch, int freq, Node left, Node right) {
            this.ch = ch; this.freq = freq; this.left = left; this.right = right;
        }
    }
}


// ## Next Steps
// - Run with larger datasets
// - Compare with a Trie-based approach
// - Measure decoding efficiency
