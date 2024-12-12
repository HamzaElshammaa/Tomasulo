package model;

public class Tag {
    public enum source {
        A, //waiting for Add
        M, //waitng for Mult
        REG, //Register
        FP_REG, //floating point REG
        IMM
    }
    public int index;
}
