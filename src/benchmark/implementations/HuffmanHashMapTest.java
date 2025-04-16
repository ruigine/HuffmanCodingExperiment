package benchmark.implementations;

import java.util.Map;

import benchmark.Blackhole;
import benchmark.CompressionBenchmark;
import huffman.hashmap.*;

public class HuffmanHashMapTest implements CompressionBenchmark {
    @Override
    public void compress(int numRuns, String inputText) {
        for (int i = 0; i < numRuns; i++) {
            System.gc();
            Map<Character, String> huffmanMap = HuffmanHashMapStrategy.buildHuffmanEncoding(inputText);
            var result = HuffmanHashMapStrategy.encodeText(inputText, huffmanMap);
            Blackhole.consume(result);
        }
    }
}
