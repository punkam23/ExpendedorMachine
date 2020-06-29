package expendedor.util;

import java.util.Date;
import java.util.List;

public class Factura {
    private String cliente;
    private Date fechaFactura;
    private List<Transaction> transactionList;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "cliente='" + cliente + '\'' +
                ", fechaFactura=" + fechaFactura +
                ", total=" + getTotal() +
                ", transactionList=" + transactionList +
                '}';
    }

    public int getTotal() {
        return transactionList
                .stream()
                .map(transaction -> transaction.getMonto())
                .reduce(0, Integer::sum);
    }
}
