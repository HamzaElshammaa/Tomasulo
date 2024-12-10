import model.InstructionQueue;
import model.RegisterFile;

public class TomasuloEngine {
    //memory variables
    static int blockSize;
    static int cacheSize;
    static int missPenalty;

    //latencies
    static int additionUnitLatency; //for FP ADD & SUB
    static int multiplicationUnitLatency; //for FP MUL & DIV

    //RS sizes
    static int additionUnitSize;
    static int multiplicationUnitSize;

    //other
    static int clockCycle = 0;
    static String instructionsFilePath;
    static String registerContentsFilePath;

    public static void init(){
        InstructionQueue instructionQueue = new InstructionQueue(instructionsFilePath);
    }




    public static void Main (String[] args) {

    }



}
