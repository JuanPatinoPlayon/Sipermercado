# Supermercado - Simulación de Cobro con Concurrencia

## Descripción

Aplicación Java que simula el proceso de cobro en un supermercado utilizando conceptos de concurrencia (hilos/threads). El sistema permite procesar múltiples clientes simultáneamente con diferentes cajeras, calculando tiempos de procesamiento y totales de compra.

## Características

- ✅ Múltiples cajeras y clientes simultáneos
- ✅ Simulación del proceso de cobro con tiempos reales
- ✅ Cálculo de tiempo necesario para cada compra
- ✅ Muestra tiempo total de cobro para todas las compras
- ✅ Detalle de productos comprados con costo y tiempo de procesamiento
- ✅ Asignación de cajeras (round-robin o aleatoria)
- ✅ Interfaz gráfica (JavaFX) y modo consola

## Requisitos

- Java 21 o superior
- Maven 3.6 o superior
- JavaFX 22 (incluido en las dependencias)

## Estructura del Proyecto

```
src/
├── co/iudigital/supermercado/
│   ├── App.java                    # Aplicación principal JavaFX
│   ├── Main.java                   # Aplicación principal consola
│   ├── controllers/                # Controladores JavaFX
│   │   └── InicioController.java
│   ├── model/                      # Modelos de datos
│   │   ├── Cajera.java
│   │   ├── Cliente.java
│   │   ├── Producto.java
│   │   ├── Compra.java
│   │   ├── RegistroCompra.java
│   │   └── DetalleProcesoProducto.java
│   ├── service/                    # Servicios de negocio
│   │   └── SimulacionService.java
│   └── threads/                    # Hilos de ejecución
│       └── HiloCajera.java
```

## Compilación

```bash
mvn clean compile
```

## Ejecución

### Modo Consola

Para ejecutar la aplicación en modo consola (recomendado para pruebas):

```bash
mvn exec:java -Dexec.mainClass="co.iudigital.supermercado.Main"
```

O directamente:

```bash
java -cp target/classes co.iudigital.supermercado.Main
```

### Modo Interfaz Gráfica (JavaFX)

```bash
mvn javafx:run
```

## Uso

### Modo Consola

1. Al iniciar, se solicita:
   - Número de clientes a procesar simultáneamente (default: 3)
   - Tipo de asignación de cajeras (aleatoria o round-robin)

2. Para cada cliente:
   - Nombre del cliente
   - Cantidad de productos a comprar
   - Para cada producto:
     - Nombre
     - Precio unitario
     - Cantidad
     - Tiempo de procesamiento por unidad (ms)

3. La simulación se ejecuta automáticamente y muestra:
   - Progreso de cada compra en tiempo real
   - Detalle de cada producto procesado
   - Tiempo de procesamiento de cada producto
   - Total de cada compra
   - Resumen final con totales generales

### Modo Interfaz Gráfica

1. Usar el botón "Registrar Cliente" para agregar clientes y sus productos
2. Usar el botón "Iniciar" para comenzar la simulación
3. Los resultados se muestran en el área de texto

## Conceptos de Concurrencia Implementados

- **Threads (Hilos)**: Cada cajera procesa un cliente en un hilo separado
- **ExecutorService**: Pool de hilos para gestionar múltiples cajeras simultáneamente
- **Sincronización**: Uso de `Future` para esperar la finalización de todos los hilos
- **Tiempo de procesamiento**: Simulación realista con `Thread.sleep()` basado en el tiempo de procesamiento de cada producto

## Cajeras Disponibles

El sistema incluye 5 cajeras predefinidas:
1. María (ID: 1)
2. Ana (ID: 2)
3. Laura (ID: 3)
4. Carmen (ID: 4)
5. Sofía (ID: 5)

## Ejemplo de Salida

```
========== INICIANDO SIMULACIÓN ==========
Total de clientes: 3
Cajeras disponibles: 5
Cajeras simultáneas: 3
Asignación: Round-Robin
==========================================

[María] Iniciando procesamiento de compra para cliente: Juan
[María] Procesando producto: Leche (x2) para cliente: Juan
[María] Producto procesado: Leche | Cantidad: 2 | Precio unitario: 3500.00 | Subtotal: 7000.00 | Tiempo: 400 ms
...

========== RESUMEN FINAL DE COMPRAS ==========

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Cliente: Juan
Cajera: María (ID: 1)
Productos:
  • Leche
    - Cantidad: 2 unidades
    - Precio unitario: $3500.00
    - Subtotal: $7000.00
    - Tiempo de procesamiento: 400 ms
Total de la compra: $7000.00
Tiempo total de la compra: 400 ms

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TOTAL GENERAL (todas las compras): $15000.00
Tiempo acumulado (suma de todos los tiempos): 1200 ms
Tiempo real de simulación (wall-clock time): 450 ms
==================================================
```

## Notas Técnicas

- El tiempo de procesamiento de un producto se calcula como: `tiempoPorUnidad * cantidad`
- El tiempo total de una compra es la suma de los tiempos de procesamiento de todos sus productos
- El tiempo real de simulación (wall-clock time) puede ser menor que la suma de tiempos individuales debido a la ejecución paralela
- Los resultados se ordenan alfabéticamente por nombre de cliente

## Autor

Proyecto desarrollado para demostrar conceptos de concurrencia en Java.

## Licencia

Este proyecto es de uso educativo.
