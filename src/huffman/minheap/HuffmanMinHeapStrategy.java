package huffman.minheap;

import java.util.HashMap;
import java.util.Map;
import huffman.adaptive.CompressionStrategy;
import datastructures.HeapPriorityQueue;
import datastructures.Entry;

public class HuffmanMinHeapStrategy implements CompressionStrategy {
    private HuffmanNode tree;
    private Map<Character, String> map;
    private boolean verbose = false;

    public HuffmanMinHeapStrategy() {
        // default constructor
    }

    public HuffmanMinHeapStrategy(String text) {
        this.tree = buildHuffmanTree(text);
        this.map = encodingMap(tree);
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void compress(String input) {
        HuffmanMinHeapStrategy compressor = new HuffmanMinHeapStrategy(input);
        CompressedData compressed = compressor.compressAndReturn(input);

        if (verbose) {
            System.out.println("MinHeap Encoding Map: " + compressor.getMap());
            System.out.println("Compressed bit length: " + compressed.bitLength);
            System.out.println("Compressed bytes (truncated): " + byteArrayPreview(compressed.data));
        }
    }

    @Override
    public String getName() {
        return "MinHeapHuffman";
    }

    public Map<Character, String> getMap() {
        return this.map;
    }

    public CompressedData compressAndReturn(String text) {
        int bitLength = 0;
        for (int i = 0; i < text.length(); i++) {
            bitLength += this.map.get(text.charAt(i)).length();
        }

        int byteLen = (bitLength + 7) / 8;
        byte[] output = new byte[byteLen];

        int byteIndex = 0;
        int bitIndex = 0;
        int currentByte = 0;

        for (int i = 0; i < text.length(); i++) {
            String code = this.map.get(text.charAt(i));
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
        HuffmanNode current = tree;

        int bitCount = 0;
        for (int i = 0; i < compressed.data.length && bitCount < compressed.bitLength; i++) {
            byte b = compressed.data[i];
            for (int j = 7; j >= 0 && bitCount < compressed.bitLength; j--) {
                int bit = (b >> j) & 1;
                current = (bit == 0) ? current.left : current.right;

                if (current.isLeaf()) {
                    decoded.append(current.ch);
                    current = tree;
                }

                bitCount++;
            }
        }

        return decoded.toString();
    }

    public HuffmanNode buildHuffmanTree(String text) {
        Map<Character, Integer> freqMap = getFrequencyMap(text);
        int size = freqMap.size();
        Integer[] keys = new Integer[size];
        HuffmanNode[] values = new HuffmanNode[size];
        int i = 0;

        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            keys[i] = entry.getValue();
            values[i] = new HuffmanNode(entry.getKey(), keys[i]);
            i++;
        }

        HeapPriorityQueue<Integer, HuffmanNode> heap = new HeapPriorityQueue<>(keys, values);

        while (heap.size() > 1) {
            Entry<Integer, HuffmanNode> e1 = heap.removeMin();
            Entry<Integer, HuffmanNode> e2 = heap.removeMin();

            HuffmanNode merged = new HuffmanNode(e1.getValue(), e2.getValue());
            heap.insert(merged.freq, merged);
        }

        return heap.removeMin().getValue();
    }

    public Map<Character, Integer> getFrequencyMap(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }
        return freqMap;
    }

    public Map<Character, String> encodingMap(HuffmanNode root) {
        Map<Character, String> codeMap = new HashMap<>();
        encodingMap(root, "", codeMap);
        return codeMap;
    }

    private void encodingMap(HuffmanNode node, String code, Map<Character, String> map) {
        if (node == null) return;

        if (node.isLeaf()) {
            map.put(node.ch, code);
            return;
        }

        encodingMap(node.left, code + "0", map);
        encodingMap(node.right, code + "1", map);
    }

    private String byteArrayPreview(byte[] data) {
        int limit = Math.min(data.length, 10);
        StringBuilder preview = new StringBuilder("[");
        for (int i = 0; i < limit; i++) {
            preview.append(data[i]);
            if (i < limit - 1) preview.append(", ");
        }
        if (data.length > limit) preview.append(", ...");
        preview.append("]");
        return preview.toString();
    }
}
