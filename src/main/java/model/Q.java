package model;

public class Q {
    public enum DataType {
        A, //waiting for Add
        M, //waitng for Mult
        L, //waiting for load values from memory
        R //real value
    }

    public DataType type;
    public int value;

}
