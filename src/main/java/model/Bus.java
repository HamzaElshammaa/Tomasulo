package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Bus {

    public static class WriteBackData {
        BusData busData;
        int priority;

        public WriteBackData(BusData busData, int priority) {
            this.busData = busData;
            this.priority = priority;
        }
    }



    // List of items waiting to write back
    private final List<WriteBackData> writeBackQueue;

    public void setBusData(BusData busData) {
        this.busData = busData;
    }

    private BusData busData;

    public BusData getBusData() {
        return busData;
    }

    // Constructor
    public Bus() {
        this.busData = new BusData(new Tag(Tag.Source.A, 0), new Q(Q.DataType.R, 0));
        this.writeBackQueue = new ArrayList<>();
    }

    // Add an item to the writeback queue
    public void addToWritebackQueue(BusData busData, int priority) {
        writeBackQueue.add(new WriteBackData(busData, priority));
        // Sort the queue to ensure the lowest priority comes first
        writeBackQueue.sort(Comparator.comparingInt(data -> data.priority));
    }

    // Remove and return the next writeback item
    public void writeBackNext() {
        if (!writeBackQueue.isEmpty()) {
            busData =  writeBackQueue.removeFirst().busData; // Remove and return the first item
        }
//        return null; // Return init form
    }


    // Check if the writeback queue is empty
    public boolean isWritebackQueueEmpty() {
        return writeBackQueue.isEmpty();
    }


    // Get the current size of the writeback queue
    public int getWritebackQueueSize() {
        return writeBackQueue.size();
    }

    // Debugging: Print the contents of the writeback queue
    public void printBusState() {
        System.out.println("Writeback Queue: " + writeBackQueue);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder("Bus State:\n");

        // Format current bus data
        sb.append("Current Bus Data: ")
                .append(formatBusData(busData))
                .append("\n");

        return sb.toString();
    }

    // Helper method to format BusData as "SourceIndex -> TypeValue"
    private String formatBusData(BusData busData) {
        if (busData == null || busData.tag == null || busData.dataValue == null) {
            return "No data";
        }
        String tagRepresentation = busData.tag.source.name() + busData.tag.index;
        String dataRepresentation = busData.dataValue.type.name() + busData.dataValue.value;
        return tagRepresentation + " -> " + dataRepresentation;
    }
}
