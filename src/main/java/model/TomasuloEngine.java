package model;
//import model.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

public class TomasuloEngine {
    //memory variables
    public static int blockSize ;
    public static int cacheSize ;
    public static int missPenalty ;
    public static int memorySize ;

    //Bus
    public static Bus bus;


    //registerFiles
    public static RegisterFile fp_registerFile;
    public static RegisterFile int_registerFile;


    public static InstructionQueue instructionQueue;

    //latencies
    public static int additionUnitLatency ; //for FP ADD & SUB
    public static int multiplicationUnitLatency ; //for FP MUL & DIV
    public  static int loadUnitLatency ;
    public  static int storeUnitLatency ;

    //RS sizes
    public  static int additionUnitSize;
    public  static int multiplicationUnitSize;
    public  static int loadUnitSize;
    public  static int storeUnitSize;


    //other
    static String instructionsFilePath;


    public static void init(){
        String filePath = "C:\\Users\\mozam\\OneDrive\\Uni\\Semester 7\\CSEN702 Microprocessors\\Micro Project 2\\Tomasulo\\src\\main\\java\\model\\instructions.txt";
        //String filePath = "C:\\Sem 7\\Microprocessors\\simulationProject\\Tomasulo\\src\\main\\java\\model\\instructions.txt";
        // Load raw instructions from the file
        List<String> rawInstructions = InstructionQueue.loadRawInstructions(filePath);

        // loading instructions into queue
        instructionQueue = new InstructionQueue(rawInstructions, bus, fp_registerFile, int_registerFile);

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


    public static void fetchInstruction(){
        if (instructionQueue.isBranching()){
            instructionQueue.updateOperands();
            return;
        }

        CompiledInstruction issuedInstruction = instructionQueue.fetchNextInstruction();
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_ADD)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_SUB)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.ADD)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.SUB)
        ){
            additionUnitStations.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_DIV)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.FP_MULT)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.MULT)
                || issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.DIV)
        ){
            multiplicationUnitStations.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.LOAD)){
            loadUnitBuffer.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.STORE)){
            storeUnitBuffer.issueInstruction(issuedInstruction);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.BNE)){
            //BNE
            instructionQueue.BNE(issuedInstruction.getDestination(), issuedInstruction.getSource1(), issuedInstruction.getSource2().index);
        }
        if(issuedInstruction.getOperation().isOperationEqual(Operation.OperationType.BEQ)){
            //BEQ
            instructionQueue.BEQ(issuedInstruction.getDestination(), issuedInstruction.getSource1(), issuedInstruction.getSource2().index);
        }
    }

    public static void runCycle(){
        System.out.println("------------------------------------------------------------------------------------------- \n");
        System.out.println("\n" + "Clock Cycle: " + clock.getCycle());

        if(clock.getCycle() == 0){
            clock.increment();
            return;
        }
        try{
            fetchInstruction();

        }catch(Exception e){
            System.out.println("No more instructions to be fetched");
        }

        additionUnitStations.runCycle();
        multiplicationUnitStations.runCycle();
        bus.writeBackNext();

        System.out.println("ADD RS \n" + additionUnitStations);
        System.out.println("////// \n");
        System.out.println("MULT RS \n" + multiplicationUnitStations);

        System.out.println("bus current: " + bus);

       if(fp_registerFile!=null){ fp_registerFile.updateRegisterFile();}
       if(int_registerFile!=null) {int_registerFile.updateRegisterFile();}

        System.out.println(fp_registerFile);

        System.out.println("------------------------------------------------------------------------------------------- \n");
        clock.increment();
    }

    public static boolean isDone(){
        return (instructionQueue.isEmpty() &&
                additionUnitStations.allEmpty() &&
                multiplicationUnitStations.allEmpty());
    }

    public static void main (String[] args) {
        init();
        while(!isDone()){
            runCycle();

        }
    }



}