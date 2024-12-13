package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import model.*;

public class InstructionQueue {

    private Queue<CompiledInstruction> instructions; // Queue of Instruction objects

    public InstructionQueue(List<String> rawInstructions) {
        this.instructions = parseInstructions(rawInstructions);
    }

    private static Operation.OperationType determineOperationType(String opCode) {
        switch (opCode) {
            // Floating-point operations
            case "ADD.D":
                return Operation.OperationType.FP_ADD;
            case "SUB.D":
                return Operation.OperationType.FP_SUB;
            case "MUL.D":
                return Operation.OperationType.FP_MULT;
            case "DIV.D":
                return Operation.OperationType.FP_DIV;
            // Integer operations
            case "ADD":
                return Operation.OperationType.ADD;
            case "SUB":
                return Operation.OperationType.SUB;
            case "MULT":
                return Operation.OperationType.MULT;
            case "DIV":
                return Operation.OperationType.DIV;
            // Memory operations
            case "L.D":
                return Operation.OperationType.LOAD;
            case "S.D":
                return Operation.OperationType.STORE;

            default:
                throw new IllegalArgumentException("Unknown operation: " + opCode);
        }
    }


    // Method to load instructions from a file into the queue
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

    private Queue<CompiledInstruction> parseInstructions(List<String> rawInstructions) {
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

    public static void main(String[] args) {
        // Path to the instruction file
        String filePath = "D:\\Uni\\Semester 7\\Microprocessors\\Tomasulo\\src\\main\\java\\model\\instructions.txt";

        // Load raw instructions from the file
        List<String> rawInstructions = InstructionQueue.loadRawInstructions(filePath);

        // Create an InstructionQueue with the loaded raw instructions
        InstructionQueue instructionQueue = new InstructionQueue(rawInstructions);

        // Print all instructions in the queue
        System.out.println("Loaded Instructions:");

        System.out.println(instructionQueue);
    }
}