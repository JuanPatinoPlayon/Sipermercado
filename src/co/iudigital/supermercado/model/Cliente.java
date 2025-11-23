package co.iudigital.supermercado.model;

import java.util.List;

public class Cliente {
    private final String nombre;
    private final List<Producto> productos;

    public Cliente(String nombre, List<Producto> productos) {
        this.nombre = nombre;
        this.productos = productos;
    }

    public String getNombre() { return nombre; }
    public List<Producto> getProductos() { return productos; }
    public double totalCompra() { return productos.stream().mapToDouble(Producto::total).sum(); }
}
