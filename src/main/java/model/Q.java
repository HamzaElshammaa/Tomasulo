package model;

public class Q {
    public enum InstructionType {
        A, //waiting for Add
        M, //waitng for Mult
        R //real value
    }

    public InstructionType type;
    public int value;

}
