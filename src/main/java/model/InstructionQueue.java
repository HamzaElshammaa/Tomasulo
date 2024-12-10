package model;

import java.util.LinkedList;
import java.util.Queue;

public class InstructionQueue {

    private Queue<String> instructions; // Generic type to store instructions as Strings

    // Constructor: Loads instructions from a file and initializes the queue
    public InstructionQueue(String filePath) {
        this.instructions = loadInstructions(filePath);
    }

    // Method to load instructions from a file into the queue
    private Queue<String> loadInstructions(String filePath) {
        Queue<String> instructionQueue = new LinkedList<>();

        // Simulate file loading (replace with actual file reading logic)
        try {
            // Example: Load instructions from the file (pseudo-code)
            /*
             * BufferedReader reader = new BufferedReader(new FileReader(filePath));
             * String line;
             * while ((line = reader.readLine()) != null) {
             *     instructionQueue.add(line.trim()); // Add each instruction to the queue
             * }
             * reader.close();
             */
            // Simulated instructions for testing purposes
            instructionQueue.add("L.D F2, 100");
            instructionQueue.add("ADD.D F4, F2, F6");
            instructionQueue.add("MUL.D F6, F4, F2");
        } catch (Exception e) {
            System.err.println("Error loading instructions from file: " + e.getMessage());
        }

        return instructionQueue;
    }

    // Method to fetch the next instruction from the queue
    public String fetchNextInstruction() {
        // Remove and return the next instruction in the queue
        if (!instructions.isEmpty()) {
            return instructions.poll(); // poll() removes and returns the head of the queue
        } else {
            return null; // Return null if the queue is empty
        }
    }

    // Method to check if the queue is empty
    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    // Method to get the current size of the queue
    public int size() {
        return instructions.size();
    }
}
