package co.iudigital.supermercado.model;

/**
 * Almacena el detalle del procesamiento de un producto durante una compra
 */
public class DetalleProcesoProducto {
    private final Producto producto;
    private final long tiempoMs;

    public DetalleProcesoProducto(Producto producto, long tiempoMs) {
        this.producto = producto;
        this.tiempoMs = tiempoMs;
    }

    public Producto getProducto() {
        return producto;
    }

    public long getTiempoMs() {
        return tiempoMs;
    }
}

