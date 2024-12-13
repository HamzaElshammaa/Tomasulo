package model;

public class Tag {

    public enum Source {
        A, // Need to check if Register is waiting for another operation (in reservation station)
        M, // Need to check if Register is waiting for another operation (in reservation station)
        REG, //Register
        FP_REG, //floating point REG
        IMM,
        L,
        S
    }

    public Source source;
    public int index;

    public Tag(Source source, int index) {
        this.source = source;
        this.index = index;
    }

    public String toString() {
        return "Tag{" +
                "source=" + source +
                ", index=" + index +
                '}';
    }

    public static Tag parseTag(String str) {
        if (str.startsWith("F")) {
            int index = Integer.parseInt(str.substring(1));
            return new Tag(Source.FP_REG, index);
        } else if (str.startsWith("R")) {
            int index = Integer.parseInt(str.substring(1));
            return new Tag(Source.REG, index);
        } else if (isNumeric(str)) {
            int value = Integer.parseInt(str);
            return new Tag(Source.IMM, value);
        } else {
            throw new IllegalArgumentException("Invalid tag format: " + str);
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}