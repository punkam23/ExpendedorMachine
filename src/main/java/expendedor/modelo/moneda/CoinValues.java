package expendedor.modelo.moneda;

import java.util.HashMap;
import java.util.Map;

public enum CoinValues {
    FIVECENT(5),
    TENCENT(10),
    TWENTYFIVECENT(25),
    FIFTYCENT(50),
    ONEDOLLAR(100),
    TWODOLLAR(200);

    private final Integer valor;

    CoinValues(Integer valor) {
        this.valor = valor;
    }

    private static final Map<Integer, CoinValues> lookup = new HashMap<Integer, CoinValues>();

    static {
        for (CoinValues d : CoinValues.values()) {
            lookup.put(d.getValor(), d);
        }
    }

    public int getValor() {
        return valor;
    }

    public static CoinValues get(Integer value) {
        return lookup.get(value);
    }
}
