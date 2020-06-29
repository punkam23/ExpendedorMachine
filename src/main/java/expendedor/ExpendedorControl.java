package expendedor;

import com.fasterxml.jackson.databind.ObjectMapper;
import expendedor.modelo.Expendedor;
import expendedor.modelo.ExpendedorConfig;
import expendedor.modelo.ExpendedorFactory;
import expendedor.util.ModelMachine;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import java.io.File;
import java.util.Objects;

public class ExpendedorControl {
    static ModelMachine machinemodel;
    static boolean programRun = true;

    public static void main(String[] args) {
        evaluateCommandParameters(args);
        ExpendedorFactory expendedorFactory = new ExpendedorFactory();
        Expendedor expendedor = expendedorFactory.getExpendedorModel(machinemodel);
        ExpendedorConfig expendedorConfig = null;
        if(Objects.nonNull(expendedor)){
            try {
                File file = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/expendedor.json");
                ObjectMapper objectMapper = new ObjectMapper();
                expendedorConfig = objectMapper.readValue(file, ExpendedorConfig.class);

            }catch (Exception ex){
                System.out.println("No config file found. " + ex.getMessage());
                System.exit(0);
            }
//            expendedor.
            TextIO textIO = TextIoFactory.getTextIO();
            //ejecuta el programa en la clase expendedor que le corresponde
            expendedor.run(programRun, textIO, expendedorConfig);
            System.exit(0);
        }
    }

    private static void evaluateCommandParameters(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--model")) {
                try {
                    machinemodel = ModelMachine.valueOf(args[++i]);
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("Argument" + args[i] + " must be an number.");
                    System.exit(1);
                }
            }
        }
    }

}
