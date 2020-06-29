package expendedor.modelo;

import expendedor.modelo.moneda.MetodoPago;
import expendedor.modelo.producto.Item;
import expendedor.util.request.ItemDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Compra {
    private MetodoPago metodoPago;
    private Date fechaCompra;
    private ArrayList<Integer> total = new ArrayList<>();
    private List<ItemDTO> itemSold = new ArrayList<>();

    public ArrayList<Integer> getTotal() {
        return total;
    }

    public void setTotal(ArrayList<Integer> total) {
        this.total = total;
    }

    public List<ItemDTO> getItemSold() {
        return itemSold;
    }

    public void setItemSold(List<ItemDTO> itemSold) {
        this.itemSold = itemSold;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
