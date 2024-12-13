package model;

public class Buffer {
    public enum BufferType {
        LOAD,
        STORE
    }

    private final Tag tag; // Use Tag as the name/identifier of the buffer
    private final BufferType type;
    private boolean busy;
    private Double value;
    private Tag valueSourceTag;
    private boolean addressReady;
    private int cycles;
    private boolean executed;
    private final Bus bus;
    private final int latency;
    private int enterTime = -1;
    private boolean addedToWriteBackQueue = false;

    public Buffer(Tag tag, BufferType type, int latency, Bus bus) {
        this.tag = tag;
        this.type = type;
        this.latency = latency;
        this.bus = bus;
        clear();
    }

    public boolean isBusy() {
        return busy;
    }
    public void clear() {
        this.busy = false;
        this.value = null;
        this.valueSourceTag = null;
        this.addressReady = false;
        this.cycles = 0;
        this.executed = false;
        this.addedToWriteBackQueue = false;
    }

    // Issue a new memory operation using a CompiledInstruction
    public void issue(CompiledInstruction instruction, int enterTime) {
        this.busy = true;
        this.cycles = latency;
        this.enterTime = enterTime;
        this.executed = false;

        if (type == BufferType.STORE) {
            // For store operations, set the value source from the instruction's source2 tag
            this.valueSourceTag = instruction.source2;
        }
    }

    public void setStoreValue(Double value, Tag sourceTag) {
        if (type != BufferType.STORE) {
            throw new IllegalStateException("Cannot set store value for load buffer");
        }
        this.value = value;
        this.valueSourceTag = sourceTag;
    }

    public void updateValue(Tag sourceTag, double value) {
        if (valueSourceTag != null && valueSourceTag.equals(sourceTag)) {
            this.value = value;
            this.valueSourceTag = null;
        }
    }

    public boolean isReadyToExecute() {
        if (!busy || cycles <= 0 || executed) {
            return false;
        }

        if (type == BufferType.LOAD) {
            return addressReady;
        } else {
            return addressReady && value != null && valueSourceTag == null;
        }
    }

    public boolean executeCycle() {
        if (!isReadyToExecute()) {
            return false;
        }

        cycles--;
        if (cycles == 0) {
            executed = true;
            broadcastResult();
            return true;
        }
        return false;
    }

    private void broadcastResult() {
        if (type == BufferType.LOAD || type == BufferType.STORE) {
            bus.addToWritebackQueue(new BusData(tag, new Q(Q.DataType.R, value)), enterTime);
        }
    }

    public void runCycle() {
        if (executed && !addedToWriteBackQueue) {
            broadcastResult();
            addedToWriteBackQueue = true;
        } else if (busy) {
            executeCycle();
        }
    }

    // Getters and setters...

    // Main method for testing
    public static void main(String[] args) {
        // Create a bus
        Bus bus = new Bus();

        // Create load and store buffers with tags as names
        Buffer loadBuffer = new Buffer(new Tag(Tag.Source.L, 0), Buffer.BufferType.LOAD, 3, bus);
        Buffer storeBuffer = new Buffer(new Tag(Tag.Source.S, 1), Buffer.BufferType.STORE, 3, bus);

        // Create compiled instructions
        CompiledInstruction loadInstruction = new CompiledInstruction(
            new Operation(CompiledInstruction.InstructionType.LOAD), new Tag(Tag.Source.L, 100), null, null);
        CompiledInstruction storeInstruction = new CompiledInstruction(
            new Operation(CompiledInstruction.InstructionType.STORE), new Tag(Tag.Source.S, 200), new Tag(Tag.Source.REG, 1), null);

        // Issue operations using compiled instructions
        System.out.println("Issuing load operation to Load1...");
        loadBuffer.issue(loadInstruction, 0);
        loadBuffer.addressReady = true;
        printBufferState(loadBuffer);

        System.out.println("Issuing store operation to Store1...");
        storeBuffer.issue(storeInstruction, 0);
        storeBuffer.addressReady = true;
        printBufferState(storeBuffer);

        // Simulate value update
        System.out.println("Broadcasting result from REG1...");
        storeBuffer.updateValue(new Tag(Tag.Source.REG, 1), 3.14);
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

    // Helper method to print the state of a buffer
    private static void printBufferState(Buffer buffer) {
        System.out.println("Buffer: " + buffer.tag);
        System.out.println("  Type: " + buffer.type);
        System.out.println("  Busy: " + buffer.busy);
        System.out.println("  Value: " + buffer.value);
        System.out.println("  Value Source Tag: " + buffer.valueSourceTag);
        System.out.println("  Address Ready: " + buffer.addressReady);
        System.out.println("  Remaining Cycles: " + buffer.cycles);
        System.out.println("  Executed: " + buffer.executed);
        System.out.println();
    }
}