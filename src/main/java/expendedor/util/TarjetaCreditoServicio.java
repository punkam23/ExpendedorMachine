package expendedor.util;

import expendedor.modelo.XYZ2;
import expendedor.modelo.moneda.TarjetaCredito;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TarjetaCreditoServicio {
    private static Logger logger = Logger.getLogger(TarjetaCreditoServicio.class.getName());
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public TarjetaCredito validarTarjetaNumero(String numeroTarjeta){
        logger.log(Level.INFO, "Servicio para validar tajeta");
        TarjetaCredito tarjetaCredito = new TarjetaCredito();
        tarjetaCredito.numeroCuenta = "2342352452452435CR";
        tarjetaCredito.nombreCliente = "Yehoshua Matamoros Valverde";
        return tarjetaCredito;
    }

    public void generarTransaccion(String numeroCuenta, Integer monto) {
        transactions.add(new Transaction(numeroCuenta, monto));
    }

    public Factura flush(String cliente){
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setFechaFactura(new Date());
        factura.setTransactionList(transactions);
        return factura;
    }

    public void send() {
        logger.log(Level.INFO, "Servicio para generar el debito de la tarjeta");
        boolean transactionExitosa = true;
        if(transactionExitosa){
            transactions = null;
        }
    }
}
