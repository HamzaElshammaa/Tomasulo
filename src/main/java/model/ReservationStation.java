package model;

//NEED COMMUNICATION WITH BUS!!!!!!!!!!!

import static model.Operation.OperationType;
import static model.Operation.OperationType.ADD;
import static model.Tag.source.A;
import static model.Tag.source.M;

public class ReservationStation {
    public enum Type{
        ADD,
        MULT,
    }

    //keep track of the cycles
    private final int latency;

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

    private int cycles;

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
    private BusData busData;

    public void BusDataInput(BusData busData){
        this.busData = busData;
    }


    public ReservationStation(Tag name, Type type, int latency) {
        this.tag = name;
        this.type = type;
        this.busy = false;
        this.resultReady = false;
        this.latency = latency;

        clear();
    }

    public boolean isReadyToExecute() {
        return (this.qj.type == Q.DataType.R && this.qk.type == Q.DataType.R);
    }

    void clear(){
        this.operation = null;
        this.vj = -1;
        this.vk = -1;
        this.qj = null;
        this.qk = null;
        this.result = -1;
        this.resultReady = false;
        this.busy = false;

    }

    public void issue(Operation operation){ //given already in constructor
        this.busy = true;
        this.operation = operation;
        this.cycles = latency;
        this.result = -1;
    }



    public void executeCycle(){
        if(!isReadyToExecute() || cycles <= 0){
            return;
        }
        cycles--;
        // if last cycle compute the result
        if(cycles == 0){
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
        if(QAndTagCompare(qj, tag)){
            vj = busData.dataValue.value;
            qj.type = Q.DataType.R;
            qj.value = 0;
        }
        if(QAndTagCompare(qj, tag)){
            vk = busData.dataValue.value;
            qk.type = Q.DataType.R;
            qk.value = 0;
        }
    }

    public void clearCurrentStation(){
        if (busData.tag == this.tag){
            clear();
        }
    }


} 