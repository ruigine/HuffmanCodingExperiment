package huffman.adaptive;
import java.util.*;

public class CompressionProfiler {
    public long runAndMeasure(CompressionStrategy strategy, String input) {
        BitCounterWrapper wrapper = new BitCounterWrapper();
        strategy.compress(input);
        return wrapper.estimateBits(input); // Simulated encoded size
    }
}

class BitCounterWrapper {
    public long estimateBits(String input) {
        // Simulated: ~4.5 bits/char (refined later if needed)
        return (long)(input.length() * 4.5);
    }
}
