/*
package model;

//Clear logic when bus broadcasts tag same as yours

import javax.xml.crypto.Data;

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
    private Tag addressTag;
    private boolean addressReady;
    private int cycles;
    private boolean executed;
    private final Bus bus;
    private final int latency;
    private int enterTime = -1;
    private boolean addedToWriteBackQueue = false;
    private DataMemory dataMemory;

    public Buffer(Tag tag, BufferType type, int latency, Bus bus, DataMemory dataMemory) {
        this.tag = tag;
        this.type = type;
        this.latency = latency;
        this.bus = bus;
        this.dataMemory = dataMemory;
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
        this.addressTag = null;
    }

    // Issue a new memory operation using a CompiledInstruction
    public void issue(CompiledInstruction instruction, int enterTime) {
        this.busy = true;
        this.cycles = latency;
        this.enterTime = enterTime;
        this.executed = false;

        if (type == BufferType.STORE) {
            // For store operations, set the value source from the instruction's source2 tag
            this.valueSourceTag = instruction.source1;
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

    public void ExecuteLoad(){
        DataMemory.MemoryAccessResult result = dataMemory.load(addressTag.index);
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
        Cache cache = new Cache(1,1,1,1);
        DataMemory dataMemory = new DataMemory(2,cache);

        // Create load and store buffers with tags as names
        Buffer loadBuffer = new Buffer(new Tag(Tag.Source.L, 0), Buffer.BufferType.LOAD, 3, bus, dataMemory);
        Buffer storeBuffer = new Buffer(new Tag(Tag.Source.S, 1), Buffer.BufferType.STORE, 3, bus, dataMemory);

        // Create compiled instructions
        CompiledInstruction loadInstruction = new CompiledInstruction(
            new Operation(Operation.OperationType.LOAD), new Tag(Tag.Source.L, 100), null, null);
        CompiledInstruction storeInstruction = new CompiledInstruction(
            new Operation(Operation.OperationType.STORE), new Tag(Tag.Source.S, 200), new Tag(Tag.Source.REG, 1), null);

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


}*/
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
    private Tag addressTag;
    private boolean addressReady;
    private int cycles;
    private boolean executed;
    private final Bus bus;
    private final int latency;
    private int enterTime = -1;
    private boolean addedToWriteBackQueue = false;
    private DataMemory dataMemory;
    private RegisterFile fpRegisterFile;
    private RegisterFile intRegisterFile;

    public Buffer(Tag tag, BufferType type, int latency, Bus bus, DataMemory dataMemory, RegisterFile fpRegisterFile, RegisterFile intRegisterFile) {
        this.tag = tag;
        this.type = type;
        this.latency = latency;
        this.bus = bus;
        this.dataMemory = dataMemory;
        this.fpRegisterFile = fpRegisterFile;
        this.intRegisterFile = intRegisterFile;
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
        this.addressTag = null;
    }

    // Issue a new memory operation using a CompiledInstruction
    public void issue(CompiledInstruction instruction, int enterTime) {
        this.busy = true;
        this.cycles = latency;
        this.enterTime = enterTime;
        this.executed = false;

        if (type == BufferType.STORE) {
            // For store operations, set the value source from the instruction's source1 tag
            this.valueSourceTag = instruction.source1;
        }
        this.addressTag = instruction.destination; // Assuming destination holds the address
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

    public void execute() {
        if (type == BufferType.LOAD) {
            executeLoad();
        } else if (type == BufferType.STORE) {
            executeStore();
        }
    }

    private void executeLoad() {
        DataMemory.MemoryAccessResult result = dataMemory.load(addressTag.index);
        if (result.success) {
            this.value = result.value;
            this.cycles = result.latency;
        } else {
            System.out.println("Load error: " + result.error);
        }
    }

    private void executeStore() {
        double valueToStore = getValueFromRegister(valueSourceTag); // Retrieve value from source register
        DataMemory.MemoryAccessResult result = dataMemory.store(addressTag.index, valueToStore);
        if (!result.success) {
            System.out.println("Store error: " + result.error);
        }
    }

    private double getValueFromRegister(Tag sourceTag) {
        if (sourceTag.source == Tag.Source.FP_REG) {
            return fpRegisterFile.getRegister(sourceTag.index).value;
        } else if (sourceTag.source == Tag.Source.REG) {
            return intRegisterFile.getRegister(sourceTag.index).value;
        } else {
            throw new IllegalArgumentException("Invalid source tag for register value retrieval: " + sourceTag);
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
}