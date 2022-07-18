import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class Buffers {
    Hashtable<String,String[]> station;
    Simulation simulator;

    boolean empty = false;
    String emptyThis = "";
    String log = "";

    public Buffers(Simulation simulator){

        station = new Hashtable<String, String[]>();

        String[] reserve1 = {"0","","",""};
        String[] reserve2 = {"0","","",""};
        String[] reserve3 = {"0","","",""};

        station.put("L1",reserve1);
        station.put("L2",reserve2);
        station.put("L3",reserve3);

        this.simulator = simulator;
    }


    public String reserve(String op,String address1, String address2){

        String isEmpty = emptyReserve();
        if(isEmpty.equals("F")){
            return "S";
        }

        String[] reserve = station.get(isEmpty);
        reserve[0] = "1";
        reserve[1] = op;
        reserve[2] = address1;
        reserve[3] = address2;
        station.put(isEmpty, reserve);

        return isEmpty;
    }


    public boolean available(String name){
        return !station.get(name)[0].equals("1");
    }

    public void setBusy(String name, String signal){
        station.get(name)[0] = signal;
    }

    public String emptyReserve(){

            if(available("L1"))
                return "L1";
            if(available("L2"))
                return "L2";
            if(available("L3"))
                return "L3";

            return "F";
    }

    public String getRegister(String key){
        return this.station.get(key)[3];
    }

    public void setRegister(String key,String register){
        String[] reserve = this.station.get(key);
        reserve[3] = register;
        this.station.put(key,reserve);
    }

    public String getOp(String key){
        return station.get(key)[1];
    }

    public void voidIt(String key){
        String[] reserve = {"0","","",""};
        this.station.put(key,reserve);
    }

    public String print(){
        return log;
    }
    public void emptyLogs() {
        log = "";
    }

    public String showComponent() {
        String component = "";

        Set<String> ins = station.keySet();
        for(String ki : ins){
            String secInstruction[] = station.get(ki);
            component +=  "[" +ki+", "+secInstruction[0] + ", " +
                    secInstruction[2] + "+" + secInstruction[3] + "]" + "\n";
            //component += Arrays.toString(secInstruction) + "\n";
        }
        return component;
    }

    public void start(){
        // getting keySet() into Set
        Set<String> ins = station.keySet();

        // for-each loop
        for(String ki : ins)
        {

            String key = getOp(ki);

            if(!key.equals("")) {

                //can execute? start executing. can not ? check dependencies

                if (simulator.cycle > Integer.parseInt(simulator.instruction.getIssue(key)) &&
                        simulator.instruction.getExStart(key).equals("")) {

                    simulator.instruction.setExStart(key, String.valueOf(simulator.cycle));

                    log += "The instruction " + key +
                            "Started executing at cycle " + simulator.cycle + "\n";
                }


                //finish
                if (!simulator.instruction.getExStart(key).equals("") && simulator.instruction.
                        getExEnd(key).equals("")) {

                    String name = this.simulator.instruction.getInstruction(key);
                    int cycles = simulator.instruction.instructionsCycles.get(name);

                    int start = Integer.parseInt(simulator.instruction.getExStart(key));

                    int finish = simulator.cycle - start + 1;

                    if (cycles == finish) {
                        simulator.instruction.setExEnd(key, String.valueOf(simulator.cycle));
                        log += "The instruction " + key + " finished executing " +
                                " at cycle " + simulator.cycle + "\n";
                    }


                }

                //writeBack
                if (!simulator.instruction.getExEnd(key).equals("") && simulator.cycle >
                        Integer.parseInt(simulator.instruction.getExEnd(key)) &&
                        simulator.instruction.getWrite(key).equals("")) {

                    simulator.instruction.setWrite(key, String.valueOf(simulator.cycle));

                    String writeBackRegisterName = simulator.instruction.getWbRegister(key);
                    simulator.registerFile.setRegister(writeBackRegisterName, "V");


                    empty = true;
                    emptyThis = ki;


                    log += "Instruction " + simulator.instruction.getInstruction(key) +
                            "writes back into the bus" + "\n" + "The register " +
                            writeBackRegisterName + " is available again";
                }
            }

        }

        if(empty){
            voidIt(emptyThis);
            emptyThis = "";
            empty = false;
        }
    }

}
