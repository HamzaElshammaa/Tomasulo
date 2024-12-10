package model;




public class InstructionUnit {
    public enum InstructionType{
        FP_ADD, //ADD.D, SUB.D
        FP_MULT, //MUL.D, DIV.D
        INT_ADD, // ADDI, SUBI
        LOAD, //L.D, LW
        STORE, //S.D, SW
        BRANCH // BEQ, BNE
    }
    private String operation; // op name ex "ADD.D, LW" .. etc
    private String destination; //Destination register
    private String source1; //Source register 1
    private String source2; //Source register 2
    private int immediate; // immediate value 
    private InstructionType type; //Type of instruction
// execution status
    private int issueTime; //when instruction was issued 
    private int startTime; // when instruction started
    private int completeTime; // when instruction completed
    private int writeTime; //when instruction result was written
    private int cyclesRemaining; // total cycles needed for execution
    private String assignedStation; // assigned reservation station
    private boolean isExecuting; 
    private boolean isComplete;
    private boolean isWritten;

    
    public InstructionUnit(String operation, String destination, String source1, String source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
        this.cyclesRemaining = -1; // Will be set based on operation type
        this.type = determineType(operation);
        //init execution status
        this.issueTime = -1;
        this.startTime = -1;
        this.completeTime = -1;
        this.assignedStation = null;
        this.isComplete = false;
        this.isExecuting = false;
        this.isWritten = false;
    }

    private InstructionType deteInstructionType(String operation){
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
                throw new IllegalArgumentException("Unkown operation"+operation);
        }
    }

    //Issue the instruction
    public void issue(int cycle, String stationName){
        this.issueTime = cycle;
        this.assignedStation = stationName;
    }

    //Start execution
    public void startExecution(int cycle){
        this.startTime = cycle;
        this.isExecuting = true;
    }

    //Complete execution
    public void completeExecution(int cycle){
        this.completeTime = cycle;
        this.isExecuting = false;
        this.isComplete = true;
    }

    //write result
    public void writeResult(int cycle){
        this.writeTime = cycle;
        this.isWritten = true;
    }

    //Calculate instruction status
    public String getStatus(){
        if (issueTime == -1) return "Not Issued";
        if (!isExecuting && !isComplete) return "Issued";
        if(isExecuting) return "Executing";
        if(isComplete && !isWritten) return "Completed";
        if(isWritten) return "Written";
        return "Unkown";
    }

    //Get execution metrics
    public InstructionMetrics getMetrics(){
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
    public InstructionType getType() { return type; }
    public String getAssignedStation() { return assignedStation; }
    public boolean isExecuting() { return isExecuting; }
    public boolean isComplete() { return isComplete; }
    public boolean isWritten() { return isWritten; }
    public int getTotalLatency() { return totalLatency; }

    // Setters
    public void setTotalLatency(int latency) { this.totalLatency = latency; }
} public static class InstructionMetrics{
    public int issueTime;
    public int startTime;
    public int completeTime;
    public int writeTime;
    public String status;

    public InstructionMetrics(int issueTime, int startTime, int completeTime, int writeTime, String status){
        this.issueTime = issueTime;
        this.completeTime = completeTime;
        this.startTime = startTime;
        this.writeTime = writeTime;
        this.status = status;
    }

}

@Override
public String toString(){
    
}