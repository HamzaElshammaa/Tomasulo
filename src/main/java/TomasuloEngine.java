import model.*;

import java.sql.SQLOutput;
import java.util.List;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

public class TomasuloEngine {
    //memory variables
    static int blockSize = 100;
    static int cacheSize = 100;
    static int missPenalty = 10;
    static int memorySize = 1000;

    //registerFiles
    public static RegisterFile fp_registerFile = new RegisterFile(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0});
    public static RegisterFile int_registerFile = new RegisterFile(new double[] {0, 0, 0, 2, 0, 0, 0});

    //clock
    static Clock clock = new Clock(0);

    //Bus
    public static Bus bus = new Bus();

    //cache
    static Cache cache = new Cache(cacheSize,blockSize,1,missPenalty);

    //DataMemory
    static DataMemory dataMemory = new DataMemory(memorySize,cache);

    public static InstructionQueue instructionQueue;

    //latencies
    static int additionUnitLatency = 4; //for FP ADD & SUB
    static int multiplicationUnitLatency = 6; //for FP MUL & DIV
    static int loadUnitLatency = 1;
    static int storeUnitLatency = 1;

    //RS sizes
    static int additionUnitSize = 3;
    static int multiplicationUnitSize = 2;
    static int loadUnitSize = 3;
    static int storeUnitSize = 3;

    //RS
    static ReservationStationManager additionUnitStations = new ReservationStationManager(ReservationStation.Type.ADD,additionUnitSize, additionUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    static ReservationStationManager multiplicationUnitStations = new ReservationStationManager(ReservationStation.Type.MULT,multiplicationUnitSize, multiplicationUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    static BufferManager loadUnitBuffer = new BufferManager(Buffer.BufferType.LOAD, loadUnitSize, loadUnitLatency, fp_registerFile, int_registerFile, bus, clock, dataMemory);
    static BufferManager storeUnitBuffer = new BufferManager(Buffer.BufferType.STORE, storeUnitSize, storeUnitLatency, fp_registerFile, int_registerFile, bus, clock, dataMemory);

    //other
    static String instructionsFilePath;


    public static void init(){
//        String filePath = "D:\\Uni\\Semester 7\\Microprocessors\\Tomasulo\\src\\main\\java\\model\\instructions.txt";
        String filePath = "C:\\Sem 7\\Microprocessors\\simulationProject\\Tomasulo\\src\\main\\java\\model\\instructions.txt";
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
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_DIV) || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_MULT)){
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
        int i = 0;
        while(i < 21){
            i++;
            
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

            System.out.println("ADD/SUB RS \n" + additionUnitStations);




            clock.increment();
        }
    }



}
