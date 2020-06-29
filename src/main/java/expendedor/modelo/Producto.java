package expendedor.modelo;

public class Producto {
    private String coordenada;
    private int precio;

//    public Producto(String coordenada) {
//        this.coordenada = coordenada;
//    }

    public String getCoordenada() {
        return this.coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecion(int precio) {
        this.precio = precio;
    }
}
