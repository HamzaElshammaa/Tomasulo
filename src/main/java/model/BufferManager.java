package model;

import java.util.ArrayList;
import java.util.List;

import static model.Tag.Source.*;

public class BufferManager {
    public static class IssueData{
        CompiledInstruction instruction;
        int enteredCycle;


        public IssueData(CompiledInstruction instruction, int enteredCycle){
            this.instruction = instruction;
            this.enteredCycle = enteredCycle;
        }
    }

    private final Buffer.BufferType bufferType;
    private final Buffer[] buffers;
    private final List<IssueData> waitingInstructions;
    private final RegisterFile fp_registerFile;
    private final RegisterFile int_registerFile;
    private final Bus bus;
    private final Clock clock;
    private DataMemory dataMemory;

    //constructor
    public BufferManager(Buffer.BufferType type, int numberOfBuffers, int latency, RegisterFile fpRegisterFile, RegisterFile intRegisterFile,Bus bus, Clock clock, DataMemory dataMemory){
        this.bufferType = type;
        this.buffers = new Buffer[numberOfBuffers];
        this.fp_registerFile = fpRegisterFile;
        this.int_registerFile = intRegisterFile;
        this.bus = bus;
        this.clock = clock;
        this.waitingInstructions = new ArrayList<>();
        this.dataMemory = dataMemory;

        //init buffers array
        for(int i = 0; i < numberOfBuffers; i++){
            //generate unqiue tag for each buffer
            Tag.Source src = (bufferType == Buffer.BufferType.LOAD) ? L : S;
            Tag tag = new Tag(src,i);
            //create new buffer and add it to the array
            buffers[i] = new Buffer(tag, type, latency, bus,dataMemory,fpRegisterFile,intRegisterFile);
        }
    }

    //add instruction to waiting list
    public void issueInstruction(CompiledInstruction instruction){
        waitingInstructions.add(new IssueData(instruction,clock.getCycle()));
    }

    //Attempt to issue instruction from waiting list
    public void attemptToIssueInstructions() {
        List<IssueData> issuedInstructions = new ArrayList<>();

        // Iterate over waiting instructions
        for (IssueData instruction : waitingInstructions) {
            Buffer freeBuff = findFreeBuffer();
            if (freeBuff != null) {
                // Issue to free buffer
                freeBuff.issue(instruction.instruction, instruction.enteredCycle);
                // Mark buffer as busy and remove instruction from waiting list
                issuedInstructions.add(instruction);

                // Only for LOAD instructions, we need to update the register file
                if (freeBuff.getType() == Buffer.BufferType.LOAD) {
                    Tag destination = instruction.instruction.destination;
                    Q bufferTag = transformTagToQ(freeBuff.getTag());

                    // Update the appropriate register file based on destination type
                    if (destination.source == Tag.Source.FP_REG) {
                        fp_registerFile.setRegister(destination.index, bufferTag);
                    } else if (destination.source == Tag.Source.REG) {
                        int_registerFile.setRegister(destination.index, bufferTag);
                    }
                }
            }
        }
        waitingInstructions.removeAll(issuedInstructions);
    }

    public Buffer findFreeBuffer(){
        for(Buffer buffer : buffers){
            if(!buffer.isBusy()){
                return buffer;
            }
        }
        return null; // no free stations
    }

    public Q transformTagToQ(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }

        return switch (tag.source) {
            case L -> // Waiting for load
                    new Q(Q.DataType.L, tag.index);
            default -> throw new IllegalArgumentException("Unknown Tag source: " + tag.source);
        };
    }


        public void executeCycle(){
    for(Buffer buffer : buffers){
    buffer.executeCycle();}
    //Attempt to issue instructions
        attemptToIssueInstructions();
    }

    public void clearAllBuffers(){
        for(Buffer buffer : buffers){
            buffer.clear();
        }
        waitingInstructions.clear();
    }

    //get number of waiting instructions
    public int getWaitingInstructionsCount(){return waitingInstructions.size();}

    public void printState(){
        System.out.println("Buffer type: " + bufferType);
        for(int i =0;i<buffers.length;i++){
            Buffer buffer = buffers[i];
            System.out.println("Buffer " + i +"Busy:"+ buffer.isBusy());
        }
        System.out.println("Waiting instructions: " + waitingInstructions.size());
    }

    public void runCycle(){
        attemptToIssueInstructions();
        for(Buffer buffer : buffers){
            buffer.runCycle();
        }
    }



    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Buffer bf : buffers) {
            sb.append(bf.toString());
        }
        return sb.toString();
    }
}
