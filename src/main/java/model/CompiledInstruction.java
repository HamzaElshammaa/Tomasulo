package model;

public class CompiledInstruction {
    public enum InstructionType {
        FP_ADD,     // ADD.D, SUB.D
        FP_MULT,    // MUL.D, DIV.D
        INT_ADD,    // ADDI, SUBI
        LOAD,       // L.D, LW
        STORE,      // S.D, SW
        BRANCH      // BEQ, BNE
    }

    InstructionType instructionOperation;
    Operation operation; //needed for rs communication
    Tag destination;
    Tag source1;
    Tag source2;

    public CompiledInstruction(InstructionUnit instruction) { //given as a string ex: MUL R1 R2 R3
//        switch case (instruction.operation) {
//            "R","F", default:
//        }

        //{FP_ADD, {FP_REG 1}, {FP_REG 2}, {IMM 5}}
        this.instructionOperation = instructionOperation;
        this.destination = source1;
        this.source1 = source1;
        this.source2 = source2;
    }
}
