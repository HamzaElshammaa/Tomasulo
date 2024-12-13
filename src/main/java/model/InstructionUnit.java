package model;
public class InstructionUnit {
    public enum InstructionType {
        FP_ADD,     // ADD.D, SUB.D
        FP_MULT,    // MUL.D, DIV.D
        INT_ADD,    // ADDI, SUBI
        LOAD,       // L.D, LW
        STORE,      // S.D, SW
        BRANCH      // BEQ, BNE
    }

    //{String "ADD", String "F6", String "F8", String "F8"}

    private String operation;         // Operation name (e.g., "ADD.D", "L.D")
    private String destination;       // Destination register
    private String source1;          // First source register
    private String source2;          // Second source register
    private int immediate;           // Immediate value or memory offset
//    private InstructionType type;    // Type of instruction

    
    // Execution status
    private int issueTime;           // Cycle when instruction was issued //DON'T NEED ITT
    private int startTime;           // Cycle when execution started
    private int completeTime;        // Cycle when execution completed
    private int writeTime;           // Cycle when result was written
    private int totalLatency;        // Total cycles needed for execution //DON'T NEED IT
    private String assignedStation;   // Assigned reservation station
    private boolean isExecuting;     // Whether instruction is currently executing
    private boolean isComplete;      // Whether instruction has completed execution
    private boolean isWritten;       // Whether result has been written

    public InstructionUnit(String operation, String destination, String source1, String source2, int immediate) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
        this.immediate = immediate;
//        this.type = determineType(operation);
        
        // Initialize execution status
//        this.issueTime = -1; DON'T NEED IT
        this.startTime = -1;
//        this.completeTime = -1;
        this.writeTime = -1;
        this.isExecuting = false;
        this.isComplete = false;
        this.isWritten = false;
        this.assignedStation = null;
    }

    private InstructionType determineType(String operation) {
        switch (operation.toUpperCase()) {
            case "ADD.D":
            case "SUB.D":
                return InstructionType.FP_ADD;
            case "MUL.D":
            case "DIV.D":
                return InstructionType.FP_MULT;
            case "ADDI":
            case "SUBI":
                return InstructionType.INT_ADD;
            case "L.D":
            case "LW":
                return InstructionType.LOAD;
            case "S.D":
            case "SW":
                return InstructionType.STORE;
            case "BEQ":
            case "BNE":
                return InstructionType.BRANCH;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }

    // Issue the instruction
    public void issue(int cycle, String stationName) {
        this.issueTime = cycle;
        this.assignedStation = stationName;
    }

    // Start execution
    public void startExecution(int cycle) {
        this.startTime = cycle;
        this.isExecuting = true;
    }

    // Complete execution
    public void completeExecution(int cycle) {
        this.completeTime = cycle;
        this.isExecuting = false;
        this.isComplete = true;
    }

    // Write result
    public void writeResult(int cycle) {
        this.writeTime = cycle;
        this.isWritten = true;
    }

    // Calculate instruction status
    public String getStatus() {
        if (issueTime == -1) return "Not Issued";
        if (!isExecuting && !isComplete) return "Issued";
        if (isExecuting) return "Executing";
        if (isComplete && !isWritten) return "Completed";
        if (isWritten) return "Written";
        return "Unknown";
    }

    // Get execution metrics
    public InstructionMetrics getMetrics() {
        return new InstructionMetrics(
            issueTime,
            startTime,
            completeTime,
            writeTime,
            getStatus()
        );
    }

    // Getters
    public String getOperation() { return operation; }
    public String getDestination() { return destination; }
    public String getSource1() { return source1; }
    public String getSource2() { return source2; }
    public int getImmediate() { return immediate; }
//    public InstructionType getType() { return type; }
    public String getAssignedStation() { return assignedStation; }
    public boolean isExecuting() { return isExecuting; }
    public boolean isComplete() { return isComplete; }
    public boolean isWritten() { return isWritten; }
    public int getTotalLatency() { return totalLatency; }

    // Setters
    public void setTotalLatency(int latency) { this.totalLatency = latency; }

    // Metrics class for tracking instruction timing
    public static class InstructionMetrics {
        public final int issueTime;
        public final int startTime;
        public final int completeTime;
        public final int writeTime;
        public final String status;

        public InstructionMetrics(int issueTime, int startTime, 
                                int completeTime, int writeTime, 
                                String status) {
            this.issueTime = issueTime;
            this.startTime = startTime;
            this.completeTime = completeTime;
            this.writeTime = writeTime;
            this.status = status;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation).append(" ");
        
//        if (type != InstructionType.BRANCH) {
//            sb.append(destination);
//        }
//
//        if (type == InstructionType.LOAD || type == InstructionType.STORE) {
//            sb.append(", ").append(immediate).append("(").append(source1).append(")");
//        } else if (type == InstructionType.BRANCH) {
//            sb.append(source1).append(", ").append(source2).append(", ").append(immediate);
//        } else {
//            sb.append(", ").append(source1);
//            if (source2 != null) {
//                sb.append(", ").append(source2);
//            } else if (type == InstructionType.INT_ADD) {
//                sb.append(", ").append(immediate);
//            }
//        }
        
        return sb.toString();
    }
}