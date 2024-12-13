package model;

import java.util.ArrayList;
import java.util.List;

public class Bus {

    // List of items waiting to be issued
    private List<String> issueQueue;

    // List of items waiting to write back
    private List<String> writebackQueue;

    private BusData busDataValue;

    public BusData getBusDataValue() {
        return busDataValue;
    }

    // Constructor
    public Bus() {
        this.issueQueue = new ArrayList<>();
        this.writebackQueue = new ArrayList<>();
    }

    // Add an item to the issue queue
    public void addToIssueQueue(String item) {
        issueQueue.add(item);
    }

    // Remove and return the next item to be issued
    public String issueNext() {
        if (!issueQueue.isEmpty()) {
            return issueQueue.remove(0); // Remove and return the first item
        }
        return null; // Return null if the queue is empty
    }

    // Add an item to the writeback queue
    public void addToWritebackQueue(String item) {
        writebackQueue.add(item);
    }

    // Remove and return the next writeback item
    public String writeBackNext() {
        if (!writebackQueue.isEmpty()) {
            return writebackQueue.remove(0); // Remove and return the first item
        }
        return null; // Return null if the queue is empty
    }

    // Check if the issue queue is empty
    public boolean isIssueQueueEmpty() {
        return issueQueue.isEmpty();
    }

    // Check if the writeback queue is empty
    public boolean isWritebackQueueEmpty() {
        return writebackQueue.isEmpty();
    }

    // Get the current size of the issue queue
    public int getIssueQueueSize() {
        return issueQueue.size();
    }

    // Get the current size of the writeback queue
    public int getWritebackQueueSize() {
        return writebackQueue.size();
    }

    // Debugging: Print the contents of both queues
    public void printBusState() {
        System.out.println("Issue Queue: " + issueQueue);
        System.out.println("Writeback Queue: " + writebackQueue);
    }
}
