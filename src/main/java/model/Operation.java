package model;

public class Operation {
    public enum OperationType {
        ADD,
        SUB,
        MULT,
        DIV,
        LOAD,
        STORE,
        FP_ADD,
        FP_SUB,
        FP_MULT,
        FP_DIV,
    }

    public OperationType operationType;

    public Operation(CompiledInstruction.InstructionType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String toString() {
        return "operationType= " + operationType;
    }
}