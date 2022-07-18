import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class ReservationStation {

    Hashtable<String,String[]> station;

    Simulation simulator;
    String type;

    boolean empty = false;
    String emptyThis = "";

    String log = "";

    public ReservationStation(Simulation simulator, String type){

        station = new Hashtable<String, String[]>();

        if(type.equals("ADD-SUB")) {

            String[] reserve1 = {"0","","",""};
            String[] reserve2 = {"0","","",""};
            String[] reserve3 = {"0","","",""};

            station.put("A1",reserve1);
            station.put("A2",reserve2);
            station.put("A3",reserve3);
        }
        else if(type.equals("MUL-DIV")){

            String[] reserve1 = {"0","","",""};
            String[] reserve2 = {"0","","",""};


            station.put("M1",reserve1);
            station.put("M2",reserve2);
        }

        this.simulator = simulator;
        this.type = type;
    }



    public String reserve(String op, String reg1, String reg2){
        String isEmpty = emptyReserve();
        if(isEmpty.equals("F")){
            return "S";
        }



        String registerOne = checkDependency(reg1);
        String registerTwo = checkDependency(reg2);

        String[] reserve = station.get(isEmpty);
        reserve[0] = "1";
        reserve[1] = op;
        reserve[2] = registerOne;
        reserve[3] = registerTwo;
        station.put(isEmpty, reserve);

        return isEmpty;
    }



    public String getOp(String key){
        return station.get(key)[1];
    }

    public void setOp(String name, String op){
        station.get(name)[1] = op;
    }

    public boolean isBusy(String key){
        return station.get(key)[0].equals("1");
    }

    public void voidIt(String key){
        String[] reserve = {"0","","",""};
        this.station.put(key,reserve);
    }

    public void setBusy(String key,String value){
        station.get(key)[0] = value;
    }

    public String getReg1(String name){
        return station.get(name)[2];
    }

    public void setReg1(String key, String value){
        String[] reserve = this.station.get(key);
        reserve[2] = value;
        station.put(key,reserve);
    }

    public void setReg2(String key, String value){
        String[] reserve = this.station.get(key);
        reserve[3] = value;
        station.put(key,reserve);
    }

    public String getReg2(String name){
        return station.get(name)[3];
    }


    /**
     * this function takes a register name, and check if another instruction in the reservation station
     * already uses that register, if the register taken by another instruction it returns the instruction
     * name that take that reg, if not taken return the input register name
     * @param reg is the register to check on
     * @return reservation channel name if reg taken, input register name if not taken
     */
    public String checkDependency(String reg){

            String register = simulator.registerFile.getRegister(reg);

            if(register.equals("E"))
                return reg;
            else
                return register;

    }



    /**
     * if there is an empty reservation station we return the Name of the station
     * @return the name of the station if empty, 'F' if full
     */
    public String emptyReserve(){
        if(type.equals("ADD-SUB")){
            if(!isBusy("A1"))
                return "A1";
            if(!isBusy("A2"))
                return "A2";
            if(!isBusy("A3"))
                return "A3";
        }
        else{
            if(!isBusy("M1"))
                return "M1";
            if(!isBusy("M2"))
                return "M2";
        }

        return "F";
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
            component +=  "[" +ki+", "+secInstruction[0] + ", " + secInstruction[1] + ", " +
                    (secInstruction[2].equals("V")? secInstruction[2] : "") +
                    ", " + (secInstruction[3].equals("V")? secInstruction[3] : "") + ", " +
                    (secInstruction[2].equals("V")? "" : secInstruction[2]) + ", " +
                    (secInstruction[3].equals("V")? "" : secInstruction[3]) + "]" +"\n";
            //component += Arrays.toString(secInstruction) + "\n";
        }
        return component;
    }

    /**
     * check if the instruction is executing or not, if executing check if its finished executing or not
     * if instruction finished remove it and write back and change registerFile accordingly, if is not
     * executing check if its dependinces are available to start executing
     */

    public void start(){
        // getting keySet() into Set
        Set<String> ins = station.keySet();

        // for-each loop
        for(String ki : ins)
        {

            String key = getOp(ki);

            if(!key.equals("")) {
                //can execute? start executing. can not ? check dependencies
                if (simulator.cycle > Integer.parseInt(simulator.instruction.getIssue(key))) {

                    String firstRegister = getReg1(ki);
                    String secRegister = getReg2(ki);


                    if (!firstRegister.equals("V") || !secRegister.equals("V")) {
                        if (!firstRegister.equals("V")) {
                            String register = simulator.instruction.getRegister1(key);
                            register = simulator.registerFile.getRegister(register);
                            if (register.equals("V")) {
                                setReg1(ki, register);
                                log += "The Register " + simulator.instruction.getRegister1(key) +
                                        " is now available for Instruction " + simulator.instruction.getInstruction(key) +
                                        " to use in Vi " + "\n";
                            }
                        }

                        if (!secRegister.equals("V")) {
                            String register = simulator.instruction.getRegister2(key);
                            register = simulator.registerFile.getRegister(register);
                            if (register.equals("V")) {
                                setReg2(ki, register);
                                log += "The Register " + simulator.instruction.getRegister2(key) +
                                        " is now available for Instruction " + simulator.instruction.getInstruction(key) +
                                        " to use in Vi " + "\n";
                            }
                        }
                    }

                    firstRegister = getReg1(ki);
                    secRegister = getReg2(ki);


                    if (firstRegister.equals("V") && secRegister.equals("V") && simulator.instruction.getExStart(key).equals("")) {
                        simulator.instruction.setExStart(key, String.valueOf(simulator.cycle));
                        log += "The instruction " + simulator.instruction.getInstruction(key) +
                                "started executing at cycle " + simulator.cycle + "\n";
                    }

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
                        log += "The instruction " + simulator.instruction.getInstruction(key) + " finished executing " +
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
                            "writes back in to the bus" + "\n" + "The register " +
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


