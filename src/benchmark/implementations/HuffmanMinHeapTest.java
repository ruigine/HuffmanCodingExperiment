package benchmark.implementations;

import benchmark.Blackhole;
import benchmark.CompressionBenchmark;
import huffman.minheap.*;

public class HuffmanMinHeapTest implements CompressionBenchmark {
    @Override
    public void compress(int numRuns, String inputText) {
        for (int i = 0; i < numRuns; i++) {
            System.gc();
            HuffmanMinHeapStrategy huffman = new HuffmanMinHeapStrategy(inputText);
            CompressedData result = huffman.compressAndReturn(inputText);
            Blackhole.consume(result);
        }
    }
}
