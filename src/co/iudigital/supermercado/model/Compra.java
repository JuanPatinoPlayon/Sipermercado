package co.iudigital.supermercado.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa una compra completa con toda su información
 */
public class Compra {
    private final Cliente cliente;
    private final Cajera cajera;
    private final List<Producto> productos;
    private final double total;
    private final long tiempoProcesamientoMs;
    private final LocalDateTime fechaHora;

    public Compra(Cliente cliente, Cajera cajera, List<Producto> productos, 
                  double total, long tiempoProcesamientoMs, LocalDateTime fechaHora) {
        this.cliente = cliente;
        this.cajera = cajera;
        this.productos = productos;
        this.total = total;
        this.tiempoProcesamientoMs = tiempoProcesamientoMs;
        this.fechaHora = fechaHora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Cajera getCajera() {
        return cajera;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public double getTotal() {
        return total;
    }

    public long getTiempoProcesamientoMs() {
        return tiempoProcesamientoMs;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
