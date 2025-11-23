package co.iudigital.supermercado.service;

import co.iudigital.supermercado.model.*;
import co.iudigital.supermercado.threads.HiloCajera;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Servicio que gestiona la simulación del proceso de cobro en el supermercado
 */
public class SimulacionService {
    private final Consumer<String> logger;
    private final List<Cajera> cajerasDisponibles;

    public SimulacionService(Consumer<String> logger) {
        this.logger = logger;
        // Crear cajeras predefinidas (hardcoded)
        this.cajerasDisponibles = new ArrayList<>();
        cajerasDisponibles.add(new Cajera(1, "María"));
        cajerasDisponibles.add(new Cajera(2, "Ana"));
        cajerasDisponibles.add(new Cajera(3, "Laura"));
        cajerasDisponibles.add(new Cajera(4, "Carmen"));
        cajerasDisponibles.add(new Cajera(5, "Sofía"));
    }

    /**
     * Ejecuta la simulación de cobro para múltiples clientes
     * 
     * @param clientes Lista de clientes a procesar
     * @param numCajerasSimultaneas Número de cajeras que procesarán clientes simultáneamente
     * @param asignacionAleatoria Si true, asigna clientes a cajeras aleatoriamente; si false, usa round-robin
     * @return Lista de registros de compra ordenados
     */
    public List<RegistroCompra> ejecutar(List<Cliente> clientes, int numCajerasSimultaneas, boolean asignacionAleatoria) {
        if (clientes == null || clientes.isEmpty()) {
            logger.accept("No hay clientes para procesar.");
            return new ArrayList<>();
        }

        logger.accept("\n========== INICIANDO SIMULACIÓN ==========");
        logger.accept(String.format("Total de clientes: %d", clientes.size()));
        logger.accept(String.format("Cajeras disponibles: %d", cajerasDisponibles.size()));
        logger.accept(String.format("Cajeras simultáneas: %d", numCajerasSimultaneas));
        logger.accept(String.format("Asignación: %s", asignacionAleatoria ? "Aleatoria" : "Round-Robin"));
        logger.accept("==========================================\n");

        long inicioSimulacion = System.currentTimeMillis();

        // Limitar el número de cajeras simultáneas al número disponible
        int cajerasAUsar = Math.min(numCajerasSimultaneas, cajerasDisponibles.size());
        List<Cajera> cajerasActivas = cajerasDisponibles.subList(0, cajerasAUsar);

        // Crear un ExecutorService con pool de hilos limitado
        ExecutorService executor = Executors.newFixedThreadPool(cajerasAUsar);
        List<Future<RegistroCompra>> futures = new ArrayList<>();
        List<HiloCajera> hilos = new ArrayList<>();

        Random random = asignacionAleatoria ? new Random() : null;
        int indiceCajera = 0;

        // Crear y ejecutar hilos para cada cliente
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            
            // Asignar cajera
            Cajera cajera;
            if (asignacionAleatoria) {
                cajera = cajerasActivas.get(random.nextInt(cajerasActivas.size()));
            } else {
                // Round-robin
                cajera = cajerasActivas.get(indiceCajera % cajerasActivas.size());
                indiceCajera++;
            }

            HiloCajera hilo = new HiloCajera(cajera, cliente, logger);
            hilos.add(hilo);
            Future<RegistroCompra> future = executor.submit(() -> {
                hilo.run();
                return hilo.getResultado();
            });
            futures.add(future);
        }

        // Esperar a que todos los hilos terminen
        List<RegistroCompra> resultados = new ArrayList<>();
        try {
            for (Future<RegistroCompra> future : futures) {
                try {
                    RegistroCompra resultado = future.get();
                    if (resultado != null) {
                        resultados.add(resultado);
                    }
                } catch (ExecutionException e) {
                    logger.accept("Error al procesar compra: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.accept("Simulación interrumpida.");
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long finSimulacion = System.currentTimeMillis();
        long tiempoTotalSimulacion = finSimulacion - inicioSimulacion;

        // Ordenar resultados por nombre de cliente
        resultados.sort(Comparator.comparing(RegistroCompra::getNombreCliente));

        // Imprimir resumen final
        imprimirResumen(resultados, tiempoTotalSimulacion);

        return resultados;
    }

    /**
     * Imprime el resumen completo y ordenado de todas las compras
     */
    private void imprimirResumen(List<RegistroCompra> resultados, long tiempoTotalSimulacion) {
        logger.accept("\n\n========== RESUMEN FINAL DE COMPRAS ==========\n");

        double totalGeneral = 0.0;
        long tiempoAcumulado = 0L;

        for (RegistroCompra registro : resultados) {
            logger.accept(String.format("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
            logger.accept(String.format("Cliente: %s", registro.getNombreCliente()));
            logger.accept(String.format("Cajera: %s (ID: %d)", registro.getNombreCajera(), registro.getCajeraId()));
            logger.accept("Productos:");

            for (DetalleProcesoProducto detalle : registro.getDetalles()) {
                Producto p = detalle.getProducto();
                logger.accept(String.format("  • %s", p.getNombre()));
                logger.accept(String.format("    - Cantidad: %d unidades", p.getCantidad()));
                logger.accept(String.format("    - Precio unitario: $%.2f", p.getPrecio()));
                logger.accept(String.format("    - Subtotal: $%.2f", p.total()));
                logger.accept(String.format("    - Tiempo de procesamiento: %d ms", detalle.getTiempoMs()));
            }

            logger.accept(String.format("Total de la compra: $%.2f", registro.getTotalCompra()));
            logger.accept(String.format("Tiempo total de la compra: %d ms", registro.getTiempoTotalMs()));
            logger.accept("");

            totalGeneral += registro.getTotalCompra();
            tiempoAcumulado += registro.getTiempoTotalMs();
        }

        logger.accept("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.accept(String.format("TOTAL GENERAL (todas las compras): $%.2f", totalGeneral));
        logger.accept(String.format("Tiempo acumulado (suma de todos los tiempos): %d ms", tiempoAcumulado));
        logger.accept(String.format("Tiempo real de simulación (wall-clock time): %d ms", tiempoTotalSimulacion));
        logger.accept("==================================================\n");
    }

    public List<Cajera> getCajerasDisponibles() {
        return new ArrayList<>(cajerasDisponibles);
    }
}

