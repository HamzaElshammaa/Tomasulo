package model;

import java.util.Objects;

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

    public Operation(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public boolean isOperationEqual(OperationType operation) {
        return operationType == operation;
    }

    public String toString() {
        return "operationType= " + operationType;
    }
}