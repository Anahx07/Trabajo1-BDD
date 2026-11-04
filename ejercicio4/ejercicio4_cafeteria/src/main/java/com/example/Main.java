package com.example;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SISTEMA CAFETERIA UTN - INGENIERIA SOFTWARE   ");
        System.out.println("==================================================");

        GestorCafeteria gestor = new GestorCafeteria();

        Ingrediente cafe = new Ingrediente("ING01", "Café molido", "kg", 2.0, 0.5, 12.00);
        Ingrediente leche = new Ingrediente("ING02", "Leche entera", "l", 5.0, 2.0, 0.90);
        Ingrediente pan = new Ingrediente("ING03", "Pan baguette", "kg", 1.5, 0.5, 2.50);
        Ingrediente queso = new Ingrediente("ING04", "Queso mozzarella", "kg", 0.4, 0.6, 5.00); // Bajo stock mínimo

        gestor.agregarIngrediente(cafe);
        gestor.agregarIngrediente(leche);
        gestor.agregarIngrediente(pan);
        gestor.agregarIngrediente(queso);

        Plato capuchino = new Plato("PLT01", "Capuchino Especial");
        capuchino.agregarDetalle(new DetalleReceta(cafe, 0.020)); 
        capuchino.agregarDetalle(new DetalleReceta(leche, 0.150)); 
        gestor.agregarPlato(capuchino);

        Plato sandwich = new Plato("PLT02", "Sandwich Mixto");
        sandwich.agregarDetalle(new DetalleReceta(pan, 0.100));
        sandwich.agregarDetalle(new DetalleReceta(queso, 0.080));
        gestor.agregarPlato(sandwich);

        Bebida agua = new Bebida("BEB01", "Agua Mineral 500ml", 0.50, 20);
        Bebida cola = new Bebida("BEB02", "Gaseosa Personal", 0.80, 15);
        gestor.agregarBebida(agua);
        gestor.agregarBebida(cola);
        gestor.reporteCostosYMargenes();
        gestor.reportePlatosPreparables();
        gestor.reporteListaCompras();

        System.out.println("\n--- PRUEBA 1: Intentar vender 20 sandwiches (queso insuficiente) ---");
        Map<String, Integer> ventaExcesiva = new HashMap<>();
        ventaExcesiva.put("PLT02", 20);
        gestor.realizarVenta(ventaExcesiva, new HashMap<>());

        System.out.println("\n--- PRUEBA 2: Venta válida de 2 capuchinos y 1 agua ---");
        Map<String, Integer> ventaValidaPlatos = new HashMap<>();
        ventaValidaPlatos.put("PLT01", 2);
        Map<String, Integer> ventaValidaBebidas = new HashMap<>();
        ventaValidaBebidas.put("BEB01", 1);
        gestor.realizarVenta(ventaValidaPlatos, ventaValidaBebidas);

        System.out.println("\n--- PRUEBA 3: Intento de eliminación de ingrediente con receta activa ---");
        gestor.eliminarIngrediente("ING01");

        gestor.reporteMasVendidos();
        gestor.auditoriaStockIngredientes();

        GestorArchivos.guardarDatos(gestor);
        System.out.println("\n[SISTEMA] Datos persistidos con exito en archivos de texto.");
    }
}
