import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

public class RegisterFile {

    Hashtable<String,String> registers;

    public RegisterFile(){

        registers = new Hashtable<String, String>();

        registers.put("F0","V");
        registers.put("F2","V");
        registers.put("F4","V");
        registers.put("F6","V");
        registers.put("F8","V");
        registers.put("F10","V");

    }

    public void setRegister(String registerName , String value){
        registers.put(registerName,value);
    }

    public String getRegister(String registerName){
        return registers.get(registerName);
    }

    public String showComponent() {
        String component = "";

        Set<String> ins = registers.keySet();
        for(String ki : ins){
            String secInstruction = registers.get(ki);
            component += ki + " : " + secInstruction + "\n";
        }
        return component;
    }
}
