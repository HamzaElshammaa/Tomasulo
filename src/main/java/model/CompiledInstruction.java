package model;

public class CompiledInstruction {

    Operation operation; //needed for rs communication
    Tag destination;
    Tag source1;
    Tag source2;



    public CompiledInstruction(Operation operation, Tag destination, Tag source1, Tag source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
    }

    public Operation getOperation() {
        return operation;
    }

    public Tag getDestination() {
        return destination;
    }

    public Tag getSource1() {
        return source1;
    }

    public Tag getSource2() {
        return source2;
    }
    public String toString() {
        return operation + " " + destination + " " + source1 + " " + source2 + "\n";
    }
}