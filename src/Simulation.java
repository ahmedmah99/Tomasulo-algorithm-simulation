import java.util.Set;

public class Simulation {

    int cycle = 1;


    Instruction instruction = new Instruction(this);

    String[] instructionName = {"LW","LW","MUL","SUB","DIV","ADD"};

    RegisterFile registerFile = new RegisterFile();
    ReservationStation reservationStation = new ReservationStation(this, "ADD-SUB");
    ReservationStation reservationStation2 = new ReservationStation(this, "MUL-DIV");
    Buffers buffers = new Buffers(this);

    boolean rolling = true;

    public void run(){

        loadInstructions();

        while(rolling){

            System.out.println("=====================");
            System.out.println("Cycle " + cycle + "\n");
            System.out.println("=====================");

            instruction.start();
            reservationStation.start();
            reservationStation2.start();
            buffers.start();

            print();
            showComponentValues();
            emptyLogs();

            cycle = cycle + 1;

            this.stop();

        }
    }



    public void loadInstructions(){
        instruction.putInstruction("LW",  "F6" ,"32", "R2", "", "", "","");
        instruction.putInstruction("LW",  "F2" ,"44", "R3", "", "", "","");
        instruction.putInstruction("MUL",  "F0" ,"F2", "F4", "", "", "","");
        instruction.putInstruction("SUB",  "F8" ,"F6", "F2", "", "", "","");
        instruction.putInstruction("DIV",  "F10" ,"F0", "F6", "", "", "","");
        instruction.putInstruction("ADD",  "F6" ,"F8", "F2", "", "", "","");
    }

    public void print(){
        if(!this.instruction.print().equals(""))
            System.out.println(this.instruction.print());
        if(!this.reservationStation.print().equals(""))
            System.out.println(this.reservationStation.print());
        if(!this.reservationStation2.print().equals(""))
            System.out.println(this.reservationStation2.print());
        if(!this.buffers.print().equals(""))
            System.out.println(this.buffers.print());
    }

    public void emptyLogs(){
        this.instruction.emptyLogs();
        this.reservationStation.emptyLogs();
        this.reservationStation2.emptyLogs();
        this.buffers.emptyLogs();
    }

    public void showComponentValues(){
        System.out.println(this.instruction.showComponent());
        System.out.println(this.reservationStation.showComponent());
        System.out.println(this.reservationStation2.showComponent());
        System.out.println(this.buffers.showComponent());
        System.out.println(this.registerFile.showComponent());
    }


    /**
     * checks if the all the instructions have finished writing back and executing
     */
    public void stop(){

        int writtenBack = 0;

        Set<String> wb = this.instruction.station.keySet();

        for(String write : wb)
        {
            String writeBack = this.instruction.getWrite(write);
            if(!writeBack.equals("")){
                writtenBack += 1;
            }
        }

        if(writtenBack == this.instructionName.length){
            this.rolling = false;
        }

    }





}
