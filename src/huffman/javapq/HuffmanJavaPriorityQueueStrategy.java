package huffman.javapq;

import huffman.minheap.CompressedData;
import huffman.minheap.HuffmanNode;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import huffman.adaptive.CompressionStrategy;

public class HuffmanJavaPriorityQueueStrategy implements CompressionStrategy{
    private final Map<Character, String> charToCode = new HashMap<>();
    private node root;
    private boolean verbose = false;
    private CompressedData lastCompressed;

    public HuffmanJavaPriorityQueueStrategy() {
    }

    public HuffmanJavaPriorityQueueStrategy(String input) {
        Map<Character, Integer> freqMap = countFrequencies(input);
        buildTree(freqMap);
        buildCodeTable(root, "");
    }

    public Map<Character, String> getCharToCode() {
        return charToCode;
    }

    @Override
    public void compress(String input) {
        Map<Character, Integer> freqMap = countFrequencies(input);
        buildTree(freqMap);
        buildCodeTable(root, "");
        lastCompressed = compressAndReturn(input);

        if (verbose) {
            System.out.println("📦 Encoded bit length: " + lastCompressed.bitLength);
        }
    }

    @Override
    public String getName() {
        return "JavaPriorityQueue";
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String decompress() {
        if (lastCompressed == null || root == null) {
            throw new IllegalStateException("Must call compress() before decompress().");
        }
        return decompress(lastCompressed);
    }

    // Step 1: Count character frequencies
    private Map<Character, Integer> countFrequencies(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {   // O(n) time for iterating through the string
            freqMap.put(text.charAt(i), freqMap.getOrDefault(text.charAt(i), 0) + 1);
        }

        return freqMap;  // O(k) space (freqMap stores unique characters)
    }

    // Step 2: Build Huffman Tree using Java's PriorityQueue
    private void buildTree(Map<Character, Integer> freqMap) {
        PriorityQueue<node> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.weight, b.weight));

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            node left = pq.poll();
            node right = pq.poll();
            node merged = new node('\0', left.weight + right.weight, left, right);
            pq.add(merged);
        }

        root = pq.poll();
    }

    // Step 3: Build character-to-code and code-to-character maps via tree traversal
    private void buildCodeTable(node current, String code) {
        if (current == null) return;

        if (isLeaf(current)) {
            charToCode.put(current.ch, code);
            return;
        }

        buildCodeTable(current.left, code + '0');
        buildCodeTable(current.right, code + '1');
    }

    private static boolean isLeaf(node n) {
        return n.left == null && n.right == null;
    }

    // Nested class for tree node
    private static class node {
        char ch;
        int weight;
        node left, right;

        node(char ch, int weight) {
            this.ch = ch;
            this.weight = weight;
        }

        node(char ch, int weight, node left, node right) {
            this.ch = ch;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }
    }

    public CompressedData compressAndReturn(String text) {
        int bitLength = 0;
        for (int i = 0; i < text.length(); i++) {
            bitLength += charToCode.get(text.charAt(i)).length();
        }

        int byteLen = (bitLength + 7) / 8;
        byte[] output = new byte[byteLen];

        int byteIndex = 0;
        int bitIndex = 0;
        int currentByte = 0;

        for (int i = 0; i < text.length(); i++) {
            String code = charToCode.get(text.charAt(i));
            for (int j = 0; j < code.length(); j++) {
                currentByte <<= 1;
                if (code.charAt(j) == '1') currentByte |= 1;
                bitIndex++;

                if (bitIndex == 8) {
                    output[byteIndex++] = (byte) currentByte;
                    bitIndex = 0;
                    currentByte = 0;
                }
            }
        }

        if (bitIndex > 0) {
            currentByte <<= (8 - bitIndex);
            output[byteIndex] = (byte) currentByte;
        }

        return new CompressedData(output, bitLength);
    }

    public String decompress(CompressedData compressed) {
        StringBuilder decoded = new StringBuilder();
        node current = root;

        int bitCount = 0;
        for (int i = 0; i < compressed.data.length && bitCount < compressed.bitLength; i++) {
            byte b = compressed.data[i];
            for (int j = 7; j >= 0 && bitCount < compressed.bitLength; j--) {
                int bit = (b >> j) & 1;
                current = (bit == 0) ? current.left : current.right;

                if (isLeaf(current)) {
                    decoded.append(current.ch);
                    current = root;
                }

                bitCount++;
            }
        }

        return decoded.toString();
    }
}
