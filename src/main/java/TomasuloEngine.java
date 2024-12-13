import model.*;

import java.sql.SQLOutput;
import java.util.List;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

public class TomasuloEngine {
    //memory variables
    static int blockSize;
    static int cacheSize;
    static int missPenalty;

    //registerFiles
    public static RegisterFile fp_registerFile = new RegisterFile(new double[] {0.2, 0.3, 1.3, 2.0, 0.0, 0.0, 0.0, 0.2, 0.3, 1.3, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
    public static RegisterFile int_registerFile = new RegisterFile(new double[] {0, 0, 0, 2, 0, 0, 0});

    //clock
    static Clock clock = new Clock(0);

    //Bus
    public static Bus bus = new Bus();

    public static InstructionQueue instructionQueue;

    //latencies
    static int additionUnitLatency = 1; //for FP ADD & SUB
    static int multiplicationUnitLatency = 1; //for FP MUL & DIV
    static int loadUnitLatency = 1;
    static int storeUnitLatency = 1;

    //RS sizes
    static int additionUnitSize = 3;
    static int multiplicationUnitSize = 3;
    static int loadUnitSize = 3;
    static int storeUnitSize = 3;

    //RS
    static ReservationStationManager additionUnitStations = new ReservationStationManager(ReservationStation.Type.ADD,additionUnitSize, additionUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    static ReservationStationManager multiplicationUnitStations = new ReservationStationManager(ReservationStation.Type.MULT,multiplicationUnitSize, multiplicationUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    static BufferManager loadUnitBuffer = new BufferManager(Buffer.BufferType.LOAD, loadUnitSize, loadUnitLatency, fp_registerFile, int_registerFile, bus, clock);
    static BufferManager storeUnitBuffer = new BufferManager(Buffer.BufferType.STORE, storeUnitSize, storeUnitLatency, fp_registerFile, int_registerFile, bus, clock);

    //other
    static String instructionsFilePath;


    public static void init(){
        String filePath = "D:\\Uni\\Semester 7\\Microprocessors\\Tomasulo\\src\\main\\java\\model\\instructions.txt";

        // Load raw instructions from the file
        List<String> rawInstructions = InstructionQueue.loadRawInstructions(filePath);

        // loading instructions into queue
        instructionQueue = new InstructionQueue(rawInstructions);

    }

    public static void fetchInstruction(){
        CompiledInstruction issuedInstruction = instructionQueue.fetchNextInstruction();
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_ADD) || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_SUB)){
            additionUnitStations.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.DIV) || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.MULT)){
            multiplicationUnitStations.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.LOAD)){
            loadUnitBuffer.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.STORE)){
            storeUnitBuffer.issueInstruction(issuedInstruction);
        }
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
                break;
            }
            additionUnitStations.runCycle();

            System.out.println("ADD/SUB RS \n" + additionUnitStations);







            clock.increment();
        }
    }



}
