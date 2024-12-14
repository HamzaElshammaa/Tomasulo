package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import static model.Tag.Source.*;

public class InstructionQueue {

    public Queue<CompiledInstruction> instructions; // Queue of Instruction objects
    private final Bus bus;
    private boolean branching;
    private final RegisterFile fp_registerFile;
    private final RegisterFile int_registerFile;
    private Q e1;
    private Q e2;
    private Q address;
    private boolean expectedBranchResult;
    public static String filePath = "D:\\Uni\\Semester 7\\Microprocessors\\Tomasulo\\src\\main\\java\\model\\instructions.txt";


    public InstructionQueue(List<String> rawInstructions, Bus bus, RegisterFile fpRegisterFile, RegisterFile intRegisterFile, boolean branching) {
        this.branching = branching;
        this.instructions = parseInstructions(rawInstructions);
        this.bus = bus;
        this.fp_registerFile = fpRegisterFile;
        this.int_registerFile = intRegisterFile;
    }

    public boolean isBranching() {
        return branching;
    }

    private static Operation.OperationType determineOperationType(String opCode) {
        switch (opCode) {
            // Floating-point operations (Double precision)
            case "ADD.D":
                return Operation.OperationType.FP_ADD;
            case "SUB.D":
                return Operation.OperationType.FP_SUB;
            case "MUL.D":
                return Operation.OperationType.FP_MULT;
            case "DIV.D":
                return Operation.OperationType.FP_DIV;

            // Floating-point operations (Single precision)
            case "ADD.S":
                return Operation.OperationType.FP_ADD;
            case "SUB.S":
                return Operation.OperationType.FP_SUB;
            case "MUL.S":
                return Operation.OperationType.FP_MULT;
            case "DIV.S":
                return Operation.OperationType.FP_DIV;

            // Integer operations
            case "ADD":
                return Operation.OperationType.ADD;
            case "SUB":
                return Operation.OperationType.SUB;
            case "DADDI":
                return Operation.OperationType.ADD;
            case "DSUBI":
                return Operation.OperationType.SUB;

            // Memory load operations
            case "LW":
                return Operation.OperationType.LOAD;
            case "LD":
                return Operation.OperationType.LOAD;
            case "L.S":
                return Operation.OperationType.LOAD;
            case "L.D":
                return Operation.OperationType.LOAD;

            // Memory store operations
            case "SW":
                return Operation.OperationType.STORE;
            case "SD":
                return Operation.OperationType.STORE;
            case "S.S":
                return Operation.OperationType.STORE;
            case "S.D":
                return Operation.OperationType.STORE;
            case "BNE":
                return Operation.OperationType.BNE;
            case "BEQ":
                return Operation.OperationType.BEQ;

            // Default case (if the opcode doesn't match any known operation)
            default:
                throw new IllegalArgumentException("Unknown operation: " + opCode);
        }
    }


    // Method to load instructions from a file into the queue
    public static List<String> loadRawInstructions(String filePath, int index) {
        List<String> rawInstructions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 0;

            while ((line = reader.readLine()) != null) {
                if (currentLine >= index) {  // Start adding lines from the given index
                    line = line.trim();
                    if (!line.isEmpty()) {
                        rawInstructions.add(line);
                    }
                }
                currentLine++;
            }
        } catch (IOException e) {
            System.err.println("Error loading instructions from file: " + e.getMessage());
        }

        return rawInstructions;
    }


    public Queue<CompiledInstruction> parseInstructions(List<String> rawInstructions) {
        Queue<CompiledInstruction> instructionQueue = new LinkedList<>();

        for (String rawInstruction : rawInstructions) {
            CompiledInstruction instruction = parseInstruction(rawInstruction);
            instructionQueue.add(instruction);
        }

        return instructionQueue;
    }

    // Helper function: Parse a single raw instruction into a CompiledInstruction object
    private CompiledInstruction parseInstruction(String line) {
        String[] parts = line.split(" ");

        Operation operation = new Operation(determineOperationType(parts[0]));
        Tag destination = Tag.parseTag(parts[1]);
        Tag source1 = parts.length > 2 ? Tag.parseTag(parts[2]) : null;
        Tag source2 = parts.length > 3 ? Tag.parseTag(parts[3]) : null;

        return new CompiledInstruction(operation, destination, source1, source2);
    }

    public void append(List<String> rawInstructions){

        for (String rawInstruction : rawInstructions) {
            CompiledInstruction instruction = parseInstruction(rawInstruction);
            this.instructions.add(instruction);
        }


    }

    // Fetch the next instruction
    public CompiledInstruction fetchNextInstruction() {
        return instructions.poll(); // Remove and return the head of the queue
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    // Get the size of the queue
    public int size() {
        return instructions.size();
    }

    public String toString() {
        return instructions.toString();
    }

    public Q transformTagToQ(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }

        return switch (tag.source) {
            case A -> // Waiting for Add
                    new Q(Q.DataType.A, tag.index);
            case M -> // Waiting for Mult
                    new Q(Q.DataType.M, tag.index);
            default -> throw new IllegalArgumentException("Unknown Tag source: " + tag.source);
        };
    }

    public void clear(){
        instructions.clear();
    }

    public void BRANCH(Tag R1, Tag R2, Tag address){
        branching = true;
        this.address =  transformTagToQ(address);
        if (R1.source.equals(FP_REG)) {
            Q registerValue = fp_registerFile.getRegister(R1.index);
            e1 = new Q(registerValue.type, registerValue.value);
        }
        else{
            Q registerValue = int_registerFile.getRegister(R1.index);
            e1 = new Q(registerValue.type, registerValue.value);
        }

        if (R2.source.equals(FP_REG)) {
            Q registerValue = fp_registerFile.getRegister(R2.index);
            e2 = new Q(registerValue.type, registerValue.value);
        }
        else{
            Q registerValue = int_registerFile.getRegister(R2.index);
            e2 = new Q(registerValue.type, registerValue.value);
        }

    }



    public void BEQ(Tag R1, Tag R2, Tag address){
        BRANCH(R1, R2, address);
        expectedBranchResult = true;
    }
    public void BNE(Tag R1, Tag R2, Tag address){
        BRANCH(R1, R2, address);
        expectedBranchResult = false;
    }
    public static boolean QAndTagCompare(Q q, Tag tag) {
        // Check if the types match based on their respective mappings
        boolean typeMatches = false;

        switch (q.type) {
            case A:
                typeMatches = tag.source == A;
                break;
            case M:
                typeMatches = tag.source == M;
                break;
            case Q.DataType.L:
                typeMatches = tag.source == L;
                break;
        }

        // Check if the values match
        boolean valueMatches = q.value == tag.index;

        // Return true if both type and value match
        return typeMatches && valueMatches;
    }


    public List<String> updateOperands(){
        BusData busData = bus.getBusData();

        if (e1.type != Q.DataType.R && QAndTagCompare(e1, busData.tag)){
            e1 = new Q(busData.dataValue.type, busData.dataValue.value);
        }
        if (e2.type != Q.DataType.R && QAndTagCompare(e2, busData.tag)){
            e2 = new Q(busData.dataValue.type, busData.dataValue.value);
        }

        if (e1.type == Q.DataType.R && e2.type == Q.DataType.R){
            boolean result = e1.value == e2.value;
            if (result == expectedBranchResult){
                branching = false;
                return loadRawInstructions(filePath, (int)address.value);
            }
        }
        return new ArrayList<>();
    }

}