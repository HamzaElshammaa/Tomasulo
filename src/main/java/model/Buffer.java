
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
    private Tag addressTag;
    private Tag destination;
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

    public Tag getTag() {
        return tag;
    }
    public BufferType getType() {return type;}

    public boolean isBusy() {
        return busy;
    }

    public void clear() {
        this.busy = false;
        this.value = null;
        this.addressTag = null;
        this.addressReady = false;
        this.cycles = 0;
        this.executed = false;
        this.addedToWriteBackQueue = false;
        this.destination = null;
    }

    // Issue a new memory operation using a CompiledInstruction
    public void issue(CompiledInstruction instruction, int enterTime) {
        this.busy = true;
        this.cycles = latency+1;
        this.enterTime = enterTime;
        this.executed = false;


        this.addressTag = instruction.source1;
        this.destination = instruction.destination; // Assuming destination holds the address
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
        value = getValueFromRegister(addressTag); // Retrieve value from source register
        DataMemory.MemoryAccessResult result = dataMemory.store(addressTag.index, value);
        if (!result.success) {
            System.out.println("Store error: " + result.error);
        }
    }

    private double getValueFromRegister(Tag sourceTag) {
        if (destination.source == Tag.Source.FP_REG) {
            return fpRegisterFile.getRegister(destination.index).value;
        } else if (destination.source == Tag.Source.REG) {
            return intRegisterFile.getRegister(destination.index).value;
        } else {
            throw new IllegalArgumentException("Invalid source tag for register value retrieval: " + sourceTag);
        }
    }

    public boolean executeCycle() {
        if (cycles <= -1) {
            return false;
        }

        cycles--;
        if (cycles == -1) {
            executed = true;
            execute();
            return true;
        }
        return false;
    }



    public void clearCurrentStation(){
        if (bus.getBusData().tag == this.tag){
            clear();
        }
    }


    public void runCycle() {
        if (executed && !addedToWriteBackQueue) {
            bus.addToWritebackQueue(new BusData(this.tag, new Q(Q.DataType.L, value)), enterTime);
            addedToWriteBackQueue = true;

        } else if (busy) {
            executeCycle();
        }
        clearCurrentStation();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nBuffer: ").append(tag).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Busy: ").append(busy).append("\n");
        sb.append("Value: ").append(value != null ? value : "None").append("\n");
        sb.append("Address  Tag: ").append(addressTag != null ? addressTag : "None").append("\n");
        sb.append("Destination Tag: ").append(destination != null ? destination : "None").append("\n");
        sb.append("Address Ready: ").append(addressReady).append("\n");
        if (cycles > -1) {
            sb.append("Cycles Remaining: ").append(cycles).append("\n");
        } else if (executed) {
            sb.append("Cycles Remaining: Execution Complete\n");
        } else {
            sb.append("Cycles Remaining: Not Started\n");
        }
        sb.append("Executed: ").append(executed).append("\n");
        sb.append("Added to WriteBack Queue: ").append(addedToWriteBackQueue).append("\n");
        return sb.toString();
    }
}