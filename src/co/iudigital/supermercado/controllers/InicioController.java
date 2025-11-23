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
    @FXML private TextArea consolaSalida;

    @FXML
    public void initialize() {
        consolaSalida.setWrapText(true);
        btnIniciar.setOnAction(e -> iniciarSimulacion());
    }

    private void iniciarSimulacion() {
        try {
            appendLog("\n========== INICIANDO NUEVA SIMULACIÓN ==========");
            
            // Paso 1: Solicitar número de clientes simultáneos
            Integer numClientesSimultaneos = pedirEnteroConCancelacion("Clientes simultáneos", 
                    "¿Cuántos clientes se procesarán simultáneamente?", 3);
            
            if (numClientesSimultaneos == null) {
                appendLog("Simulación cancelada por el usuario.");
                return;
            }
            
            if (numClientesSimultaneos <= 0) {
                mostrarAlerta("Error", "Debe ingresar al menos 1 cliente.");
                return;
            }
            
            // Paso 2: Solicitar tipo de asignación
            String asignacionInput = pedirTextoConCancelacion("Asignación de cajeras", 
                    "¿Asignar cajeras aleatoriamente? (s/n, default: n)");
            
            if (asignacionInput == null) {
                appendLog("Simulación cancelada por el usuario.");
                return;
            }
            
            boolean asignacionAleatoria = asignacionInput.toLowerCase().equals("s") || 
                                         asignacionInput.toLowerCase().equals("si");

            appendLog("Clientes simultáneos: " + numClientesSimultaneos);
            appendLog("Asignación de cajeras: " + (asignacionAleatoria ? "Aleatoria" : "Round-Robin"));
            appendLog("\n--- REGISTRO DE CLIENTES QUE LLEGAN A LA TIENDA ---\n");

            // Paso 3: Registrar cada cliente que "llega"
            List<Cliente> clientes = new ArrayList<>();
            
            for (int i = 1; i <= numClientesSimultaneos; i++) {
                appendLog("--- Cliente " + i + " de " + numClientesSimultaneos + " ---");
                
                // Nombre del cliente
                String nombreCliente = pedirTextoConCancelacion("Cliente " + i, "Nombre del cliente " + i + ":");
                if (nombreCliente == null) {
                    appendLog("Simulación cancelada por el usuario.");
                    return;
                }
                if (nombreCliente.isBlank()) {
                    nombreCliente = "Cliente " + i;
                }
                
                // Cantidad de productos
                Integer cantidadProd = pedirEnteroConCancelacion("Productos", 
                        "¿Cuántos productos comprará " + nombreCliente + "?", 1);
                
                if (cantidadProd == null) {
                    appendLog("Simulación cancelada por el usuario.");
                    return;
                }
                
                List<Producto> productos = new ArrayList<>();
                
                // Registrar productos del cliente
                for (int j = 1; j <= cantidadProd; j++) {
                    String nombreProducto = pedirTextoConCancelacion("Producto " + j, 
                            "Nombre del producto " + j + " de " + nombreCliente + ":");
                    
                    if (nombreProducto == null) {
                        appendLog("Simulación cancelada por el usuario.");
                        return;
                    }
                    
                    if (nombreProducto.isBlank()) {
                        nombreProducto = "Producto " + j;
                    }
                    
                    Double precio = pedirDoubleConCancelacion("Precio", 
                            "Precio unitario de " + nombreProducto + " (ej: 3500.0):");
                    
                    if (precio == null) {
                        appendLog("Simulación cancelada por el usuario.");
                        return;
                    }
                    
                    if (precio <= 0) precio = 1000.0;
                    
                    Integer cantidad = pedirEnteroConCancelacion("Cantidad", 
                            "Cantidad de " + nombreProducto + ":", 1);
                    
                    if (cantidad == null) {
                        appendLog("Simulación cancelada por el usuario.");
                        return;
                    }
                    
                    // Tiempo de procesamiento fijo (200ms por defecto)
                    long tiempoProc = 200;
                    
                    productos.add(new Producto(nombreProducto, precio, cantidad, tiempoProc));
                }
                
                clientes.add(new Cliente(nombreCliente, productos));
                appendLog("✓ Cliente " + nombreCliente + " registrado con " + cantidadProd + " productos.\n");
            }

            // Paso 4: Ejecutar simulación automáticamente
            appendLog("\n" + "=".repeat(60));
            appendLog("INICIANDO PROCESAMIENTO DE COMPRAS...");
            appendLog("=".repeat(60) + "\n");

            SimulacionService simulacion = new SimulacionService(this::appendLog);
            
            // Ejecutar en hilo aparte para no bloquear la UI
            new Thread(() -> {
                try {
                    List<RegistroCompra> resultados = simulacion.ejecutar(
                            new ArrayList<>(clientes),
                            numClientesSimultaneos, 
                            asignacionAleatoria
                    );
                    
                    appendLog("\n✓ Simulación completada exitosamente.");
                } catch (Exception ex) {
                    appendLog("Error en simulación: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }).start();

        } catch (Exception ex) {
            mostrarError("Error iniciar simulación", ex.getMessage());
        }
    }

    private String pedirTextoConCancelacion(String titulo, String mensaje) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle(titulo);
        dlg.setHeaderText(null);
        dlg.setContentText(mensaje);
        Optional<String> res = dlg.showAndWait();
        if (res.isEmpty()) {
            return null; // Usuario canceló
        }
        return res.get().trim();
    }

    private Integer pedirEnteroConCancelacion(String titulo, String mensaje, int defecto) {
        String t = pedirTextoConCancelacion(titulo, mensaje + " (defecto: " + defecto + ")");
        if (t == null) return null; // Usuario canceló
        if (t.isBlank()) return defecto;
        try { return Integer.parseInt(t); } catch (NumberFormatException ex) { return defecto; }
    }

    private Double pedirDoubleConCancelacion(String titulo, String mensaje) {
        String t = pedirTextoConCancelacion(titulo, mensaje);
        if (t == null) return null; // Usuario canceló
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
