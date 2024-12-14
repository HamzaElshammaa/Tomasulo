package model;

public class RegisterFile {
    private final Q[] qi; // Array of Q objects representing registers
    private final Bus bus;

    // Constructor with initial values
    public RegisterFile(double [] initialValues, Bus bus) {
        this.bus = bus;
        if (initialValues == null || initialValues.length == 0) {
            throw new IllegalArgumentException("Initial values array must not be null or empty.");
        }

        // Initialize the qi array with the size of initialValues
        this.qi = new Q[initialValues.length];

        // Populate the qi array with initial values
        for (int i = 0; i < initialValues.length; i++) {
            this.qi[i] = new Q(Q.DataType.R, 0);
            this.qi[i].type = Q.DataType.R; // Default to real value (ready)
            this.qi[i].value = initialValues[i]; // Assign the initial value
        }
    }

    // Get the Q object for a specific register
    public Q getRegister(int index) {
        if (index < 0 || index >= qi.length) {
            throw new IndexOutOfBoundsException("Invalid register index: " + index);
        }
        return qi[index];
    }

    // Set the Q object for a specific register
    public void setRegister(int index, Q newQ) {
        if (index < 0 || index >= qi.length) {
            throw new IndexOutOfBoundsException("Invalid register index: " + index);
        }
        qi[index] = newQ;
    }

    // Method to update the register file based on the tag on the bus
    public void updateRegisterFile() {
        BusData busData = bus.getBusData(); // Fetch the current bus data
        Tag tag = busData.tag;

        for (int i = 0; i < qi.length; i++) {
            Q currentQ = qi[i];

            if (QAndTagCompare(currentQ, tag)) {
                // Update the Q object in the register file
                currentQ.type = Q.DataType.R; // Set to ready
                currentQ.value = busData.dataValue.value; // Update the value
                System.out.println("Register R[" + i + "] updated with value: " + busData.dataValue.value);
            }
        }
    }

    // Compare a Q object and a Tag (same as in the reservation station logic)
    public static boolean QAndTagCompare(Q q, Tag tag) {
        if (q == null || tag == null) {
            return false;
        }

        boolean typeMatches = false;

        switch (q.type) {
            case A:
                typeMatches = tag.source == Tag.Source.A;
                break;
            case M:
                typeMatches = tag.source == Tag.Source.M;
                break;
            case L:
                typeMatches = tag.source == Tag.Source.REG;
                break;
            default:
                break;
        }

        return typeMatches && q.value == tag.index; // Both type and value must match
    }

    // Override toString to display the state of all registers in the desired format
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Register File State:\n");
        for (int i = 0; i < qi.length; i++) {
            sb.append("R[").append(i).append("] -> ")
                    .append(qi[i].type).append(" ")
                    .append(qi[i].value).append("\n");
        }
        return sb.toString();
    }

}
