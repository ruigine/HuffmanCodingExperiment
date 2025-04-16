package huffman.minheap;

/**
 * A container class to hold the compressed data and the actual number of valid bits.
*/
public class CompressedData {
    public final byte[] data;
    public final int bitLength;

    /**
     * Constructs a CompressedData object.
     *
     * @param data      The byte array containing compressed data.
     * @param bitLength The number of meaningful bits in the compressed data.
     */
    public CompressedData(byte[] data, int bitLength) {
        this.data = data;
        this.bitLength = bitLength;
    }

    @Override
    public String toString() {
        StringBuilder bitString = new StringBuilder();

        int bitCount = 0;
        for (int i = 0; i < data.length && bitCount < bitLength; i++) {
            byte b = data[i];
            for (int j = 7; j >= 0 && bitCount < bitLength; j--) {
                int bit = (b >> j) & 1;
                bitString.append(bit);
                bitCount++;
            }
        }

        return bitString.toString();
    }
}