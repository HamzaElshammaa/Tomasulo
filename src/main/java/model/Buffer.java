package model;
//Latest Cache-Buffer-DataMemory
public class Buffer {
    public enum BufferType {
        LOAD,
        STORE
    }

    private final String name;          // Buffer identifier (e.g., "Load1", "Store1")
    private final BufferType type;      // Type of buffer
    private boolean busy;               // Whether buffer is in use
    private Integer address;            // Effective address
    private Double value;               // Value to be stored (for store buffer)
    private String valueSource;         // RS that will provide the value (for store buffer)
    private Integer calculatedAddress;  // Final calculated address
    private boolean addressReady;       // Whether address calculation is complete
    private int remainingCycles;        // Cycles left for memory operation
    private boolean executed;           // Whether memory operation is complete

    public Buffer(String name, BufferType type) {
        this.name = name;
        this.type = type;
        clear();
    }

    public void clear() {
        this.busy = false;
        this.address = null;
        this.value = null;
        this.valueSource = null;
        this.calculatedAddress = null;
        this.addressReady = false;
        this.remainingCycles = 0;
        this.executed = false;
    }

    // Issue a new memory operation to this buffer
    public void issue(Integer address, int latency) {
        this.busy = true;
        this.address = address;
        this.remainingCycles = latency;
        this.executed = false;
    }

    // For store buffer: set the value or its source
    public void setStoreValue(Double value, String source) {
        if (type != BufferType.STORE) {
            throw new IllegalStateException("Cannot set store value for load buffer");
        }
        this.value = value;
        this.valueSource = source;
    }

    // Update value when result is broadcast (for store buffer)
    public void updateValue(String source, double value) {
        if (valueSource != null && valueSource.equals(source)) {
            this.value = value;
            this.valueSource = null;
        }
    }

    // Check if ready to execute memory operation
    public boolean isReadyToExecute() {
        if (!busy || remainingCycles <= 0 || executed) {
            return false;
        }

        if (type == BufferType.LOAD) {
            return addressReady;
        } else {
            return addressReady && value != null && valueSource == null;
        }
    }

    // Execute one cycle of the memory operation
    public boolean executeCycle() {
        if (!isReadyToExecute()) {
            return false;
        }

        remainingCycles--;
        if (remainingCycles == 0) {
            executed = true;
            return true;
        }
        return false;
    }

    // Getters
    public String getName() { return name; }
    public BufferType getType() { return type; }
    public boolean isBusy() { return busy; }
    public Integer getAddress() { return address; }
    public Double getValue() { return value; }
    public String getValueSource() { return valueSource; }
    public Integer getCalculatedAddress() { return calculatedAddress; }
    public boolean isAddressReady() { return addressReady; }
    public int getRemainingCycles() { return remainingCycles; }
    public boolean isExecuted() { return executed; }

    // Setters
    public void setAddressReady(boolean ready) {
        this.addressReady = ready;
    }
    public void setCalculatedAddress(Integer address) {
        this.calculatedAddress = address;
        this.addressReady = true;
    }

    // Get buffer state for display
    public BufferState getState() {
        return new BufferState(
                name,
                type,
                busy,
                address,
                value,
                valueSource,
                calculatedAddress,
                addressReady,
                remainingCycles,
                executed
        );
    }

    // State class for GUI display
    public static class BufferState {
        public final String name;
        public final BufferType type;
        public final boolean busy;
        public final Integer address;
        public final Double value;
        public final String valueSource;
        public final Integer calculatedAddress;
        public final boolean addressReady;
        public final int remainingCycles;
        public final boolean executed;

        public BufferState(String name, BufferType type, boolean busy,
                           Integer address, Double value, String valueSource,
                           Integer calculatedAddress, boolean addressReady,
                           int remainingCycles, boolean executed) {
            this.name = name;
            this.type = type;
            this.busy = busy;
            this.address = address;
            this.value = value;
            this.valueSource = valueSource;
            this.calculatedAddress = calculatedAddress;
            this.addressReady = addressReady;
            this.remainingCycles = remainingCycles;
            this.executed = executed;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        if (!busy) {
            sb.append("Not busy");
        } else {
            sb.append("Busy, ");
            if (type == BufferType.LOAD) {
                sb.append("Loading from ");
            } else {
                sb.append("Storing ");
                if (value != null) {
                    sb.append(value);
                } else {
                    sb.append("(waiting for ").append(valueSource).append(")");
                }
                sb.append(" to ");
            }
            sb.append("address ");
            if (calculatedAddress != null) {
                sb.append(calculatedAddress);
            } else {
                sb.append("(calculating)");
            }
            if (executed) {
                sb.append(" [Completed]");
            } else {
                sb.append(" [").append(remainingCycles).append(" cycles remaining]");
            }
        }
        return sb.toString();
    }

    // Helper method to print the state of a buffer
    private static void printBufferState(Buffer buffer) {
        Buffer.BufferState state = buffer.getState();
        System.out.println("Buffer: " + state.name);
        System.out.println("  Type: " + state.type);
        System.out.println("  Busy: " + state.busy);
        System.out.println("  Address: " + state.address);
        System.out.println("  Value: " + state.value);
        System.out.println("  Value Source: " + state.valueSource);
        System.out.println("  Calculated Address: " + state.calculatedAddress);
        System.out.println("  Address Ready: " + state.addressReady);
        System.out.println("  Remaining Cycles: " + state.remainingCycles);
        System.out.println("  Executed: " + state.executed);
        System.out.println();
    }

    public static void main(String[] args) {
        // Create load and store buffers
        Buffer loadBuffer = new Buffer("Load1", Buffer.BufferType.LOAD);
        Buffer storeBuffer = new Buffer("Store1", Buffer.BufferType.STORE);

        // Issue a load operation
        System.out.println("Issuing load operation to Load1...");
        loadBuffer.issue(100, 3); // Load from address 100, 3 cycles latency
        loadBuffer.setCalculatedAddress(100); // Address is ready
        printBufferState(loadBuffer);

        // Issue a store operation
        System.out.println("Issuing store operation to Store1...");
        storeBuffer.issue(200, 3); // Store to address 200, 3 cycles latency
        storeBuffer.setStoreValue(null, "Add1"); // Waiting for value from Add1
        storeBuffer.setCalculatedAddress(200); // Address is ready
        printBufferState(storeBuffer);

        // Simulate Add1 completing and broadcasting its result
        System.out.println("Broadcasting result from Add1...");
        storeBuffer.updateValue("Add1", 3.14);
        printBufferState(storeBuffer);

        // Execute cycles for load buffer
        System.out.println("Executing cycles for Load1...");
        while (loadBuffer.isReadyToExecute()) {
            boolean completed = loadBuffer.executeCycle();
            printBufferState(loadBuffer);
            if (completed) {
                System.out.println("Load operation completed.");
            }
        }

        // Execute cycles for store buffer
        System.out.println("Executing cycles for Store1...");
        while (storeBuffer.isReadyToExecute()) {
            boolean completed = storeBuffer.executeCycle();
            printBufferState(storeBuffer);
            if (completed) {
                System.out.println("Store operation completed.");
            }
        }
    }

}