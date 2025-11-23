package co.iudigital.supermercado.model;

import java.util.List;

/**
 * Almacena el resultado completo de una compra procesada por una cajera
 */
public class RegistroCompra {
    private final String nombreCliente;
    private final int cajeraId;
    private final String nombreCajera;
    private final List<DetalleProcesoProducto> detalles;
    private final double totalCompra;
    private final long tiempoTotalMs;

    public RegistroCompra(String nombreCliente, int cajeraId, String nombreCajera,
                          List<DetalleProcesoProducto> detalles, double totalCompra, long tiempoTotalMs) {
        this.nombreCliente = nombreCliente;
        this.cajeraId = cajeraId;
        this.nombreCajera = nombreCajera;
        this.detalles = detalles;
        this.totalCompra = totalCompra;
        this.tiempoTotalMs = tiempoTotalMs;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public int getCajeraId() {
        return cajeraId;
    }

    public String getNombreCajera() {
        return nombreCajera;
    }

    public List<DetalleProcesoProducto> getDetalles() {
        return detalles;
    }

    public double getTotalCompra() {
        return totalCompra;
    }

    public long getTiempoTotalMs() {
        return tiempoTotalMs;
    }
}

