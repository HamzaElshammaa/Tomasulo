package model;

//NEED COMMUNICATION WITH BUS!!!!!!!!!!!

public class ReservationStation {
    public enum Type{
        ADD,
        MULT,
        LOAD,
        STORE,
        INT
    }

    private int latency;
    private String name; //Name of station
    private Type type; //Type of station ? ex : mult or add 
    private boolean busy; //if station is in use
    private String operation; //operation to preform
    private double vj; //first operand
    private double vk; //second operand
    //////////////////////////////////////////////////////CHANGED TYPEE///////////////////////////////////////////////////
    private Q qj; //queue first operand
    private Q qk; //queue second operand
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int address; // For load/store operations
    private int cycles; //remaining execution cycles
    private double result; //computed result
    private boolean resultReady; //if execution is done or not
    private BusData busData;

    public void BusDataInput(BusData busData){
        this.busData = busData;
    }


    public ReservationStation(String name, Type type, int latency) {
        this.name = name;
        this.type = type;
        this.busy = false;
        this.resultReady = false;
        this.latency = latency;

        clear();
    }
    
    void clear(){
        this.operation = null;
        this.vj = -1;
        this.vk = -1;
        this.qj = null;
        this.qk = null;
        this.address = -1;
        this.cycles = -1;
        this.result = -1;
        this.resultReady = false;
        this.busy = false;

    }

    public void issue(String operation){ //given already in constructor
        this.busy = true;
        this.operation = operation;
        this.cycles = latency;
        this.resultReady = false;
        this.result = -1;
    }


    public boolean isReadyToExecute() {
        if(!busy || cycles <= 0){
            return false;
        }
        switch (type) {
            case LOAD:
                return address != -1;
            case STORE:
            return address != -1 && vj != -1 && qj == null;
        
            default:
                return vj != -1 && vk!= -1 && qj == null && qk == null;
        }
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
        if(type == Type.LOAD || type == Type.STORE){
            result = vj; // for load / stores value is already in vj
        }else{
            switch (operation) {
                case "ADD.D":
                case "ADDI":
                    result = vj + vk;
                    break;
            case "SUB.D":
            case "SUBI":
                result = vj - vk;
                break;
            case "MUL.D" :
                result = vj * vk;
                break;
            case "DIV.D":
                result= vj/vk;
                break;
            
             default:
                    throw new IllegalStateException("Unkown operation:" + operation);
            }
        }
        resultReady = true;
    }

    public void updateOperand(String rsName, double value){
//        if(rsName.equals(qj)){
//            vj = value;
//            qj = null;
//        }
//        if(rsName.equals(qk)){
//            vk = value;
//            qk = null;
//        }
    }
    // Getters and setters
    public String getName() { return name; }
    public Type getType() { return type; }
    public boolean isBusy() { return busy; }
    public String getOperation() { return operation; }
    public Double getVj() { return vj; }
    public Double getVk() { return vk; }
    public Q getQj() { return qj; }
    public Q getQk() { return qk; }
    public Integer getAddress() { return address; }
    public Integer getCycles() { return cycles; }
    public Double getResult() { return result; }
    public boolean isResultReady() { return resultReady; }

    public void setVj(Double vj) { this.vj = vj; }
    public void setVk(Double vk) { this.vk = vk; }
    public void setQj(Q qj) { this.qj = qj; }
    public void setQk(Q qk) { this.qk = qk; }
    public void setAddress(Integer address) { this.address = address; }

   // State class for GUI display
   public static class ReservationStationState {
    public final String name;
    public final Type type;
    public final boolean busy;
    public final String operation;
    public final Double vj;
    public final Double vk;
    public final String qj;
    public final String qk;
    public final Integer address;
    public final Integer cycles;
    public final boolean resultReady;

    public ReservationStationState(String name, Type type, boolean busy, 
                                 String operation, Double vj, Double vk, 
                                 String qj, String qk, Integer address, 
                                 Integer cycles, boolean resultReady) {
        this.name = name;
        this.type = type;
        this.busy = busy;
        this.operation = operation;
        this.vj = vj;
        this.vk = vk;
        this.qj = qj;
        this.qk = qk;
        this.address = address;
        this.cycles = cycles;
        this.resultReady = resultReady;
    }
}
} 