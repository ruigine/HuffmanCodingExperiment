package huffman.minheap;

/**
 * A node in the Huffman tree.
 * Each node may represent a character (leaf node) or an internal node with left/right children.
 */
public class HuffmanNode {
    Character ch;
    Integer freq;
    HuffmanNode left;
    HuffmanNode right;

    /**
     * Constructs a leaf Huffman node with a given character and its frequency.
     *
     * @param ch The character.
     * @param freq The frequency of the character.
     */
    HuffmanNode(Character ch, Integer freq) {
        this.ch = ch;
        this.freq = freq;
    }

    /**
     * Constructs an internal Huffman node by combining two child nodes.
     *
     * @param left The left child.
     * @param right The right child.
     */
    HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.ch = '\0'; // internal node
        this.freq = left.freq + right.freq;
        this.left = left;
        this.right = right;
    }

    /**
     * Checks if this node is a leaf (i.e., has no children).
     *
     * @return true if leaf, false otherwise.
     */
    boolean isLeaf() {
        return left == null && right == null;
    }
}