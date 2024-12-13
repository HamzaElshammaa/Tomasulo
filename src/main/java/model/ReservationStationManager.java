package model;

import java.util.ArrayList;
import java.util.List;

import static model.Tag.source.A;
import static model.Tag.source.M;

public class ReservationStationManager {
    private final ReservationStation.Type type; // Type of the reservation stations (ADD or MULT)
    private final ReservationStation[] reservationStations; // Array of reservation stations
    private final List<CompiledInstruction> waitingInstructions; // List of instructions waiting for RS

    // Constructor
    public ReservationStationManager(ReservationStation.Type type, int numberOfStations, int latency) {
        this.type = type;
        this.reservationStations = new ReservationStation[numberOfStations];
        this.waitingInstructions = new ArrayList<>();

        // Initialize the reservation stations array
        for (int i = 0; i < numberOfStations; i++) {
            // Generate a unique tag for each station (e.g., ADD_0, MULT_1, etc.)
            Tag tag = new Tag();
            tag.source = (type == ReservationStation.Type.ADD) ? A : M;
            tag.index = i;

            // Create a new reservation station and add it to the array
            reservationStations[i] = new ReservationStation(tag, type, latency);
        }
    }

    // Add an instruction to the waiting list
    public void addToWaitingList(CompiledInstruction instruction) {
        waitingInstructions.add(instruction);
    }

    // Attempt to issue instructions from the waiting list
    public void attemptToIssueInstructions() {
        List<CompiledInstruction> issuedInstructions = new ArrayList<>();

        // Iterate over the waiting instructions
        for (CompiledInstruction instruction : waitingInstructions) {
            ReservationStation freeRS = findFreeStation();

            if (freeRS != null) {
                // Issue the instruction to the free RS
                freeRS.issue(instruction.operation);

                freeRS.setBusy(true);
                //do register file to cont.....
                // Assign operands to the RS
//                freeRS.vj = instruction.source1 != null ? instruction.source1.index : 0; // Example logic
//                freeRS.vk = instruction.source2 != null ? instruction.source2.index : 0; // Example logic

                // Mark the RS as busy and remove the instruction from the waiting list
                issuedInstructions.add(instruction);
            }
        }

        // Remove all issued instructions from the waiting list
        waitingInstructions.removeAll(issuedInstructions);
    }

    // Find a free reservation station
    public ReservationStation findFreeStation() {
        for (ReservationStation rs : reservationStations) {
            if (!rs.isBusy()) {
                return rs;
            }
        }
        return null; // No free station available
    }

    // Execute one cycle for all reservation stations
    public void executeCycle() {
        for (ReservationStation rs : reservationStations) {
            rs.executeCycle();
        }

        // Attempt to issue waiting instructions after executing a cycle
        attemptToIssueInstructions();
    }

    // Clear all reservation stations
    public void clearAllStations() {
        for (ReservationStation rs : reservationStations) {
            rs.clear();
        }
        waitingInstructions.clear();
    }

    // Get the number of waiting instructions
    public int getWaitingCount() {
        return waitingInstructions.size();
    }

    // Print the state of all reservation stations and waiting instructions (for debugging)
    public void printState() {
        System.out.println("Reservation Stations of type: " + type);
        for (int i = 0; i < reservationStations.length; i++) {
            ReservationStation rs = reservationStations[i];
            System.out.println("Station " + i + " - Busy: " + rs.isBusy() + ", Result Ready: ");
        }
        System.out.println("Waiting Instructions: " + waitingInstructions.size());
    }
}
