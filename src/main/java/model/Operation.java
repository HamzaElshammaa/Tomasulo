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
}
