package model;

// Wrapper class for a mutable int
public class Clock {
    private int cycle;

    public Clock(int initialCycle) {
        this.cycle = initialCycle;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public void increment() {
        this.cycle++;
    }
}

