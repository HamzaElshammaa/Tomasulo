package model;
import java.util.HashMap;
import java.util.Map;


public class RegisterFile {

    
    private static final int NUM_INT_REGISTERS = 32;
    private static final int NUM_FP_REGISTERS = 32;
    
    private class Register {
        private double value;
        private String Qi;  // Name of reservation station producing result, null if value is ready

        public Register() {
            this.value = 0.0;
            this.Qi = null;
        }

    }

    private Register[] integerRegisters;
    private Register[] fpRegisters;

    public RegisterFile() {
        // Initialize integer registers (R0-R31)
        integerRegisters = new Register[NUM_INT_REGISTERS];
        for (int i = 0; i < NUM_INT_REGISTERS; i++) {
            integerRegisters[i] = new Register();
        }
        
        // Initialize floating-point registers (F0-F31)
        fpRegisters = new Register[NUM_FP_REGISTERS];
        for (int i = 0; i < NUM_FP_REGISTERS; i++) {
            fpRegisters[i] = new Register();
        }
    }

    // Get register value if available (not waiting for result)
    public Double getValue(String registerName) {
        Register reg = getRegisterByName(registerName);
        return (reg.qi == null) ? reg.value : null;
    }

    // Get reservation station name if register is waiting for result
    public String getQi(String registerName) {
        return getRegisterByName(registerName).qi;
    }

    // Set register value and clear reservation station dependency
    public void setValue(String registerName, double value) {
        Register reg = getRegisterByName(registerName);
        reg.value = value;
        reg.qi = null;
    }

    // Set reservation station dependency
    public void setQi(String registerName, String stationName) {
        getRegisterByName(registerName).qi = stationName;
    }

    // Helper method to get register by name (e.g., "R1", "F2")
    private Register getRegisterByName(String registerName) {
        if (registerName == null || registerName.length() < 2) {
            throw new IllegalArgumentException("Invalid register name: " + registerName);
        }

        char type = registerName.charAt(0);
        int index;
        try {
            index = Integer.parseInt(registerName.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid register number: " + registerName);
        }

        if (type == 'R' || type == 'r') {
            if (index >= 0 && index < NUM_INT_REGISTERS) {
                return integerRegisters[index];
            }
        } else if (type == 'F' || type == 'f') {
            if (index >= 0 && index < NUM_FP_REGISTERS) {
                return fpRegisters[index];
            }
        }
        throw new IllegalArgumentException("Invalid register name: " + registerName);
    }

    // Load initial values into registers
    public void loadInitialValues(Map<String, Double> initialValues) {
        for (Map.Entry<String, Double> entry : initialValues.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
    }

    // Get a copy of current register states for display
    public Map<String, RegisterState> getRegisterStates() {
        Map<String, RegisterState> states = new HashMap<>();
        
        // Add integer registers
        for (int i = 0; i < NUM_INT_REGISTERS; i++) {
            Register reg = integerRegisters[i];
            states.put("R" + i, new RegisterState(reg.value, reg.qi));
        }
        
        // Add floating-point registers
        for (int i = 0; i < NUM_FP_REGISTERS; i++) {
            Register reg = fpRegisters[i];
            states.put("F" + i, new RegisterState(reg.value, reg.qi));
        }
        
        return states;
    }

    // Helper class for returning register state
    public static class RegisterState {
        public final double value;
        public final String qi;

        public RegisterState(double value, String qi) {
            this.value = value;
            this.qi = qi;
        }
    }
} 
