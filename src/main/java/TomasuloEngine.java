public class TomasuloEngine {
    //memory variables
    int blockSize;
    int cacheSize;
    int missPenalty;

    //latencies
    int additionUnitLatency; //for FP ADD & SUB
    int multiplicationUnitLatency; //for FP MUL & DIV

    //RS sizes
    int additionUnitSize;
    int multiplicationUnitSize;

    //other
    int clockCycle = 0;


}
