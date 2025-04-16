package benchmark.implementations;

import benchmark.Blackhole;
import benchmark.CompressionBenchmark;
import huffman.binarytree.*;

public class HuffmanBinaryTreeTest implements CompressionBenchmark {
    @Override
    public void compress(int numRuns, String inputText) {
        for (int i = 0; i < numRuns; i++) {
            System.gc();
            var result = HuffmanBinaryTreeStrategy.huffman(inputText);
            Blackhole.consume(result);
        }
    }
}
