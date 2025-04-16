package experiment;
import java.util.*;

public class ExperimentMetrics {
    public long totalTimeNs = 0;
    public long totalMemoryBytes = 0;
    public long totalBitsEncoded = 0;
    public List<Long> runTimes = new ArrayList<>();
    public String dataStructureName;
    public int inputSizeBytes;

    public ExperimentMetrics(String name, int inputSizeBytes) {
        this.dataStructureName = name;
        this.inputSizeBytes = inputSizeBytes;
    }

    public void logRun(long timeNs, long memBytes, long bitsEncoded) {
        totalTimeNs += timeNs;
        totalMemoryBytes += memBytes;
        totalBitsEncoded += bitsEncoded;
        runTimes.add(timeNs);
    }

    public void printSummary(int NUM_RUNS) {
        double avgTimeMs = totalTimeNs / 1_000_000.0 / NUM_RUNS;
        double avgMemoryMB = totalMemoryBytes / 1024.0 / 1024.0 / NUM_RUNS;
        double avgThroughput = inputSizeBytes / (totalTimeNs / 1_000_000_000.0 / NUM_RUNS);
        double avgCodeLength = (double) totalBitsEncoded / inputSizeBytes;
        double compressionRatio = (double) totalBitsEncoded / (inputSizeBytes * 8);

        System.out.println("=== " + dataStructureName + " ===");
        System.out.printf("Input Size: %d bytes%n", inputSizeBytes);
        System.out.printf("Avg Runtime: %.2f ms%n", avgTimeMs);
        System.out.printf("Avg Memory Usage: %.2f MB%n", avgMemoryMB);
        System.out.printf("Throughput: %.2f bytes/sec%n", avgThroughput);
        System.out.printf("Avg Code Length: %.2f bits/char%n", avgCodeLength);
        System.out.printf("Compression Ratio: %.2f%n", compressionRatio);
        System.out.println("----------------------------");
    }

    public static String generateInputText(int repeat) {
        String sentence = """
                In a village of La Mancha, the name of which I have no desire to call to mind, 
                there lived not long since one of those gentlemen that keep a lance in the lance-rack, 
                an old buckler, a lean hack, and a greyhound for coursing. A stew pan for a helmet, 
                and a bookshelf for a sword—this was a man of oddity, noble in his own imagination.
                """;
        return sentence.repeat(repeat);
    }
}
