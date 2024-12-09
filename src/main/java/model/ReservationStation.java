package model;

public class ReservationStation {
    private String name;
    private boolean busy;
    private String operation;
    private double vj;
    private double vk;
    private String qj;
    private String qk;
    private int address; // For load/store operations
    
    public ReservationStation(String name) {
        this.name = name;
        this.busy = false;
        this.qj = null;
        this.qk = null;
    }
    
    public boolean isReady() {
        return busy && qj == null && qk == null;
    }
    
    // Getters and setters
} 