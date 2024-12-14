package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static model.Tag.Source.*;

public class InstructionQueue {

    private List<CompiledInstruction> instructions; // List of Instruction objects
    private int currentInstructionIndex; // Tracks the current instruction index
    private final Bus bus;
    private boolean branching;
    private final RegisterFile fp_registerFile;
    private final RegisterFile int_registerFile;
    private Q address;
    private Q e1;
    private Q e2;
    private boolean expectedBranchResult;

    public InstructionQueue(List<String> rawInstructions, Bus bus, RegisterFile fpRegisterFile, RegisterFile intRegisterFile) {
        this.instructions = parseInstructions(rawInstructions);
        this.fp_registerFile = fpRegisterFile;
        this.currentInstructionIndex = 0; // Start at the first instruction
        this.bus = bus;
        this.int_registerFile = intRegisterFile;
        this.branching = false;
    }

    public boolean isBranching() {
        return branching;
    }

    private static Operation.OperationType determineOperationType(String opCode) {
        switch (opCode) {
            case "ADD.D": return Operation.OperationType.FP_ADD;
            case "SUB.D": return Operation.OperationType.FP_SUB;
            case "MUL.D": return Operation.OperationType.FP_MULT;
            case "DIV.D": return Operation.OperationType.FP_DIV;
            case "ADD": return Operation.OperationType.ADD;
            case "SUB": return Operation.OperationType.SUB;
            case "DADDI": return Operation.OperationType.ADD;
            case "DSUBI": return Operation.OperationType.SUB;
            case "BNE": return Operation.OperationType.BNE;
            case "BEQ": return Operation.OperationType.BEQ;
            default: throw new IllegalArgumentException("Unknown operation: " + opCode);
        }
    }

    public static List<String> loadRawInstructions(String filePath) {
        List<String> rawInstructions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    rawInstructions.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading instructions from file: " + e.getMessage());
        }
        return rawInstructions;
    }

    private List<CompiledInstruction> parseInstructions(List<String> rawInstructions) {
        List<CompiledInstruction> instructionList = new ArrayList<>();
        for (String rawInstruction : rawInstructions) {
            CompiledInstruction instruction = parseInstruction(rawInstruction);
            instructionList.add(instruction);
        }
        return instructionList;
    }

    private CompiledInstruction parseInstruction(String line) {
        String[] parts = line.split(" ");
        Operation operation = new Operation(determineOperationType(parts[0]));
        Tag destination = parts.length > 1 ? Tag.parseTag(parts[1]) : null;
        Tag source1 = parts.length > 2 ? Tag.parseTag(parts[2]) : null;
        Tag source2 = parts.length > 3 ? Tag.parseTag(parts[3]) : null;

        return new CompiledInstruction(operation, destination, source1, source2);
    }

    public CompiledInstruction fetchNextInstruction() {
        if (branching || currentInstructionIndex >= instructions.size()) {
            return null; // Halt fetching if branching or end of instructions
        }
        return instructions.get(currentInstructionIndex++);
    }

    public boolean isEmpty() {
        return currentInstructionIndex >= instructions.size();
    }

    public int size() {
        return instructions.size();
    }

    @Override
    public String toString() {
        return instructions.toString();
    }

    public void BRANCH(Tag R1, Tag R2, int targetIndex) {
        branching = true;
        this.address = new Q(Q.DataType.R, targetIndex);

        Q registerValue1 = getRegisterValue(R1);
        Q registerValue2 = getRegisterValue(R2);

        this.e1 = new Q(registerValue1.type, registerValue1.value);
        this.e2 = new Q(registerValue2.type, registerValue2.value);
    }

    public void BEQ(Tag R1, Tag R2, int targetIndex) {
        BRANCH(R1, R2, targetIndex);
        expectedBranchResult = true;
    }

    public void BNE(Tag R1, Tag R2, int targetIndex) {
        BRANCH(R1, R2, targetIndex);
        expectedBranchResult = false;
    }

    public static boolean QAndTagCompare(Q q, Tag tag) {
        boolean typeMatches = switch (q.type) {
            case A -> tag.source == A;
            case M -> tag.source == M;
            case L -> tag.source == L;
            default -> false;
        };
        boolean valueMatches = q.value == tag.index;
        return typeMatches && valueMatches;
    }

    public void updateOperands() {
        BusData busData = bus.getBusData();
        if (e1.type != Q.DataType.R && QAndTagCompare(e1, busData.tag)) {
            e1 = new Q(busData.dataValue.type, busData.dataValue.value);
        }
        if (e2.type != Q.DataType.R && QAndTagCompare(e2, busData.tag)) {
            e2 = new Q(busData.dataValue.type, busData.dataValue.value);
        }

        if (e1.type == Q.DataType.R && e2.type == Q.DataType.R) {
            boolean result = e1.value == e2.value;
            if (result == expectedBranchResult) {
                currentInstructionIndex = (int) address.value; // Jump to the target index
            }
            branching = false; // Clear branching status
        }
    }

    private Q getRegisterValue(Tag tag) {
        if (tag.source.equals(FP_REG)) {
            return fp_registerFile.getRegister(tag.index);
        } else {
            return int_registerFile.getRegister(tag.index);
        }
    }

    public List<CompiledInstruction> getInstructions() {
        return new ArrayList<>(this.instructions); // Return a copy of the instructions
    }
}
