package co.iudigital.supermercado.threads;

import co.iudigital.supermercado.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que representa una cajera procesando la compra de un cliente
 */
public class HiloCajera extends Thread {
    private final Cajera cajera;
    private final Cliente cliente;
    private RegistroCompra resultado;
    private final java.util.function.Consumer<String> logger;

    public HiloCajera(Cajera cajera, Cliente cliente, java.util.function.Consumer<String> logger) {
        this.cajera = cajera;
        this.cliente = cliente;
        this.logger = logger;
        this.setName("Cajera-" + cajera.getNombre() + "-Cliente-" + cliente.getNombre());
    }

    @Override
    public void run() {
        long inicioTotal = System.currentTimeMillis();
        
        logger.accept(String.format("[%s] Iniciando procesamiento de compra para cliente: %s",
                cajera.getNombre(), cliente.getNombre()));

        List<DetalleProcesoProducto> detalles = new ArrayList<>();
        double totalCompra = 0.0;

        // Procesar cada producto del cliente
        for (Producto producto : cliente.getProductos()) {
            long inicioProducto = System.currentTimeMillis();
            
            logger.accept(String.format("[%s] Procesando producto: %s (x%d) para cliente: %s",
                    cajera.getNombre(), producto.getNombre(), producto.getCantidad(), cliente.getNombre()));

            // Simular el tiempo de procesamiento del producto
            // El tiempo total es: tiempo por unidad * cantidad
            long tiempoProceso = producto.getTiempoProcesoMs() * producto.getCantidad();
            
            try {
                Thread.sleep(tiempoProceso);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.accept(String.format("[%s] Procesamiento interrumpido para cliente: %s",
                        cajera.getNombre(), cliente.getNombre()));
                return;
            }

            long finProducto = System.currentTimeMillis();
            long tiempoProductoMs = finProducto - inicioProducto;
            
            double subtotal = producto.total();
            totalCompra += subtotal;

            detalles.add(new DetalleProcesoProducto(producto, tiempoProductoMs));

            logger.accept(String.format("[%s] Producto procesado: %s | Cantidad: %d | Precio unitario: %.2f | " +
                            "Subtotal: %.2f | Tiempo: %d ms",
                    cajera.getNombre(), producto.getNombre(), producto.getCantidad(),
                    producto.getPrecio(), subtotal, tiempoProductoMs));
        }

        long finTotal = System.currentTimeMillis();
        long tiempoTotalMs = finTotal - inicioTotal;

        resultado = new RegistroCompra(
                cliente.getNombre(),
                cajera.getId(),
                cajera.getNombre(),
                detalles,
                totalCompra,
                tiempoTotalMs
        );

        logger.accept(String.format("[%s] Compra completada para cliente: %s | Total: %.2f | Tiempo total: %d ms",
                cajera.getNombre(), cliente.getNombre(), totalCompra, tiempoTotalMs));
    }

    public RegistroCompra getResultado() {
        return resultado;
    }

    public Cajera getCajera() {
        return cajera;
    }

    public Cliente getCliente() {
        return cliente;
    }
}
