package model;

public class RegisterFile {
    private final Q[] qi; // Array of Q objects representing registers

    // Constructor with initial values
    public RegisterFile(double [] initialValues) {
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


}
