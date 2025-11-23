package co.iudigital.supermercado.controllers;

import co.iudigital.supermercado.model.*;
import co.iudigital.supermercado.service.SimulacionService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InicioController {

    @FXML private Button btnIniciar;
    @FXML private Button btnRegistrarCliente;
    @FXML private TextArea consolaSalida;

    private final List<Cliente> clientes = new ArrayList<>();

    @FXML
    public void initialize() {
        consolaSalida.setWrapText(true);
        btnRegistrarCliente.setOnAction(e -> registrarClienteDialog());
        btnIniciar.setOnAction(e -> iniciarSimulacion());
    }

    private void registrarClienteDialog() {
        try {
            String nombre = pedirTexto("Registrar cliente", "Nombre del cliente:");
            if (nombre.isBlank()) return;

            int cantidadProd = pedirEntero("Productos", "¿Cuántos productos comprará " + nombre + "?", 1);
            List<Producto> productos = new ArrayList<>();

            for (int i = 1; i <= cantidadProd; i++) {
                String nombreP = pedirTexto("Producto " + i, "Nombre del producto " + i + ":");
                double precio = pedirDouble("Precio", "Precio unitario de " + nombreP + " (ej: 3500.0):");
                int cantidad = pedirEntero("Cantidad", "Cantidad de " + nombreP + ":", 1);
                long tiempoProc = pedirEntero("Tiempo (ms)", "Tiempo ms por unidad para " + nombreP + " (ej: 200):", 200);
                productos.add(new Producto(nombreP, precio, cantidad, tiempoProc));
            }

            clientes.add(new Cliente(nombre, productos));
            appendLog("Cliente registrado: " + nombre + " | Productos: " + productos.size());
        } catch (Exception ex) {
            mostrarError("Error registrar cliente", ex.getMessage());
        }
    }

    private void iniciarSimulacion() {
        if (clientes.isEmpty()) {
            mostrarAlerta("No hay clientes", "Primero registra al menos 1 cliente.");
            return;
        }

        try {
            int numCajeras = 3; // por defecto como pediste
            SimulacionService simulacion = new SimulacionService(this::appendLog);
            // Ejecutar en hilo aparte para no bloquear la UI
            new Thread(() -> {
                try {
                    long start = System.nanoTime();
                    List<RegistroCompra> resultados = simulacion.ejecutar(clientes, numCajeras, false); // cajeras fijas
                    long end = System.nanoTime();
                    long wallMs = (end - start) / 1_000_000;

                    StringBuilder resumen = new StringBuilder("\n===== RESUMEN FINAL =====\n");
                    double totalGeneral = 0;
                    long acumuladoMs = 0;
                    for (RegistroCompra r : resultados) {
                        resumen.append(String.format("\nCliente: %s (Cajera: %d)\n", r.getNombreCliente(), r.getCajeraId()));
                        for (DetalleProcesoProducto d : r.getDetalles()) {
                            resumen.append(String.format("  - %s x%d => %.2f (tiempo: %d ms)\n",
                                    d.getProducto().getNombre(), d.getProducto().getCantidad(), d.getProducto().total(), d.getTiempoMs()));
                        }
                        resumen.append(String.format("Total cliente: %.2f | Tiempo: %d ms\n", r.getTotalCompra(), r.getTiempoTotalMs()));
                        totalGeneral += r.getTotalCompra();
                        acumuladoMs += r.getTiempoTotalMs();
                    }
                    resumen.append(String.format("\nTiempo real (wall-clock) de la simulación: %d ms\n", wallMs));
                    resumen.append(String.format("Suma tiempos por cliente (acumulado): %d ms\n", acumuladoMs));
                    resumen.append(String.format("Costo total (todas las compras): %.2f\n", totalGeneral));

                    appendLog("\n" + resumen.toString());
                    // Limpiar clientes para nueva simulación si se desea
                    clientes.clear();
                } catch (Exception ex) {
                    appendLog("Error en simulación: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }).start();

        } catch (Exception ex) {
            mostrarError("Error iniciar simulación", ex.getMessage());
        }
    }

    private String pedirTexto(String titulo, String mensaje) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle(titulo);
        dlg.setHeaderText(null);
        dlg.setContentText(mensaje);
        Optional<String> res = dlg.showAndWait();
        return res.orElse("").trim();
    }

    private int pedirEntero(String titulo, String mensaje, int defecto) {
        String t = pedirTexto(titulo, mensaje + " (defecto: " + defecto + ")");
        if (t.isBlank()) return defecto;
        try { return Integer.parseInt(t); } catch (NumberFormatException ex) { return defecto; }
    }

    private double pedirDouble(String titulo, String mensaje) {
        String t = pedirTexto(titulo, mensaje);
        if (t.isBlank()) return 0.0;
        try { return Double.parseDouble(t); } catch (NumberFormatException ex) { return 0.0; }
    }

    private void appendLog(String text) {
        Platform.runLater(() -> {
            consolaSalida.appendText(text + "\n");
            System.out.println(text);
        });
    }

    private void mostrarAlerta(String t, String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
        });
    }

    private void mostrarError(String t, String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
        });
    }
}
