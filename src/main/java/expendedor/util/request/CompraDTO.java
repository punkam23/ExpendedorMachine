package expendedor.util.request;

import expendedor.modelo.Compra;
import expendedor.modelo.moneda.MetodoPago;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompraDTO {
    private MetodoPago metodoPago;
    private Integer expendedorId;
    private Date fechaCompra;
    private ArrayList<Integer> total = new ArrayList<>();
    private List<ItemDTO> itemSold = new ArrayList<>();

    public CompraDTO(Compra compra) {
        this.metodoPago = compra.getMetodoPago();
        this.fechaCompra = new Date();
        this.total = compra.getTotal();
        this.itemSold = compra.getItemSold();
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getExpendedorId() {
        return expendedorId;
    }

    public void setExpendedorId(Integer expendedorId) {
        this.expendedorId = expendedorId;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

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
}
