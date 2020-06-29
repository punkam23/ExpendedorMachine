package expendedor.modelo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expendedor.accions.items.ActionXYZ1;
import expendedor.modelo.moneda.CoinValues;
import expendedor.modelo.moneda.MetodoPago;
import expendedor.modelo.moneda.Moneda;
import expendedor.modelo.moneda.Monedero;
import expendedor.modelo.producto.Item;
import expendedor.util.ModelMachine;
import expendedor.util.RestConnection;
import expendedor.util.request.ItemDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XYZ1 implements Expendedor {
    private ModelMachine modelMachine = ModelMachine.XYZ1;
    private Compra compra;
    private static Logger logger = Logger.getLogger(XYZ1.class.getName());
    private ObjectMapper objectMapper = new ObjectMapper();
    private ExpendedorConfig expendedorConfig;

    @Override
    public void sumarizeTotal(TextIO textIO) {
        CoinValues centavos = textIO.newEnumInputReader(CoinValues.class)
                .read("Introduzca una moneda");
        compra = ObjectUtils.defaultIfNull(compra, new Compra());
        compra.setMetodoPago(MetodoPago.EFECTIVO);
        compra.getTotal().add(centavos.getValor());
    }

    @Override
    public void devolucion() {
        Iterator value = compra.getTotal().iterator();
        while (value.hasNext()) {
            //envia senal al expendedor para liberar moneda.
            int lastIndex = compra.getTotal().size() - 1;
            int refoundValue = compra.getTotal().get(0);
            if(refoundValue < 0){
                refoundValue = Math.abs(refoundValue);
                List<CoinValues> coinValues = Arrays.asList(CoinValues.values());
                Collections.reverse(coinValues);
                for (CoinValues coin : coinValues) {
                    if(refoundValue >= coin.getValor()){
                        ArrayList<Moneda> cash;
                        File file = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/monedero.json");
                        try {
                            Monedero monedero = objectMapper.readValue(file, Monedero.class);
                            if (CoinValues.get(coin.getValor()).getValor() < 100) {
                                cash = monedero.getCoins();
                            } else {
                                cash = monedero.getTickets();
                            }
                            Moneda monedaStack = cash.stream()
                                    .filter(moneda1 -> moneda1.getValor() == coin.getValor())
                                    .findFirst()
                                    .orElse(null);
                            monedaStack.setCantidad(monedaStack.getCantidad() - 1);
                            objectMapper.writeValue(file, monedero);
                            int remainMount = refoundValue - monedaStack.getValor();
                            if(remainMount > 0){
                                compra.getTotal().add(refoundValue - monedaStack.getValor());
                            }
                            compra.getTotal().remove(0);
                            refoundValue = remainMount;
                            logger.log(Level.INFO, "monton devolucion :" + monedaStack.getValor());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                logger.log(Level.INFO, "monton devolucion :" + compra.getTotal().get(lastIndex));
                compra.getTotal().remove(lastIndex);
            }
        }
    }

    @Override
    public Item findProducto(TextIO textIO) {
        String coordenadas = textIO.newStringInputReader().read("Coordenada");
        Item foundItem = null;
        try {
            File file = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/items.json");
            ArrayList<Item> items = objectMapper.readValue(file, new TypeReference<ArrayList<Item>>() {
            });
            foundItem = items.stream()
                    .filter(item -> item.getCoordenadas().equals(coordenadas) && item.getCantidad() > 0)
                    .findFirst().orElse(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundItem;
    }

    @Override
    public void abrirCompuerta(String coordenada) {
        //envia senal al expendedor para liberar producto.
        try {
            File file = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/items.json");
            ArrayList<Item> items = objectMapper.readValue(file, new TypeReference<ArrayList<Item>>() {
            });
            Item foundItem = items.stream()
                    .filter(item -> item.getCoordenadas().equals(coordenada))
                    .findFirst().orElse(null);
            foundItem.setCantidad(foundItem.getCantidad() - 1);
            objectMapper.writeValue(file, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "abriendo compuerta: " + coordenada);
    }

    @Override
    public void generarCompra(Item item) {
        //llamado al restApi para generar una transaccion
        //aumentar monto en monedero
        try {

            File file = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/monedero.json");
            File soldItemsFile = new File("/Users/yehoshuamatamorosvalverde/IdeaProjects/maquina/src/main/resources/config/soldItems.json");
            Monedero monedero = objectMapper.readValue(file, Monedero.class);
            ArrayList<Compra> soldItems = objectMapper.readValue(soldItemsFile, new TypeReference<ArrayList<Compra>>() {
            });
            generateSoldItem(item, soldItems);
            objectMapper.writeValue(soldItemsFile, soldItems);
            Integer precioProducto = new Integer(item.getPrecio());
            Iterator value = compra.getTotal().iterator();
            while (value.hasNext()) {
                int lastIndex = compra.getTotal().size() - 1;
                int monedaValor = compra.getTotal().get(lastIndex);
                ArrayList<Moneda> cash = new ArrayList<>();
                compra.getTotal().remove(lastIndex);
                Moneda moneda = new Moneda();
                moneda.setValor(monedaValor);
                if (CoinValues.get(monedaValor).getValor() < 100) {
                    cash = monedero.getCoins();
                } else {
                    cash = monedero.getTickets();
                }
                Moneda monedaStack = cash.stream()
                        .filter(moneda1 -> moneda1.getValor() == monedaValor)
                        .findFirst()
                        .orElse(null);
                if (Objects.nonNull(monedaStack)) {
                    monedaStack.setCantidad(monedaStack.getCantidad() + 1);
                } else {
                    moneda.setCantidad(1);
                    monedero.getCoins().add(moneda);
                }

                precioProducto = precioProducto - monedaValor;
                if (precioProducto <= 0) {
                    compra.getTotal().add(precioProducto);
                    break;
                }
            }
            objectMapper.writeValue(file, monedero);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateSoldItem(Item item, ArrayList<Compra> soldItems) {
        ItemDTO itemSold = new ItemDTO();
        itemSold.setCantidad(1);
        itemSold.setCoordenadas(item.getCoordenadas());
        itemSold.setPrecio(item.getPrecio());
        itemSold.setNombre(item.getNombre());
        compra.getItemSold().add(itemSold);
        compra.setFechaCompra(new Date());
        soldItems.add(compra);
        RestConnection restConnection = new RestConnection();
        restConnection.sendCompraRequest(compra, expendedorConfig);
    }

    @Override
    public boolean validarMontoTotal(Item item) {

        return getTotal() >= item.getPrecio();
    }

    @Override
    public void run(boolean programRun, TextIO textIO, ExpendedorConfig expendedorConfig) {
        this.expendedorConfig = expendedorConfig;
        TextTerminal textTerminal = textIO.getTextTerminal();
        while (programRun) {
            ActionXYZ1 action = textIO.newEnumInputReader(ActionXYZ1.class)
                    .read("select an action");
            switch (action) {
                case ADDCOIN:
                    sumarizeTotal(textIO);
                    break;
                case SELECTPRODUCT:
                    Item item = findProducto(textIO);
                    if (Objects.nonNull(item)) {
                        if (validarMontoTotal(item)) {
                            abrirCompuerta(item.getCoordenadas());
                            generarCompra(item);
                        } else {
                            textTerminal.printf("Necesita mas monedas para el item");
                        }
                    }else{
                        textTerminal.printf("Item agotado, seleccione otro");
                    }
                    break;
                case DEVOLUCION:
                    devolucion();
                    break;
                case EXIT:
                    programRun = false;
                    break;

            }
        }
    }

    private int getTotal() {
        return compra.getTotal().stream()
                .reduce(0, Integer::sum);
    }

    public ModelMachine getModelo() {
        return modelMachine;
    }
}
