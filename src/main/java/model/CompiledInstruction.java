package model;

public class CompiledInstruction {
//    public enum InstructionType {
//        FP_ADD,     // ADD.D, SUB.D
//        FP_MULT,    // MUL.D, DIV.D
//        INT_ADD,    // ADDI, SUBI
//        LOAD,       // L.D, LW
//        STORE,      // S.D, SW
//        BRANCH      // BEQ, BNE
//    }

    Operation operation; //needed for rs communication
    public Tag destination;
    public Tag source1;
    public Tag source2;

    public CompiledInstruction(Operation operation, Tag destination, Tag source1, Tag source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
    }

    public Operation getOperation() {
        return operation;
    }


    public String toString() {
        return operation + " " + destination + " " + source1 + " " + source2 + "\n";
    }
}