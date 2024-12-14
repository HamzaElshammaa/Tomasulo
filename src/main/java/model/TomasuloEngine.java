package model;

import java.util.ArrayList;
import java.util.List;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

public class TomasuloEngine {
    //memory variables
    public static int blockSize =10;
    public static int cacheSize =10;
    static int missPenalty=1;
    static int memorySize =32;


    //clock
    static Clock clock = new Clock(0);

    //Bus
    public static Bus bus = new Bus();

    //registerFiles
    public static RegisterFile fp_registerFile = new RegisterFile(new double[] {0.2, 0.3, 1.3, 2.0, 0.0, 0.0, 0.0, 0.2, 0.3, 1.3, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},bus);
    public static RegisterFile int_registerFile = new RegisterFile(new double[] {0, 0, 0, 2, 0, 0, 0},bus);

    //cache
    static Cache cache = new Cache(cacheSize,blockSize,1,missPenalty);

    //DataMemory
    static DataMemory dataMemory = new DataMemory(memorySize,cache);

    public static InstructionQueue instructionQueue;

    //latencies
    public static int additionUnitLatency = 1; //for FP ADD & SUB
    public static int multiplicationUnitLatency = 1; //for FP MUL & DIV
    public static int loadUnitLatency = 1;
    public static int storeUnitLatency = 1;

    //RS sizes
    public static int additionUnitSize = 3;
    public static int multiplicationUnitSize = 3;
    public static int loadUnitSize = 3;
    public static int storeUnitSize = 3;

    //RS
    public static ReservationStationManager additionUnitStations = new ReservationStationManager(ReservationStation.Type.ADD,additionUnitSize, additionUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    public static ReservationStationManager multiplicationUnitStations = new ReservationStationManager(ReservationStation.Type.MULT,multiplicationUnitSize, multiplicationUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    public static BufferManager loadUnitBuffer = new BufferManager(Buffer.BufferType.LOAD, loadUnitSize, loadUnitLatency, fp_registerFile, int_registerFile, bus, clock, dataMemory);
    public static BufferManager storeUnitBuffer = new BufferManager(Buffer.BufferType.STORE, storeUnitSize, storeUnitLatency, fp_registerFile, int_registerFile, bus, clock, dataMemory);

    //other
    static String instructionsFilePath;


    public static void init(){
        String filePath = "C:\\Users\\mozam\\OneDrive\\Uni\\Semester 7\\CSEN702 Microprocessors\\Micro Project 2\\Tomasulo\\src\\main\\java\\model\\instructions.txt";

        // Load raw instructions from the file
        List<String> rawInstructions = InstructionQueue.loadRawInstructions(filePath);

        // loading instructions into queue
        instructionQueue = new InstructionQueue(rawInstructions);

    }

    public static void fetchInstruction() {
        // Fetch the next instruction from the queue
        CompiledInstruction issuedInstruction = instructionQueue.fetchNextInstruction();

        // Handle FP_ADD and FP_SUB
        if (issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_ADD) ||
                issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_SUB)) {
            additionUnitStations.issueInstruction(issuedInstruction);
        }

        // Handle MULT and DIV (Floating point)
        if (issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_MULT) ||
                issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_DIV)) {
            multiplicationUnitStations.issueInstruction(issuedInstruction);
        }

        // Handle integer addition and subtraction (e.g., ADDI, SUBI)
        if (issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.ADD) ||
                issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.SUB)) {
            additionUnitStations.issueInstruction(issuedInstruction);
        }

        // Handle load instructions (e.g., LW, LD, L.S, L.D)
        if (issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.LOAD)) {
            loadUnitBuffer.issueInstruction(issuedInstruction);
        }

        // Handle store instructions (e.g., STORE, S.S, S.D)
        if (issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.STORE)) {
            storeUnitBuffer.issueInstruction(issuedInstruction);
        }
    }

    public static List<String[]> getInstructionQueueData() {
        List<String[]> data = new ArrayList<>();
        int instructionNumber = 1; // To number the instructions

        for (CompiledInstruction instruction : instructionQueue.getInstructions()) {
            data.add(new String[]{
                    String.valueOf(instructionNumber++),   // Instruction Number
                    instruction.toString()                // Instruction String
            });
        }

        return data;
    }

    public List<CompiledInstruction> getInstructions() {
        return new ArrayList<>(this.getInstructions()); // Return a copy of the instructions
    }





    public static void main (String[] args) {
        init();
        while(true){


            System.out.println("\n" + "Clock Cycle: " + clock.getCycle());

            if(clock.getCycle() == 0){
                clock.increment();
                continue;
            }
            try{
                fetchInstruction();

            }catch(Exception e){
                System.out.println("No more instructions to be fetched");

            }
            additionUnitStations.runCycle();
            multiplicationUnitStations.runCycle();
            loadUnitBuffer.runCycle();
            storeUnitBuffer.runCycle();


            System.out.println("ADD/SUB RS \n" + additionUnitStations);
            System.out.println("MUL/DIV RS:\n" + multiplicationUnitStations);
            System.out.println("LOAD Buffer:\n" + loadUnitBuffer);
            System.out.println("STORE Buffer:\n" + storeUnitBuffer);


            if (instructionQueue.isEmpty() && additionUnitStations.allEmpty() && multiplicationUnitStations.allEmpty() && loadUnitBuffer.isEmpty()  && storeUnitBuffer.isEmpty() ) {
                System.out.println("Simulation completed.");
                break;
            }


            clock.increment();
        }
    }



}