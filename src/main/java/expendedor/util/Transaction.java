package expendedor.util;

public class Transaction {
    private String numeroCuenta;
    private Integer monto;

    public Transaction(String numeroCuenta, Integer monto) {

        this.numeroCuenta = numeroCuenta;
        this.monto = monto;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", monto=" + monto +
                '}';
    }
}
