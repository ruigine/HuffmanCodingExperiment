package huffman.adaptive;

public interface CompressionStrategy {
    void compress(String input);     
    String getName();            

    default void setVerbose(boolean verbose) {
    }
}
