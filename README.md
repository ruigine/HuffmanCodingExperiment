# G1T5

This project focuses on experimenting with various implementations of the **Huffman Coding** text compression algorithm. The goal is to analyse the performance of the algorithm by exploring trade-offs between different data structures and algorithmic configurations, assessing their impact on compression efficiency.

## Project Structure

- **src/**
   - **datastructures/**: Contains basic data structures used across the project.
  - **experiment/**: Contains a class for evaluating the different compression strategies.
  - **huffman/**: This folder contains the **Huffman coding** algorithm implementations, which is implemented using 5 different methods/data structures
      - **adaptive/**: Selects the most efficient compression strategy of the below implementations
      - **binarytree/**: Binary Tree implementation
      - **hashmap/**: HashMap implementation
      - **javapq/**: Java's built-in Priority Queue implementation
      - **minheap/**: Min-Heap implementation
  - **test/**: Contains tests for the various implementations in the project.
  - **benchmark/**: Contains the compression and decompression benchmarking helpers and scripts for evaluation of the implementations.
- **classes/** (contains the compiled .class files)

## Requirements

- **JDK 24 or later**: Ensure you have Java 24+ installed on your system.

### To Verify Your Java Version:
```bash
java -version
```

## How to Compile

1. Run the batch file to compile the Java classes:
   ```bash
   compile.sh
   ```

## How to Run the Project

### Running Individual Tests

The **run.sh** file allows you to execute the compression and decompression benchmarking scripts.

1. Execute the following batch file (comment out either CompressionBenchmarkScript or DecompressionBenchmarkScript):
   ```bash
   run.sh
   ```

### Output

When the batch file is run, it will execute the compression/decompression benchmarks.

Compression Phase:
For each implementation, a CSV file is generated mapping input text length to compression time (in nanoseconds).

Decompression Phase:
For the binary tree, min heap and Java priority queue implementations, a CSV file is generated mapping input text length to decompression time (in nanoseconds) for each implementation.

These CSV files can be used for performance analysis or plotted for visualization.