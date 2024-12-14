package model;

//NEED COMMUNICATION WITH BUS!!!!!!!!!!!

import static model.Operation.OperationType;
import static model.Operation.OperationType.ADD;
import static model.Tag.Source.*;

public class ReservationStation {
    public enum Type{
        ADD,
        MULT,
    }

    //keep track of the cycles
    private final int latency;
    private int cycles;

    //enter time to set priority
    private int enterTime = -1;
    boolean addedToWriteBackQueue = false;

    //bus
    private final Bus bus;

    //register files
    private final RegisterFile fp_registerFile;
    private final RegisterFile int_registerFile;

    public void setVj(double vj) {
        this.vj = vj;
    }

    public void setVk(double vk) {
        this.vk = vk;
    }

    public void setQj(Q qj) {
        this.qj = qj;
    }

    public void setQk(Q qk) {
        this.qk = qk;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }


    public Tag getTag() {
        return tag;
    }

    //tag name to compare with the bus input tag to erase and to determine the output busData tag
    private final Tag tag; //Name of station
    private final Type type; //Type of station ? ex : mult or add

    public boolean isBusy() {
        return busy;
    }

    //the data variables
    private boolean busy; //if station is in use
    private Operation operation; //operation to preform
    private double vj; //first operand
    private double vk; //second operand

    public double getVj() {
        return vj;
    }

    public double getVk() {
        return vk;
    }

    public Q getQj() {
        return qj;
    }

    public Q getQk() {
        return qk;
    }

    private Q qj; //queue first operand
    private Q qk; //queue second operand

    //other
    private double result; //computed result
    private boolean resultReady; //if execution is done or not


    public ReservationStation(Tag name, Type type, int latency, RegisterFile fpRegisterFile, RegisterFile intRegisterFile, Bus bus) {
        this.tag = name;
        this.type = type;
        this.bus = bus;
        fp_registerFile = fpRegisterFile;
        int_registerFile = intRegisterFile;
        this.busy = false;
        this.resultReady = false;
        this.latency = latency;
        this.qj = new Q(Q.DataType.R, 0);
        this.qk = new Q(Q.DataType.R, 0);

        clear();
    }

    public boolean isReadyToExecute() {
        return (this.qj.type == Q.DataType.R && this.qk.type == Q.DataType.R);
    }

    void clear(){
        this.operation = null;
//        this.vj = -1;
//        this.vk = -1;
//        this.qj = null;
//        this.qk = null;
//        this.result = -1;
        this.resultReady = false;
        this.busy = false;
        this.addedToWriteBackQueue = false;
    }

    public void issue(CompiledInstruction instruction, int enterTime){ //given already in constructor
        this.busy = true;
        this.operation = instruction.operation;
        this.cycles = latency;
        this.enterTime = enterTime;
        Q operand1;
        Q operand2;
        if (instruction.source1.source == FP_REG){
            Q current = fp_registerFile.getRegister(instruction.source1.index);
            operand1 = new Q(current.type, current.value);
        }
        else if (instruction.source1.source == IMM){
            operand1 = new Q(Q.DataType.R, instruction.source1.index);
        }
        else {
            Q current = int_registerFile.getRegister(instruction.source1.index);
            operand1 = new Q(current.type, current.value);
        }

        if (instruction.source2.source == FP_REG){
            Q current = fp_registerFile.getRegister(instruction.source2.index);
            operand2 = new Q(current.type, current.value);
        }
        else if (instruction.source2.source == IMM){
            operand2 = new Q(Q.DataType.R, instruction.source2.index);
        }
        else {
            Q current = int_registerFile.getRegister(instruction.source2.index);
            operand2 = new Q(current.type, current.value);
        }

        if (operand1.type == Q.DataType.R){
            this.vj = operand1.value;
        }
        else {
            this.qj = operand1;
        }

        if (operand2.type == Q.DataType.R){
            this.vk = operand2.value;
        }
        else {
            this.qk = operand2;
        }

        this.result = -1;
    }



    public void executeCycle(){
        if(!isReadyToExecute() || cycles <= -1){
            return;
        }
        cycles--;
        // if last cycle compute the result
        if(cycles == -1){
            computeResult();
        }
    }

    private void computeResult(){

        switch (operation.operationType) {
            case ADD:
            case FP_ADD:
                result = vj + vk;
                break;
            case SUB:
            case FP_SUB:
                result = vj - vk;
                break;
            case MULT:
            case FP_MULT:
                result = vj * vk;
                break;
            case DIV:
            case FP_DIV:
                result = vj / vk;
                break;
            default:
                throw new IllegalStateException("Unkown operation:" + operation);
        }

        resultReady = true;
        bus.addToWritebackQueue(new BusData(this.tag, new Q(Q.DataType.R, result)), enterTime);
        addedToWriteBackQueue = true;
    }

    public static boolean QAndTagCompare(Q q, Tag tag) {
        // Check if the types match based on their respective mappings
        boolean typeMatches = false;

        switch (q.type) {
            case A:
                typeMatches = tag.source == A;
                break;
            case M:
                typeMatches = tag.source == M;
                break;
        }

        // Check if the values match
        boolean valueMatches = q.value == tag.index;

        // Return true if both type and value match
        return typeMatches && valueMatches;
    }

    public void updateOperands(){
        BusData busData = bus.getBusData();
        if(QAndTagCompare(qj, busData.tag)){
            vj = busData.dataValue.value;
            qj = new Q(Q.DataType.R, 0);
        }
        if(QAndTagCompare(qk, busData.tag)){
            vk = busData.dataValue.value;
            qk = new Q(Q.DataType.R, 0);
        }
    }

    public void clearCurrentStation(){
        if (bus.getBusData().tag == this.tag){
            clear();
        }
    }

    public void runCycle(){
        if (resultReady && !addedToWriteBackQueue){
//            bus.addToWritebackQueue(new BusData(this.tag, new Q(Q.DataType.R, result)), enterTime);
//            addedToWriteBackQueue = true;
        }
        else{
            if (busy){
                updateOperands();
                executeCycle();
            }
        }
        clearCurrentStation();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nReservation Station: ").append(tag).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("Busy: ").append(busy).append("\n");
        sb.append("Operation: ").append(operation != null ? operation.operationType : "None").append("\n");
        sb.append("Vj: ").append(qj != null && qj.type != Q.DataType.R ? "Waiting" : vj).append("\n");
        sb.append("Vk: ").append(qk != null && qk.type != Q.DataType.R ? "Waiting" : vk).append("\n");
        sb.append("Qj: ").append(qj != null ? qj : "None").append("\n");
        sb.append("Qk: ").append(qk != null ? qk : "None").append("\n");
        sb.append("Result: ").append(resultReady ? result : "Not Ready").append("\n");
        sb.append("Cycles Remaining: ").append(cycles > 0 ? cycles : "Execution Complete").append("\n");
        sb.append("Result Ready: ").append(resultReady).append("\n");
        sb.append("Added to WriteBack Queue: ").append(addedToWriteBackQueue).append("\n");
        sb.append("Enter Time: ").append(enterTime).append("\n");
        return sb.toString();
    }





} 