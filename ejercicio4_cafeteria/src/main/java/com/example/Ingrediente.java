package com.example;

public class Ingrediente {
    private String id;
    private String nombre;
    private String unidadMedida;
    private double stockActual;
    private double stockMinimo;
    private double costoUnitario;

    public Ingrediente(String id, String nombre, String unidadMedida, double stockActual, double stockMinimo, double costoUnitario) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.costoUnitario = costoUnitario;
    }

    public String getId() { 
        return id; 
    }

    public String getNombre() { 
        return nombre; 
    }

    public String getUnidadMedida() { 
        return unidadMedida; 
    }

    public double getStockActual() { 
        return stockActual; 
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public double getStockMinimo() { 
        return stockMinimo; 
    }

    public double getCostoUnitario() { 
        return costoUnitario; 
    }
}