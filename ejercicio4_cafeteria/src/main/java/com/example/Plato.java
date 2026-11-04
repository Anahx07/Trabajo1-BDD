package com.example;

import java.util.ArrayList;
import java.util.List;

public class Plato {
    private String id;
    private String nombre;
    private String categoria;
    private List<DetalleReceta> receta;

    public Plato(String id, String nombre, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.receta = new ArrayList<>();
    }
    public String getId() { 
        return id; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public String getCategoria() { 
        return categoria; 
    }
    public List<DetalleReceta> getReceta() { 
        return receta; 
    }
    public void agregarIngrediente(DetalleReceta detalle) {
        this.receta.add(detalle);
    }
    public double calcularCosto() {
        double costoTotal = 0.0;
        for (DetalleReceta dr : receta) {
            costoTotal += dr.calcularSubtotal();
        }
        return costoTotal;
    }
    public double calcularPrecioVenta() {
        return calcularCosto() * 2.0;
    }
    public double calcularMargenGanancia() {
        return calcularPrecioVenta() - calcularCosto();
    }
}
