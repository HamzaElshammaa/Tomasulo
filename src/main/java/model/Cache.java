/*package model;
import java.util.HashMap;
import java.util.Map;

//cash
public class Cache {
    private final int cacheSize;      // in bytes
    private final int blockSize;      // in bytes
    private final int numberOfBlocks;
    private final int hitLatency;     // cycles
    private final int missLatency;    // cycles
    private boolean firstAccess = true;

    private static class CacheBlock {
        boolean valid;
        int tag;
        byte[] data;

        public CacheBlock(int blockSize) {
            this.valid = false;
            this.tag = -1;
            this.data = new byte[blockSize];
        }
    }

    private final CacheBlock[] blocks;
    private final Map<Integer, Double> mainMemory; // Simulated main memory: address -> value

    public Cache(int cacheSize, int blockSize, int hitLatency, int missLatency) {
        this.cacheSize = cacheSize;
        this.blockSize = blockSize;
        this.hitLatency = hitLatency;
        this.missLatency = missLatency;
        this.numberOfBlocks = cacheSize / blockSize;

        // Initialize cache blocks
        this.blocks = new CacheBlock[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new CacheBlock(blockSize);
        }

        // Initialize simulated main memory
        this.mainMemory = new HashMap<>();
    }

    public CacheAccessResult read(int address) {
        int blockOffset = address % blockSize;
        int blockIndex = (address / blockSize) % numberOfBlocks;
        int tag = address / (blockSize * numberOfBlocks);

        CacheBlock block = blocks[blockIndex];

        // Cache hit
        if (block.valid && block.tag == tag) {
            return new CacheAccessResult(
                    true,
                    hitLatency,
                    getValueFromMainMemory(address)
            );
        }

        // Cache miss
        handleCacheMiss(address, blockIndex, tag);
        return new CacheAccessResult(
                false,
                missLatency,
                getValueFromMainMemory(address)
        );
    }

    public CacheAccessResult write(int address, double value) {
        int blockOffset = address % blockSize;
        int blockIndex = (address / blockSize) % numberOfBlocks;
        int tag = address / (blockSize * numberOfBlocks);

        CacheBlock block = blocks[blockIndex];

        // Update main memory
        mainMemory.put(address, value);

        // Cache hit
        if (block.valid && block.tag == tag) {
            // Write-through policy
            return new CacheAccessResult(true, hitLatency, value);
        }

        // Cache miss
        handleCacheMiss(address, blockIndex, tag);
        return new CacheAccessResult(false, missLatency, value);
    }

    private void handleCacheMiss(int address, int blockIndex, int tag) {
        CacheBlock block = blocks[blockIndex];
        block.valid = true;
        block.tag = tag;

        // Load the entire block from main memory
        int blockStartAddress = address - (address % blockSize);
        for (int i = 0; i < blockSize; i++) {
            int currentAddress = blockStartAddress + i;
            if (mainMemory.containsKey(currentAddress)) {
                // Convert double to bytes and store in block
                double value = mainMemory.get(currentAddress);
                // In a real implementation, we would convert the double to bytes here
                // For simulation purposes, we'll skip the actual byte storage
            }
        }
    }

    private Double getValueFromMainMemory(int address) {
        return mainMemory.getOrDefault(address, 0.0);
    }

    // Pre-load values into main memory
    public void preloadMemory(Map<Integer, Double> values) {
        mainMemory.putAll(values);
    }

    // Get cache statistics for display
    public CacheStatistics getStatistics() {
        int validBlocks = 0;
        for (CacheBlock block : blocks) {
            if (block.valid) validBlocks++;
        }
        return new CacheStatistics(
                cacheSize,
                blockSize,
                numberOfBlocks,
                validBlocks,
                hitLatency,
                missLatency
        );
    }

    // Result class for cache access operations
    public static class CacheAccessResult {
        public final boolean isHit;
        public final int latency;
        public final double value;

        public CacheAccessResult(boolean isHit, int latency, double value) {
            this.isHit = isHit;
            this.latency = latency;
            this.value = value;
        }
    }

    // Statistics class for GUI display
    public static class CacheStatistics {
        public final int cacheSize;
        public final int blockSize;
        public final int totalBlocks;
        public final int validBlocks;
        public final int hitLatency;
        public final int missLatency;

        public CacheStatistics(int cacheSize, int blockSize, int totalBlocks,
                               int validBlocks, int hitLatency, int missLatency) {
            this.cacheSize = cacheSize;
            this.blockSize = blockSize;
            this.totalBlocks = totalBlocks;
            this.validBlocks = validBlocks;
            this.hitLatency = hitLatency;
            this.missLatency = missLatency;
        }
    }
}*/

package model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache {
    private final int blockSize; // Number of bytes per block
    private final int cacheSize; // Total number of blocks the cache can hold
    private final int hitLatency;
    private final int missLatency;
    private final Map<Integer, byte[]> cacheData; // Cache stores blocks of bytes

    public Cache(int blockSize, int cacheSize, int hitLatency, int missLatency) {
        this.blockSize = blockSize;
        this.cacheSize = cacheSize;
        this.hitLatency = hitLatency;
        this.missLatency = missLatency;
        this.cacheData = new LinkedHashMap<Integer, byte[]>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, byte[]> eldest) {
                return size() > cacheSize;
            }
        };
    }

    public CacheAccessResult read(int address) {
        int blockAddress = address / blockSize;
        int offset = address % blockSize;

        if (!cacheData.containsKey(blockAddress)) {
            // Compulsory miss: Load block from memory
            byte[] block = loadBlockFromMemory(blockAddress);
            cacheData.put(blockAddress, block);
            return new CacheAccessResult(false, missLatency, block[offset]);
        } else {
            // Cache hit
            byte[] block = cacheData.get(blockAddress);
            return new CacheAccessResult(true, hitLatency, block[offset]);
        }
    }

    public CacheAccessResult write(int address, byte value) {
        int blockAddress = address / blockSize;
        int offset = address % blockSize;

        if (!cacheData.containsKey(blockAddress)) {
            // Compulsory miss: Load block from memory
            byte[] block = loadBlockFromMemory(blockAddress);
            cacheData.put(blockAddress, block);
        }

        // Cache hit: Write value
        byte[] block = cacheData.get(blockAddress);
        block[offset] = value;
        return new CacheAccessResult(true, hitLatency, value);
    }

    private byte[] loadBlockFromMemory(int blockAddress) {
        // Simulate loading a block from memory
        byte[] block = new byte[blockSize];
        for (int i = 0; i < blockSize; i++) {
            block[i] = 0; // Initialize with default values
        }
        return block;
    }

    public static class CacheAccessResult {
        public final boolean isHit;
        public final int latency;
        public final byte value;

        public CacheAccessResult(boolean isHit, int latency, byte value) {
            this.isHit = isHit;
            this.latency = latency;
            this.value = value;
        }
    }
}
