package benchmark;

/**
 * Utility class for preventing JVM optimization in benchmarks.
 */
public class Blackhole {
    // Volatile field to prevent compiler optimizations
    private static volatile Object sink;

    /**
     * Consumes the provided object to prevent dead code elimination during benchmarking.
     *
     * @param obj The object to consume
     */
    public static void consume(Object obj) {
        // Simple assignment to volatile field ensures the value cannot be optimized away
        sink = obj;
    }

    /**
     * Consumes a primitive int value by converting it to an Integer.
     *
     * @param value The int value to consume
     */
    public static void consume(int value) {
        sink = value;
    }

    /**
     * Consumes a primitive long value by converting it to a Long.
     *
     * @param value The long value to consume
     */
    public static void consume(long value) {
        sink = value;
    }

    /**
     * Consumes a primitive double value by converting it to a Double.
     *
     * @param value The double value to consume
     */
    public static void consume(double value) {
        sink = value;
    }

    /**
     * Consumes a primitive boolean value by converting it to a Boolean.
     *
     * @param value The boolean value to consume
     */
    public static void consume(boolean value) {
        sink = value;
    }

    /**
     * Resets the sink to null (optional, typically not needed).
     */
    public static void reset() {
        sink = null;
    }
}
