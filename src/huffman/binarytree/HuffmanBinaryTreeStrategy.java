package huffman.binarytree;
import huffman.adaptive.CompressionStrategy;

// Time Complexity Analysis
// parseText method

// Creating frequency map: O(n) where n is the length of the input text
// Creating letters list: O(k) where k is the number of unique characters
// Sorting letters: O(k log k)
// Overall: O(n + k log k)

// buildTree method

// While loop runs k-1 times (combining nodes until only one remains)
// Each iteration:

// Removing first two elements: O(1) with LinkedList implementation
// Creating a new TreeNode: O(1)
// Adding node and sorting: O(k log k)


// Overall: O(k² log k) due to repeated sorting inside the loop

// traverseTree method

// Recursively visits each node in the tree once
// Time complexity: O(k) where k is the number of unique characters

// encodeAsBits method

// Iterates through the input text once: O(n)
// Each character lookup in encoding map is O(1)
// Overall: O(n)

// bitsToBytes method

// Processing each bit in the bitString: O(b) where b is the length of the bitString
// Since b is proportional to n, this is effectively O(n)

// huffman method (main algorithm)

// parseText: O(n + k log k)
// buildTree: O(k² log k)
// traverseTree: O(k)
// Creating encodingMap: O(k)
// encodeAsBits: O(n)
// bitsToBytes: O(n)
// Overall: O(n + k² log k)

// decode method

// Converting bytes to bits: O(b) where b is the number of bytes
// Creating the decoding map: O(k)
// Decoding the bitstring: O(b) in the worst case
// Overall: O(b + k), which is O(n + k) since b is proportional to n

// Space Complexity

// Frequency map: O(k)
// Letters list: O(k)
// Tree nodes: O(k)
// Encoding/decoding maps: O(k)
// Bit strings: O(n)
// Byte array: O(n)
// Overall: O(n + k)

import java.util.*;

public class HuffmanBinaryTreeStrategy implements CompressionStrategy{

    private boolean verbose = false;

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void compress(String input) {
        HuffmanResult result = huffman(input);
        
        if (verbose) {
            System.out.println("BinaryTree Encoding Map: " + result.encodingMap);
            System.out.println("Encoded bit string: " + result.bitString);
            System.out.println("Bytes: " + Arrays.toString(result.byteData));
        }
    }

    @Override
    public String getName() {
        return "BinaryTreeHuffman";
    }

    static class Letter {
        char letter;
        int freq;
        Map<Character, String> bitstring;

        public Letter(char letter, int freq) {
            this.letter = letter;
            this.freq = freq;
            this.bitstring = new HashMap<>();
        }

        @Override
        public String toString() {
            return letter + ":" + freq;
        }
    }

    static class TreeNode {
        int freq;
        Object left;  // Either Letter or TreeNode
        Object right; // Either Letter or TreeNode

        public TreeNode(int freq, Object left, Object right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    }

    private static List<Letter> parseText(String text) {
        Map<Character, Integer> chars = new HashMap<>();
        for (char c : text.toCharArray()) {
            chars.put(c, chars.getOrDefault(c, 0) + 1);
        }

        List<Letter> letters = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
            letters.add(new Letter(entry.getKey(), entry.getValue()));
        }

        return letters;
    }

    private static Object buildTree(List<Letter> letters) {
        // Create a priority queue with a custom comparator based on frequency
        PriorityQueue<Object> pq = new PriorityQueue<>((a, b) -> {
            int freqA = (a instanceof Letter) ? ((Letter) a).freq : ((TreeNode) a).freq;
            int freqB = (b instanceof Letter) ? ((Letter) b).freq : ((TreeNode) b).freq;
            return Integer.compare(freqA, freqB);
        });

        // Add all letters to the priority queue
        pq.addAll(letters);

        // Build the tree by combining nodes
        while (pq.size() > 1) {
            Object left = pq.poll();
            Object right = pq.poll();

            int leftFreq = (left instanceof Letter) ? ((Letter) left).freq : ((TreeNode) left).freq;
            int rightFreq;
            if ((right instanceof Letter)) {
                rightFreq = ((Letter) right).freq;
            } else {
                assert right != null;
                rightFreq = ((TreeNode) right).freq;
            }

            int totalFreq = leftFreq + rightFreq;
            TreeNode node = new TreeNode(totalFreq, left, right);

            pq.add(node);
        }

        return pq.poll();
    }

    private static List<Letter> traverseTree(Object root, String bitstring) {
        if (root instanceof Letter letter) {
            letter.bitstring.put(letter.letter, bitstring);
            return List.of(letter);
        }

        TreeNode treeNode = (TreeNode) root;
        List<Letter> letters = new ArrayList<>();

        letters.addAll(traverseTree(treeNode.left, bitstring + "0"));
        letters.addAll(traverseTree(treeNode.right, bitstring + "1"));

        return letters;
    }

    private static String encodeAsBits(String text, Map<Character, String> encodingMap) {
        StringBuilder bitString = new StringBuilder();
        for (char c : text.toCharArray()) {
            bitString.append(encodingMap.get(c));
        }
        return bitString.toString();
    }

    private static class ByteResult {
        byte[] data;
        int padding;

        public ByteResult(byte[] data, int padding) {
            this.data = data;
            this.padding = padding;
        }
    }

    private static ByteResult bitsToBytes(String bitString) {
        // Pad to multiple of 8
        int padding = (bitString.length() % 8 != 0) ? 8 - (bitString.length() % 8) : 0;
        String paddedBits = bitString + "0".repeat(padding);

        // Convert to bytes
        byte[] byteArray = new byte[paddedBits.length() / 8];
        for (int i = 0; i < paddedBits.length(); i += 8) {
            String byteStr = paddedBits.substring(i, i + 8);
            byteArray[i / 8] = (byte) Integer.parseInt(byteStr, 2);
        }

        return new ByteResult(byteArray, padding);
    }

    public static class HuffmanResult {
        public Map<Character, String> encodingMap;
        public String bitString;
        public byte[] byteData;
        public int padding;

        public HuffmanResult(Map<Character, String> encodingMap, String bitString, byte[] byteData, int padding) {
            this.encodingMap = encodingMap;
            this.bitString = bitString;
            this.byteData = byteData;
            this.padding = padding;
        }
    }

    public static HuffmanResult huffman(String text) {
        if (text.isEmpty()) {
            return new HuffmanResult(new HashMap<>(), "", new byte[0], 0);
        }

        List<Letter> lettersList = parseText(text);

        // Handle the special case of only one unique character
        if (lettersList.size() == 1) {
            Letter letter = lettersList.getFirst();
            Map<Character, String> encodingMap = new HashMap<>();
            encodingMap.put(letter.letter, "0");

            String bitString = "0".repeat(text.length());
            ByteResult byteResult = bitsToBytes(bitString);

            return new HuffmanResult(encodingMap, bitString, byteResult.data, byteResult.padding);
        }

        Object root = buildTree(lettersList);

        List<Letter> letters = traverseTree(root, "");
        Map<Character, String> encodingMap = new HashMap<>();
        for (Letter letter : letters) {
            encodingMap.put(letter.letter, letter.bitstring.get(letter.letter));
        }

        String bitString = encodeAsBits(text, encodingMap);
        ByteResult byteResult = bitsToBytes(bitString);

        return new HuffmanResult(encodingMap, bitString, byteResult.data, byteResult.padding);
    }

    public static String decode(byte[] byteData, Map<Character, String> encodingMap, int padding) {
        // Convert bytes back to bits
        StringBuilder bitString = new StringBuilder();
        for (byte b : byteData) {
            StringBuilder bits = new StringBuilder(Integer.toBinaryString(b & 0xFF));
            // Ensure each byte is represented as 8 bits
            while (bits.length() < 8) {
                bits.insert(0, "0");
            }
            bitString.append(bits);
        }

        // Remove padding
        if (padding > 0) {
            bitString.delete(bitString.length() - padding, bitString.length());
        }

        // Invert the encoding map for decoding
        StringBuilder decodedText = getDecodedText(encodingMap, bitString);

        return decodedText.toString();
    }

    private static StringBuilder getDecodedText(Map<Character, String> encodingMap, StringBuilder bitString) {
        Map<String, Character> decodingMap = new HashMap<>();
        for (Map.Entry<Character, String> entry : encodingMap.entrySet()) {
            decodingMap.put(entry.getValue(), entry.getKey());
        }

        // Decode the bit string
        StringBuilder decodedText = new StringBuilder();
        StringBuilder currentBits = new StringBuilder();

        for (int i = 0; i < bitString.length(); i++) {
            currentBits.append(bitString.charAt(i));
            if (decodingMap.containsKey(currentBits.toString())) {
                decodedText.append(decodingMap.get(currentBits.toString()));
                currentBits.setLength(0);
            }
        }
        return decodedText;
    }

    public static void main(String[] args) {
        String sampleText = "hello world";
        HuffmanResult result = huffman(sampleText);

        System.out.println("Original text: " + sampleText);
        System.out.println("Encoding map: " + result.encodingMap);
        System.out.println("Encoded bit string: " + result.bitString);
        System.out.println("Encoded as bytes: " + Arrays.toString(result.byteData));
        System.out.println("Padding bits: " + result.padding);

        // Calculate compression ratio
        int originalSize = sampleText.length() * 8;  // Assuming 8 bits per character (ASCII)
        int compressedSize = result.bitString.length();
        double compressionRatio = (double) (originalSize - compressedSize) / originalSize * 100;

        System.out.println("Original size (bits): " + originalSize);
        System.out.println("Compressed size (bits): " + compressedSize);
        System.out.printf("Compression ratio: %.2f%%\n", compressionRatio);

        // Decode and verify
        String decodedText = decode(result.byteData, result.encodingMap, result.padding);
        System.out.println("Decoded text: " + decodedText);
        System.out.println("Decoding successful: " + decodedText.equals(sampleText));
    }
}
