package com.example;

import java.util.*;

public class GestorCafeteria {
    private Map<String, Ingrediente> ingredientes = new LinkedHashMap<>();
    private Map<String, Plato> platos = new LinkedHashMap<>();
    private Map<String, Bebida> bebidas = new LinkedHashMap<>();
    private Map<String, Integer> contadorVentasPlatos = new HashMap<>();
    private Map<String, Integer> contadorVentasBebidas = new HashMap<>();

    public void agregarIngrediente(Ingrediente ing) {
        ingredientes.put(ing.getId(), ing);
    }

    public void agregarPlato(Plato plato) {
        platos.put(plato.getId(), plato);
    }

    public void agregarBebida(Bebida bebida) {
        bebidas.put(bebida.getId(), bebida);
    }

    public Map<String, Ingrediente> getIngredientes() { return ingredientes; }
    public Map<String, Plato> getPlatos() { return platos; }
    public Map<String, Bebida> getBebidas() { return bebidas; }

    public boolean eliminarIngrediente(String idIngrediente) {
        for (Plato p : platos.values()) {
            for (DetalleReceta d : p.getReceta()) {
                if (d.getIngrediente().getId().equalsIgnoreCase(idIngrediente)) {
                    System.out.println("[RECHAZADO] No se puede eliminar: el ingrediente forma parte de la receta de " + p.getNombre());
                    return false;
                }
            }
        }
        return ingredientes.remove(idIngrediente) != null;
    }

    public boolean realizarVenta(Map<String, Integer> platosAVender, Map<String, Integer> bebidasAVender) {
        Map<Ingrediente, Double> consumoTotalIngredientes = new HashMap<>();

        for (Map.Entry<String, Integer> entry : platosAVender.entrySet()) {
            Plato p = platos.get(entry.getKey());
            int cantidad = entry.getValue();
            if (p == null) {
                System.out.println("[VENTA RECHAZADA] El plato no existe.");
                return false;
            }
            for (DetalleReceta d : p.getReceta()) {
                double requerido = d.getCantidad() * cantidad;
                consumoTotalIngredientes.put(d.getIngrediente(),
                        consumoTotalIngredientes.getOrDefault(d.getIngrediente(), 0.0) + requerido);
            }
        }

        for (Map.Entry<Ingrediente, Double> entry : consumoTotalIngredientes.entrySet()) {
            if (entry.getKey().getStockActual() < entry.getValue()) {
                System.out.printf("[VENTA RECHAZADA] Stock insuficiente para el ingrediente: %s (Disponible: %.2f, Requerido: %.2f)%n",
                        entry.getKey().getNombre(), entry.getKey().getStockActual(), entry.getValue());
                return false;
            }
        }

        for (Map.Entry<String, Integer> entry : bebidasAVender.entrySet()) {
            Bebida b = bebidas.get(entry.getKey());
            int cantidad = entry.getValue();
            if (b == null || b.getStock() < cantidad) {
                System.out.printf("[VENTA RECHAZADA] Stock insuficiente para la bebida: %s%n",
                        (b != null ? b.getNombre() : "Desconocida"));
                return false;
            }
        }

        for (Map.Entry<Ingrediente, Double> entry : consumoTotalIngredientes.entrySet()) {
            entry.getKey().descontar(entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : platosAVender.entrySet()) {
            contadorVentasPlatos.put(entry.getKey(),
                    contadorVentasPlatos.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : bebidasAVender.entrySet()) {
            Bebida b = bebidas.get(entry.getKey());
            b.descontar(entry.getValue());
            contadorVentasBebidas.put(entry.getKey(),
                    contadorVentasBebidas.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }

        System.out.println("[EXITO] Venta procesada y stock actualizado correctamente.");
        return true;
    }

    public void reporteCostosYMargenes() {
        System.out.println("\n=== REPORTE: COSTOS, PRECIOS Y MARGENES ===");
        System.out.printf("%-20s %-10s %-12s %-10s%n", "Plato", "Costo", "P. Venta", "Margen");
        for (Plato p : platos.values()) {
            System.out.printf("%-20s $%-9.2f $%-11.2f $%-9.2f%n",
                    p.getNombre(), p.calcularCosto(), p.calcularPrecioVenta(), p.calcularMargen());
        }
    }

    public void reporteListaCompras() {
        System.out.println("\n=== REPORTE: INGREDIENTES BAJO STOCK MINIMO (LISTA DE COMPRAS) ===");
        for (Ingrediente i : ingredientes.values()) {
            if (i.necesitaReposicion()) {
                System.out.printf("- %s: Stock actual %.2f %s | Minimo: %.2f %s | Faltante sugerido: %.2f %s%n",
                        i.getNombre(), i.getStockActual(), i.getUnidadMedida(),
                        i.getStockMinimo(), i.getUnidadMedida(),
                        (i.getStockMinimo() - i.getStockActual()), i.getUnidadMedida());
            }
        }
    }

    public void reportePlatosPreparables() {
        System.out.println("\n=== REPORTE: PLATOS PREPARABLES CON STOCK ACTUAL ===");
        for (Plato p : platos.values()) {
            System.out.printf("- %s: %d unidades disponibles para preparar%n",
                    p.getNombre(), p.calcularMaximoPreparable());
        }
    }

    public void reporteMasVendidos() {
        System.out.println("\n=== REPORTE: PLATOS Y BEBIDAS MAS VENDIDOS ===");
        for (Map.Entry<String, Integer> e : contadorVentasPlatos.entrySet()) {
            Plato p = platos.get(e.getKey());
            System.out.println("Plato: " + (p != null ? p.getNombre() : e.getKey()) + " -> " + e.getValue() + " ventas");
        }
        for (Map.Entry<String, Integer> e : contadorVentasBebidas.entrySet()) {
            Bebida b = bebidas.get(e.getKey());
            System.out.println("Bebida: " + (b != null ? b.getNombre() : e.getKey()) + " -> " + e.getValue() + " ventas");
        }
    }

    public void auditoriaStockIngredientes() {
        System.out.println("\n=== AUDITORIA DE STOCK DE INGREDIENTES ===");
        System.out.printf("%-6s %-18s %-12s %-12s %-8s%n", "ID", "Nombre", "Stock Actual", "Stock Min.", "Unidad");
        for (Ingrediente i : ingredientes.values()) {
            System.out.printf("%-6s %-18s %-12.2f %-12.2f %-8s%n",
                    i.getId(), i.getNombre(), i.getStockActual(), i.getStockMinimo(), i.getUnidadMedida());
        }
    }
}
