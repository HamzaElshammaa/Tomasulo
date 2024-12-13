package model;

import java.util.ArrayList;
import java.util.List;

import static model.Tag.Source.M;
import static model.Tag.Source.A;


public class ReservationStationManager {
    public static class IssueData {
        CompiledInstruction instruction;
        int enteredCycle;

        public IssueData(CompiledInstruction instruction, int enteredCycle) {
            this.instruction = instruction;
            this.enteredCycle = enteredCycle; //should be equal the cycle it emtered in
        }
    }

    private final ReservationStation.Type type; // Type of the reservation stations (ADD or MULT)
    private final ReservationStation[] reservationStations; // Array of reservation stations
    private final List<IssueData> waitingInstructions; // List of instructions waiting for RS
    private final RegisterFile fp_registerFile;
    private final RegisterFile int_registerFile;
    private final Bus bus;
    private final Clock clock;

    // Constructor
    public ReservationStationManager(ReservationStation.Type type, int numberOfStations, int latency, RegisterFile fpRegisterFile, RegisterFile intRegisterFile, Bus bus, Clock clock) {
        this.type = type;
        this.reservationStations = new ReservationStation[numberOfStations];
        fp_registerFile = fpRegisterFile;
        int_registerFile = intRegisterFile;
        this.bus = bus;
        this.clock = clock;
        this.waitingInstructions = new ArrayList<>();

        // Initialize the reservation stations array
        for (int i = 0; i < numberOfStations; i++) {
            // Generate a unique tag for each station (e.g., ADD_0, MULT_1, etc.)
            Tag.Source src = (type == ReservationStation.Type.ADD) ? A : M;
            Tag tag = new Tag(src, i);
            // Create a new reservation station and add it to the array
            reservationStations[i] = new ReservationStation(tag, type, latency,fpRegisterFile, intRegisterFile, bus);
        }
    }

    // Add an instruction to the waiting list
    public void issueInstruction(CompiledInstruction instruction) {
        waitingInstructions.add(new IssueData(instruction, clock.getCycle()));
    }

    // Attempt to issue instructions from the waiting list
    public void attemptToIssueInstructions() {
        List<IssueData> issuedInstructions = new ArrayList<>();

        // Iterate over the waiting instructions
        for (IssueData instruction : waitingInstructions) {
            ReservationStation freeRS = findFreeStation();

            if (freeRS != null) {
                // Issue the instruction to the free RS
                freeRS.issue(instruction.instruction, instruction.enteredCycle);
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

    public void runCycle() {
        for (ReservationStation rs : reservationStations) {
            rs.runCycle();
        }
    }
}
