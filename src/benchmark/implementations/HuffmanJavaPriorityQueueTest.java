package benchmark.implementations;

import benchmark.Blackhole;
import benchmark.CompressionBenchmark;
import huffman.minheap.CompressedData;
import huffman.javapq.HuffmanJavaPriorityQueueStrategy;

public class HuffmanJavaPriorityQueueTest implements CompressionBenchmark {
    @Override
    public void compress(int numRuns, String inputText) {
        for (int i = 0; i < numRuns; i++) {
            System.gc();
            HuffmanJavaPriorityQueueStrategy huffman = new HuffmanJavaPriorityQueueStrategy(inputText);
            CompressedData result = huffman.compressAndReturn(inputText);
            Blackhole.consume(result);
        }
    }
}
