package co.iudigital.supermercado.model;

public class Cajera {
    private final int id;
    private final String nombre;

    public Cajera(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
}
