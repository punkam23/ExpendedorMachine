package expendedor.modelo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import expendedor.accions.items.ActionXYZ2;
import expendedor.modelo.moneda.*;
import expendedor.modelo.producto.Item;
import expendedor.util.*;
import expendedor.util.request.ItemDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XYZ2 implements Expendedor{
    private ModelMachine modelMachine = ModelMachine.XYZ2;
    private Compra compra;
    private static Logger logger = Logger.getLogger(XYZ2.class.getName());
    private ObjectMapperWrapper objectMapper = new ObjectMapperWrapper();
    private TarjetaCredito tarjetaCredito;
    private TarjetaCreditoServicio tarjetaCreditoServicio;
    private RestConnection restConnection;
    private ExpendedorConfig expendedorConfig;

    @Override
    public void sumarizeTotal(TextIO textIO) {
        compra = ObjectUtils.defaultIfNull(compra, new Compra());
        if(!MetodoPago.TARGETA.equals(compra.getMetodoPago())){
            CoinValues centavos = textIO.newEnumInputReader(CoinValues.class)
                    .read("Introduzca una moneda");
            compra = ObjectUtils.defaultIfNull(compra, new Compra());
            compra.setMetodoPago(MetodoPago.EFECTIVO);
            compra.getTotal().add(centavos.getValor());
        }
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
                if(compra.getMetodoPago().equals(MetodoPago.EFECTIVO)){
                    ArrayList<Moneda> cash;
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
                    objectMapper.writeValue(file, monedero);
                }else{
                    tarjetaCreditoServicio.generarTransaccion(tarjetaCredito.numeroCuenta, compra.getTotal().get(0));

                }
                compra.getTotal().remove(lastIndex);
                precioProducto = precioProducto - monedaValor;
                if (Math.abs(precioProducto) > 0) {
                    compra.getTotal().add(precioProducto);
                    break;
                }
            }
            if(compra.getMetodoPago().equals(MetodoPago.EFECTIVO))
                objectMapper.writeValue(file, monedero);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateSoldItem(Item item, ArrayList<Compra> soldItems) {
        Compra singleItem = new Compra();
        ItemDTO itemSold = new ItemDTO();
        itemSold.setCantidad(1);
        itemSold.setCoordenadas(item.getCoordenadas());
        itemSold.setPrecio(item.getPrecio());
        itemSold.setNombre(item.getNombre());
        singleItem.setMetodoPago(compra.getMetodoPago());
        singleItem.setTotal(new ArrayList<Integer>(){{add(item.getPrecio());}});
        singleItem.setFechaCompra(new Date());
        singleItem.getItemSold().add(itemSold);
        soldItems.add(singleItem);
        restConnection.sendCompraRequest(singleItem, expendedorConfig);
    }

    @Override
    public boolean validarMontoTotal(Item item) {
        return getTotal(item.getPrecio()) >= item.getPrecio();
    }

    public void validarTarjeta(TextIO textIO) {
        //se valida tarjeta con servicio externo, quien devuelve la informacion de la tarjeta de ser correcta;
        String numeroTarjeta = textIO.newStringInputReader().read("Introduzca numero de tajerta");
        tarjetaCreditoServicio = new TarjetaCreditoServicio();
        tarjetaCredito = tarjetaCreditoServicio.validarTarjetaNumero(numeroTarjeta);
        if(Objects.nonNull(tarjetaCredito)){
            compra = ObjectUtils.defaultIfNull(compra, new Compra());
            compra.setMetodoPago(MetodoPago.TARGETA);
            sumarizeTotal(textIO);
        }else {
            textIO.getTextTerminal().printf("Tarjeta de CreditoInvalida");
        }
    }

    public ModelMachine getModelo() {
        return modelMachine;
    }
    @Override
    public void run(boolean programRun, TextIO textIO, ExpendedorConfig expendedorConfig) {
        TextTerminal textTerminal = textIO.getTextTerminal();
        while(programRun) {
            ActionXYZ2 action = textIO.newEnumInputReader(ActionXYZ2.class)
                    .read("select an action");
            switch (action){
                case ADDCOIN:
                    if(Objects.nonNull(compra) && MetodoPago.TARGETA.equals(compra.getMetodoPago())) {
                        compra = null;
                        tarjetaCredito = null;
                    }
                    sumarizeTotal(textIO);
                    break;
                case ADDCARD:
                    validarTarjeta(textIO);
                    break;
                case SELECTPRODUCT:
                    Item item = findProducto(textIO);
                    if (Objects.nonNull(item)){
                        if(validarMontoTotal(item)){
                            abrirCompuerta(item.getCoordenadas());
                            generarCompra(item);
                        } else {
                            textTerminal.printf("Necesita mas monedas para el item seleccionado");
                        }
                    }else{
                        textTerminal.printf("Item agotado, seleccione otro");
                    }
                    break;
                case DEVOLUCION:
                    if(MetodoPago.TARGETA.equals(compra.getMetodoPago())) {
                        Factura factura = tarjetaCreditoServicio.flush(tarjetaCredito.nombreCliente);
                        tarjetaCreditoServicio.send();
                        tarjetaCredito = null;
                        imprimir(factura);
                    }else {
                        devolucion();
                    }
                    compra = null;
                    break;
                case EXIT:
                    programRun = false;
                    break;
            }
        }
    }

    private void imprimir(Factura factura) {
        logger.log(Level.INFO, factura.toString());
    }

    private int getTotal(int precio) {
        if(compra.getMetodoPago().equals(MetodoPago.TARGETA)){
            compra.getTotal().add(precio);
            return precio;
        }
        return compra.getTotal().stream()
                .reduce(0, Integer::sum);
    }
}
