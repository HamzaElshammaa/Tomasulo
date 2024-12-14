package model;

import java.util.*;

public class DataMemory {
    private final int size;                          // Memory size in bytes
    private final Map<Integer, Double> memory;       // Address -> Value mapping
    private final Cache cache;                       // Associated cache
    private final Map<Integer, Boolean> busyAddresses; // Track memory access conflicts

    public DataMemory(int size, Cache cache) {
        this.size = size;
        this.memory = new HashMap<>();
        this.cache = cache;
        this.busyAddresses = new HashMap<>();
    }

    public static class MemoryAccessResult {
        public final boolean success;    // Whether access was successful
        public final int latency;        // Total cycles needed
        public final Double value;       // Value read/written
        public final String error;       // Error message if any

        public MemoryAccessResult(boolean success, int latency, Double value, String error) {
            this.success = success;
            this.latency = latency;
            this.value = value;
            this.error = error;
        }
    }

    // Load value from memory
    public MemoryAccessResult load(int address) {
        // Check if address is valid
        if (address < 0 || address >= size) {
            return new MemoryAccessResult(false, 0, null,
                    "Invalid address: " + address);
        }

        // Check if address is busy (being written to)
        if (isBusy(address)) {
            return new MemoryAccessResult(false, 0, null,
                    "Address " + address + " is busy");
        }

        // Try to read from cache first
        Cache.CacheAccessResult cacheResult = cache.read(address);

        // If cache miss, load from main memory to cache
        if (!cacheResult.isHit) {
            double value = memory.getOrDefault(address, 0.0);
            memory.put(address, value);
        }

        return new MemoryAccessResult(true, cacheResult.latency,
                cacheResult.value, null);
    }

    // Store value to memory
    public MemoryAccessResult store(int address, double value) {
        // Check if address is valid
        if (address < 0 || address >= size) {
            return new MemoryAccessResult(false, 0, null,
                    "Invalid address: " + address);
        }

        // Mark address as busy
        setBusy(address, true);

        // Write to cache (write-through policy)
        Cache.CacheAccessResult cacheResult = cache.write(address, value);

        // Update main memory
        memory.put(address, value);

        // Mark address as not busy
        setBusy(address, false);

        return new MemoryAccessResult(true, cacheResult.latency, value, null);
    }

    // Reserve address for upcoming store
    public synchronized boolean reserveAddress(int address) {
        if (isBusy(address)) {
            return false;
        }
        setBusy(address, true);
        return true;
    }

    // Release reserved address
    public synchronized void releaseAddress(int address) {
        setBusy(address, false);
    }

    // Check if address is busy
    public boolean isBusy(int address) {
        return busyAddresses.getOrDefault(address, false);
    }

    // Set busy status for address
    private void setBusy(int address, boolean busy) {
        busyAddresses.put(address, busy);
    }

    // Preload memory with values
    public void preloadMemory(Map<Integer, Double> values) {
        for (Map.Entry<Integer, Double> entry : values.entrySet()) {
            int address = entry.getKey();
            if (address >= 0 && address < size) {
                memory.put(address, entry.getValue());
            }
        }
    }

    // Get memory state for display
    public Map<Integer, MemoryState> getMemoryState() {
        Map<Integer, MemoryState> state = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : memory.entrySet()) {
            state.put(entry.getKey(), new MemoryState(
                    entry.getValue(),
                    isBusy(entry.getKey())
            ));
        }
        return state;
    }

    // Memory state class for GUI display
    public static class MemoryState {
        public final double value;
        public final boolean busy;

        public MemoryState(double value, boolean busy) {
            this.value = value;
            this.busy = busy;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Data Memory State:\n");

        // Sort the addresses for consistent display
        List<Integer> sortedAddresses = new ArrayList<>(memory.keySet());
        Collections.sort(sortedAddresses);

        for (Integer address : sortedAddresses) {
            sb.append("Address[").append(address).append("] -> ");
            sb.append("Value: ").append(memory.getOrDefault(address, 0.0));
            sb.append(", Busy: ").append(isBusy(address));
            sb.append("\n");
        }

        if (sortedAddresses.isEmpty()) {
            sb.append("Memory is empty\n");
        }

        return sb.toString();
    }


}