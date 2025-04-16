package benchmark.implementations;

import benchmark.CompressionBenchmark;
import huffman.adaptive.SelfAdaptiveCompressor;

import java.util.ArrayList;
import java.util.List;

public class HuffmanAdaptiveTest implements CompressionBenchmark {

    private SelfAdaptiveCompressor compressor;

    @Override
    public void compress(int numRuns, String inputText) {
        // If compressor isn't prepared yet, initialize and prepare
        if (compressor == null) {
            compressor = new SelfAdaptiveCompressor(false);

            // Simulate a batch of varied inputs to choose smallest & largest
            List<String> samples = new ArrayList<>();
            samples.add(inputText); // large one
            samples.add(inputText.substring(0, Math.min(20, inputText.length()))); // small one

            compressor.prepare(samples);
        }

        for (int i = 0; i < numRuns; i++) {
            System.gc();
            compressor.compress(inputText);
        }
    }
}
