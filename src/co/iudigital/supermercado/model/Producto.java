package co.iudigital.supermercado.model;

public class Producto {
    private final String nombre;
    private final double precio;
    private final int cantidad;
    private final long tiempoProcesoMs;

    public Producto(String nombre, double precio, int cantidad, long tiempoProcesoMs) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.tiempoProcesoMs = tiempoProcesoMs;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }
    public long getTiempoProcesoMs() { return tiempoProcesoMs; }
    public double total() { return precio * cantidad; }
}
