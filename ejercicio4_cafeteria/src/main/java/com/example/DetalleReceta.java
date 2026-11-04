package com.example;

public class DetalleReceta {
    private Ingrediente ingrediente;
    private double cantidadNecesaria;

    public DetalleReceta(Ingrediente ingrediente, double cantidadNecesaria) {
        this.ingrediente = ingrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public Ingrediente getIngrediente() { 
        return ingrediente; 
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public double getCantidadNecesaria() { 
        return cantidadNecesaria; 
    }

    public void setCantidadNecesaria(double cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    // Calcula el costo de este ingrediente en la receta
    public double calcularSubtotal() {
        return ingrediente.getCostoUnitario() * cantidadNecesaria;
    }
}