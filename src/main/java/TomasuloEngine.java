import model.*;

//ANYTHING THAT GOES ON THE BUS NEEDS TO COMMUNICATE WITH THE PERSON DOING THE BUS

public class TomasuloEngine {
    //memory variables
    static int blockSize;
    static int cacheSize;
    static int missPenalty;

    //registerFiles
    public static RegisterFile fp_registerFile = new RegisterFile(new double[] {0.2, 0.3, 1.3, 2.0, 0.0, 0.0, 0.0});
    public static RegisterFile int_registerFile = new RegisterFile(new double[] {0, 0, 0, 2, 0, 0, 0});

    //clock
    static Clock clock = new Clock(0);

    //Bus
    public static Bus bus = new Bus();


    //latencies
    static int additionUnitLatency; //for FP ADD & SUB
    static int multiplicationUnitLatency; //for FP MUL & DIV

    //RS sizes
    static int additionUnitSize;
    static int multiplicationUnitSize;

    //RS
    static ReservationStationManager additionUnitStations = new ReservationStationManager(ReservationStation.Type.ADD,additionUnitSize, additionUnitLatency,fp_registerFile, int_registerFile, bus, clock);
    static ReservationStationManager multiplicationUnitStations = new ReservationStationManager(ReservationStation.Type.MULT,multiplicationUnitSize, multiplicationUnitLatency,fp_registerFile, int_registerFile, bus, clock);


    //other
    static String instructionsFilePath;


    public static void init(){
//        InstructionQueue instructionQueue = new InstructionQueue(instructionsFilePath);


    }

    public static void Main (String[] args) {
        while(true){
            clock.increment();
        }
    }



}
