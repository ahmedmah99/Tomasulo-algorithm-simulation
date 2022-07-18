import java.util.Arrays;
import java.util.Hashtable;

public class Instruction {

    Hashtable<String,String[]> station;

    Simulation simulator;
    int instructionNumber = 1;

    String log = "";

    Hashtable<String,Integer> instructionsCycles = new Hashtable<String, Integer>();

    public Instruction(Simulation simulator){

        station = new Hashtable<String, String[]>();
        this.simulator = simulator;

        instructionsCycles.put("ADD",2);
        instructionsCycles.put("SUB",2);
        instructionsCycles.put("MUL",10);
        instructionsCycles.put("DIV",40);
        instructionsCycles.put("LW",2);
        instructionsCycles.put("SW",2);

    }

    public void putInstruction(String instruction, String wbReg ,String reg1, String reg2, String Issue, String ExStart,String ExEnd, String write){

        String[] instructions = {instruction,wbReg,reg1,reg2,Issue,ExStart,ExEnd,write};

        String key = instruction+ instructionNumber;

        station.put(key,instructions);

        instructionNumber += 1;

    }


    public void setExStart(String instruction, String execute){
        station.get(instruction)[5] = execute;
    }

    public String getExStart(String instruction){
        return station.get(instruction)[5];
    }

    public String getExEnd(String instruction){
        return station.get(instruction)[6];
    }

    public void setExEnd(String instruction, String execute){
        station.get(instruction)[6] = execute;
    }

    public void setWrite(String instruction, String wb){
        station.get(instruction)[7] = wb;
    }

    public String getWrite(String instruction){
        return station.get(instruction)[7];
    }

    public void setIssue(String instruction, String issue){
        station.get(instruction)[4] = issue;
    }


    public String getIssue(String instruction){
        return this.station.get(instruction)[4];
    }


    public String getInstruction(String instruction){
        return station.get(instruction)[0];
    }

    public String getWbRegister(String instruction){
        return station.get(instruction)[1];
    }


    public String getRegister1(String instruction){
        return station.get(instruction)[2];
    }

    public String getRegister2(String instruction){
        return station.get(instruction)[3];
    }

    public String print() {
        return log;
    }

    public void emptyLogs() {
        log = "";
    }

    public String showComponent() {
        String component = "";
        for (int i= 0; i < simulator.instructionName.length; i++){
            String secInstruction[] = station.get(simulator.instructionName[i] + (i+1));
            component += Arrays.toString(secInstruction) + "\n";
        }
        return component;
    }

    /**
     * if the instruction is not issued it will be issued in the reservation station if there is a place
     * and change the registerFile accordingly
     * the instruction will end execution if it finished its cycles
     */

    public void start()
    {
            String[] instructions = simulator.instructionName;


            for(int i = 0; i < instructions.length && i < this.simulator.cycle; i++)
            {
                String fetch = instructions[i] + (i+1);
                String name = instructions[i];


                if(getIssue(fetch).equals("")){
                    if(name.equals("ADD") || name.equals("SUB"))
                        this.handleAddIssue(fetch, getRegister1(fetch), getRegister2(fetch), name);
                    else if(name.equals("MUL") || name.equals("DIV"))
                        this.handleMulIssue(fetch,getRegister1(fetch),getRegister2(fetch), name);
                    else
                        this.handleLoadIssue(fetch,getRegister1(fetch),getRegister2(fetch), name);
                }

            }

    }


    public void handleAddIssue(String instruction, String reg1, String reg2, String instructionName){


        String reserved = simulator.reservationStation.reserve(instruction,reg1,reg2);
        if(!reserved.equals("F")){
            simulator.instruction.setIssue(instruction,String.valueOf(simulator.cycle));
            String registerName = simulator.instruction.getWbRegister(instruction);

            simulator.registerFile.setRegister(registerName,reserved);

            // add to log
            log += "The instruction " + instructionName + " is Issued at cycle " + simulator.cycle +
                    " and added to reservation station " + reserved + " \n" +
                    "The Register " +registerName+ " from registerFile is used by Instruction " + reserved +
                    " \n";
        }


    }

    public void handleMulIssue(String instruction, String reg1, String reg2, String instructionName){

        String reserved = simulator.reservationStation2.reserve(instruction,reg1,reg2);

        if(!reserved.equals("F")){
            simulator.instruction.setIssue(instruction,String.valueOf(simulator.cycle));
            String registerName = simulator.instruction.getWbRegister(instruction);

            simulator.registerFile.setRegister(registerName,reserved);

            log += "The instruction " + instructionName + " is Issued at cycle " + simulator.cycle +
                    " and added to reservation station " + reserved + " \n" +
                    "The Register " +registerName+ " from registerFile is used by Instruction " + reserved +
                    " \n";
        }
    }

    public void handleLoadIssue(String instruction, String reg1, String reg2, String instructionName){

        String reserved = simulator.buffers.reserve(instruction,reg1,reg2);

        if(!reserved.equals("F")){
            simulator.instruction.setIssue(instruction,String.valueOf(simulator.cycle));
            String registerName = simulator.instruction.getWbRegister(instruction);

            simulator.registerFile.setRegister(registerName,reserved);

            log += "The instruction " + instructionName + " is Issued at cycle " + simulator.cycle +
                    " and added to reservation station " + reserved + " \n" +
                    "The Register " +registerName+ " from registerFile is used by Instruction " + reserved +
                    " \n";
        }
    }
}
