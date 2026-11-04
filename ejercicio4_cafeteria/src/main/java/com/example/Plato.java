package com.example;

import java.util.ArrayList;
import java.util.List;

public class Plato {
    private String id;
    private String nombre;
    private List<DetalleReceta> receta;

    public Plato(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.receta = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public List<DetalleReceta> getReceta() { return receta; }

    public void agregarDetalle(DetalleReceta detalle) {
        receta.add(detalle);
    }
    public double calcularCosto() {
        double total = 0.0;
        for (DetalleReceta d : receta) {
            total += d.getSubtotalCosto();
        }
        return total;
    }
    public double calcularPrecioVenta() {
        return calcularCosto() * 2.0;
    }
    public double calcularMargen() {
        return calcularPrecioVenta() - calcularCosto();
    }
    public int calcularMaximoPreparable() {
        if (receta.isEmpty()) return 0;
        int maximo = Integer.MAX_VALUE;

        for (DetalleReceta d : receta) {
            if (d.getCantidad() <= 0) continue;
            int posiblesConEsteIngrediente = (int) (d.getIngrediente().getStockActual() / d.getCantidad());
            if (posiblesConEsteIngrediente < maximo) {
                maximo = posiblesConEsteIngrediente;
            }
        }
        return (maximo == Integer.MAX_VALUE) ? 0 : maximo;
    }

    public String toCSV() {
        return String.format("%s;%s", id, nombre);
    }
}