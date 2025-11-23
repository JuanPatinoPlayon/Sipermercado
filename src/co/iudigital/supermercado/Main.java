package co.iudigital.supermercado;

import co.iudigital.supermercado.model.*;
import co.iudigital.supermercado.service.SimulacionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal para ejecutar la simulación desde consola
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   SIMULACIÓN DE COBRO - SUPERMERCADO LA ECONOMÍA        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        // Paso 1: Solicitar número de clientes simultáneos
        System.out.print("¿Cuántos clientes se procesarán simultáneamente? (default: 3): ");
        String input = scanner.nextLine().trim();
        int numClientesSimultaneos = input.isEmpty() ? 3 : Integer.parseInt(input);

        if (numClientesSimultaneos <= 0) {
            System.out.println("Error: Debe ingresar al menos 1 cliente. Saliendo...");
            scanner.close();
            return;
        }

        // Paso 2: Solicitar si usar asignación aleatoria
        System.out.print("¿Asignar cajeras aleatoriamente? (s/n, default: n): ");
        String asignacionInput = scanner.nextLine().trim().toLowerCase();
        boolean asignacionAleatoria = asignacionInput.equals("s") || asignacionInput.equals("si");

        System.out.println("\n--- REGISTRO DE CLIENTES QUE LLEGAN A LA TIENDA ---\n");

        // Paso 3: Registrar cada cliente que "llega"
        List<Cliente> clientes = new ArrayList<>();

        for (int i = 1; i <= numClientesSimultaneos; i++) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Cliente " + i + " de " + numClientesSimultaneos);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // Nombre del cliente
            System.out.print("Nombre del cliente " + i + ": ");
            String nombreCliente = scanner.nextLine().trim();
            if (nombreCliente.isEmpty()) {
                nombreCliente = "Cliente " + i;
            }

            // Cantidad de productos
            System.out.print("¿Cuántos productos comprará " + nombreCliente + "? ");
            String cantidadInput = scanner.nextLine().trim();
            int cantidadProductos = cantidadInput.isEmpty() ? 1 : Integer.parseInt(cantidadInput);

            List<Producto> productos = new ArrayList<>();

            // Registrar productos del cliente
            for (int j = 1; j <= cantidadProductos; j++) {
                System.out.println("\n  --- Producto " + j + " de " + cantidadProductos + " ---");
                
                System.out.print("  Nombre del producto: ");
                String nombreProducto = scanner.nextLine().trim();
                if (nombreProducto.isEmpty()) {
                    nombreProducto = "Producto " + j;
                }

                System.out.print("  Precio unitario: ");
                String precioInput = scanner.nextLine().trim();
                double precio = precioInput.isEmpty() ? 1000.0 : Double.parseDouble(precioInput);

                System.out.print("  Cantidad: ");
                String cantidadProdInput = scanner.nextLine().trim();
                int cantidad = cantidadProdInput.isEmpty() ? 1 : Integer.parseInt(cantidadProdInput);

                // Tiempo de procesamiento fijo (200ms por defecto)
                long tiempoProceso = 200;

                productos.add(new Producto(nombreProducto, precio, cantidad, tiempoProceso));
            }

            clientes.add(new Cliente(nombreCliente, productos));
            System.out.println("\n✓ Cliente " + nombreCliente + " registrado con " + cantidadProductos + " productos.\n");
        }

        // Paso 4: Ejecutar simulación
        System.out.println("\n" + "=".repeat(60));
        System.out.println("INICIANDO PROCESAMIENTO DE COMPRAS...");
        System.out.println("=".repeat(60) + "\n");

        SimulacionService simulacion = new SimulacionService(System.out::println);
        List<RegistroCompra> resultados = simulacion.ejecutar(clientes, numClientesSimultaneos, asignacionAleatoria);

        System.out.println("\n✓ Simulación completada exitosamente.");
        scanner.close();
    }
}

