package expendedor.modelo.moneda;


import java.util.ArrayList;

public class Monedero {

    private ArrayList<Moneda> coins;
    private ArrayList<Moneda> tickets;

    public ArrayList<Moneda> getCoins() {
        return coins;
    }

    public void setCoins(ArrayList<Moneda> coins) {
        this.coins = coins;
    }

    public ArrayList<Moneda> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Moneda> tickets) {
        this.tickets = tickets;
    }
}
