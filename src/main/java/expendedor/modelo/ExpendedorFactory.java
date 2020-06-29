package expendedor.modelo;

import expendedor.util.ModelMachine;

import java.util.HashMap;

public class ExpendedorFactory {
    HashMap<ModelMachine, Expendedor> modelosExpendedor = new HashMap<ModelMachine, Expendedor>(){{
        put(ModelMachine.XYZ1, new XYZ1());
        put(ModelMachine.XYZ2, new XYZ2());
    }};

    public Expendedor getExpendedorModel(ModelMachine modelMachine){
        if(modelosExpendedor.containsKey(modelMachine)){
            return modelosExpendedor.get(modelMachine);
        }
        return null;
    }

}
