package com.example;

public class DetalleReceta {
    private Ingrediente ingrediente;
    private double cantidad;

    public DetalleReceta(Ingrediente ingrediente, double cantidad) {
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
    }

    public Ingrediente getIngrediente() { return ingrediente; }
    public double getCantidad() { return cantidad; }

    public double getSubtotalCosto() {
        return ingrediente.getCostoUnitario() * cantidad;
    }

    public String toCSV(String platoId) {
        return String.format("%s;%s;%.4f", platoId, ingrediente.getId(), cantidad);
    }
}