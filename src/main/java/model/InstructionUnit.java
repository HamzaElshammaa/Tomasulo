package model;

public class InstructionUnit {
    private String operation;
    private String destination;
    private String source1;
    private String source2;
    private int cyclesRemaining;
    private int issueTime;
    private int startTime;
    private int completeTime;
    
    public InstructionUnit(String operation, String destination, String source1, String source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
        this.cyclesRemaining = -1; // Will be set based on operation type
        this.issueTime = -1;
        this.startTime = -1;
        this.completeTime = -1;
    }
    
    // Getters and setters
} 