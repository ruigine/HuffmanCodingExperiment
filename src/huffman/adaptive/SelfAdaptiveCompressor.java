package huffman.adaptive;

import java.util.*;

import huffman.binarytree.HuffmanBinaryTreeStrategy;
import huffman.hashmap.HuffmanHashMapStrategy;
import huffman.javapq.HuffmanJavaPriorityQueueStrategy;
import huffman.minheap.HuffmanMinHeapStrategy;

public class SelfAdaptiveCompressor {

    private final List<CompressionStrategy> candidates;
    private final Map<String, CompressionStrategy> strategyCache = new HashMap<>();
    private final Map<CompressionStrategy, String> lastDiagnostics = new HashMap<>();
    private final boolean verbose;

    // 🔧 Scoring Weights
    private static final double TIME_WEIGHT = 0.5;
    private static final double MEMORY_WEIGHT = 0.2;
    private static final double ENCODED_BITS_WEIGHT = 0.3;

    public SelfAdaptiveCompressor(boolean verbose) {
        this.verbose = verbose;
        candidates = List.of(
            new HuffmanHashMapStrategy(),
            new HuffmanBinaryTreeStrategy(),
            new HuffmanMinHeapStrategy(),
            new HuffmanJavaPriorityQueueStrategy()
        );
        for (CompressionStrategy strategy : candidates) {
            strategy.setVerbose(verbose);
        }
    }

    // 🚀 Prepare by analyzing smallest and largest input in advance
    public void prepare(List<String> inputs) {
        if (inputs == null || inputs.isEmpty()) return;
    
        String smallest = inputs.get(0);
        String largest = inputs.get(0);
    
        for (String input : inputs) {
            if (input.length() < smallest.length()) {
                smallest = input;
            }
            if (input.length() > largest.length()) {
                largest = input;
            }
        }
    
        CompressionStrategy bestSmall = runProfilingAndShowAll(smallest, "SMALL");
        CompressionStrategy bestLarge = runProfilingAndShowAll(largest, "LARGE");
    
        strategyCache.put("SMALL", bestSmall);
        strategyCache.put("LARGE", bestLarge);
    }

    private CompressionStrategy runProfilingAndShowAll(String input, String label) {
        CompressionStrategy bestStrategy = runProfilingOnSample(input.substring(0, Math.min(1000, input.length())));
        System.out.printf("🔁 Final Selected Strategy [%s Input] %s: %s%n",
                label, label.equals("LARGE") ? "📦" : "🧪", bestStrategy.getName());
    
        System.out.println("📊 Strategy Comparison for [" + label + "] input:");
        for (CompressionStrategy strategy : candidates) {
            String marker = strategy == bestStrategy ? "✅" : "  ";
            System.out.printf("%s %s => %s%n", marker, strategy.getName(), lastDiagnostics.getOrDefault(strategy, "⚠️ No data"));
        }
    
        return bestStrategy;
    }

    private String classifyInput(String input) {
        return input.length() >= 5000 ? "LARGE" : "SMALL";
    }

    public void compress(String input) {
        String category = classifyInput(input);
        CompressionStrategy strategyToUse = strategyCache.get(category);

        if (strategyToUse == null) {
            System.err.println("⚠️ Strategy not prepared for category: " + category);
            return;
        }

        if (verbose) {
            System.out.println("🚀 Compressing using: " + strategyToUse.getName());
        }

        strategyToUse.compress(input);
    }

    public CompressionStrategy runProfilingOnSample(String sample) {
        if (verbose) {
            System.out.println("🔍 Profiling sample strategies (multi-metric)...");
        }

        long bestScore = Long.MAX_VALUE;
        CompressionStrategy bestStrategy = null;
        lastDiagnostics.clear();

        for (CompressionStrategy strategy : candidates) {
            if (verbose) {
                System.out.println("🔎 Testing: " + strategy.getName());
            }

            System.gc();
            long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startTime = System.nanoTime();

            CompressionProfiler profiler = new CompressionProfiler();
            long encodedBits = profiler.runAndMeasure(strategy, sample);

            long endTime = System.nanoTime();
            long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long timeTaken = endTime - startTime;
            long memoryUsed = endMem - startMem;

            long weightedScore = (long) (
                TIME_WEIGHT * timeTaken +
                MEMORY_WEIGHT * memoryUsed +
                ENCODED_BITS_WEIGHT * encodedBits
            );

            String diag = String.format("⏱️ Time: %d ns, 🧠 Memory: %d B, 📦 Bits: %d, 🧮 Cost Score: %d",
                    timeTaken, memoryUsed, encodedBits, weightedScore);

            lastDiagnostics.put(strategy, diag);

            if (verbose) {
                System.out.println("📊 " + strategy.getName() + " => " + diag);
            }

            if (weightedScore < bestScore) {
                bestScore = weightedScore;
                bestStrategy = strategy;
            }
        }

        if (bestStrategy != null && verbose) {
            System.out.println("✅ Best Strategy: " + bestStrategy.getName());
            System.out.println("📈 Reason: Lowest weighted score based on time, memory, and bits.");
            System.out.println("🔍 Breakdown: " + lastDiagnostics.get(bestStrategy));
        }

        return bestStrategy;
    }

    public String getSelectedStrategyName(String category) {
        CompressionStrategy strategy = strategyCache.get(category);
        return strategy != null ? strategy.getName() : "None";
    }
}
