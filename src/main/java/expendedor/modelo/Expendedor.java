package expendedor.modelo;

import expendedor.modelo.producto.Item;
import expendedor.util.ModelMachine;
import org.beryx.textio.TextIO;

import java.util.ArrayList;

public interface Expendedor {
    ModelMachine getModelo();
    void sumarizeTotal(TextIO textIO);
    void devolucion();
    void generateSoldItem(Item item, ArrayList<Compra> soldItems);
    Item findProducto(TextIO textIO);
    void abrirCompuerta(String coordenada);
    void generarCompra(Item item);
    boolean validarMontoTotal(Item item);
    void run(boolean programRun, TextIO textIO, ExpendedorConfig expendedorConfig);
}
