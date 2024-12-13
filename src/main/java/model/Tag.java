package model;

public class Tag {
    public enum source {
        A, //waiting for Add
        M, //waitng for Mult
        L,
        REG, //Register
        FP_REG, //floating point REG
        IMM
    }
    public source source;
    public int index;
}
