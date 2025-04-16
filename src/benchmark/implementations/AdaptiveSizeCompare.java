package benchmark.implementations;

import benchmark.CompressionBenchmark;
import huffman.adaptive.SelfAdaptiveCompressor;

import java.util.List;

public class AdaptiveSizeCompare implements CompressionBenchmark {
    @Override
    public void compress(int numRuns, String inputText) {
        System.out.println("📊 Running Adaptive Strategy Comparison");

        String smallSample = inputText.substring(0, Math.min(500, inputText.length()));
        String largeSample = inputText;

        // ✅ Create compressor with verbose ON to see details
        SelfAdaptiveCompressor compressor = new SelfAdaptiveCompressor(true);

        // 🔧 Prepare once using both input sizes
        compressor.prepare(List.of(smallSample, largeSample));

        // 🔁 Compress once for each to see result and verify behavior
        System.out.println("\n🔎 Compressing SMALL input:");
        compressor.compress(smallSample);
        System.out.println("✅ Selected strategy (SMALL): " + compressor.getSelectedStrategyName("SMALL"));

        System.out.println("\n🔎 Compressing LARGE input:");
        compressor.compress(largeSample);
        System.out.println("✅ Selected strategy (LARGE): " + compressor.getSelectedStrategyName("LARGE"));
    }
}
