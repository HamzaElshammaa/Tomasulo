import model.InstructionQueue;
import model.RegisterFile;
import model.ReservationStation;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

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

    //RS
    static ReservationStation []  additionUnitStations;
    static ReservationStation []  multiplicationUnitStations;

    public static void init(){
        InstructionQueue instructionQueue = new InstructionQueue(instructionsFilePath);
        //call reg file
        additionUnitStations = new ReservationStation[additionUnitSize];
        multiplicationUnitStations = new ReservationStation[multiplicationUnitSize];

    }




    public static void Main (String[] args) {

    }



}
