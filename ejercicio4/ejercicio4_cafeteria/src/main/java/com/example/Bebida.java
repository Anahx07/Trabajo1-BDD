package com.example;
    
    public class Bebida {
        private String id;
        private String nombre;
        private double precioBase;
        private int stock; 
    
        public Bebida(String id, String nombre, double precioBase, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precioBase = precioBase;
            this.stock = stock;
        }
    
        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public double getPrecioBase() { return precioBase; }
        public int getStock() { return stock; }
    
        public double getPrecioFinalConIVA() {
            return this.precioBase * 1.15; 
        }
    
        public void abastecer(int cantidad) {
            if (cantidad > 0) {
                this.stock += cantidad;
            }
        }
    
        public void descontar(int cantidad) {
            this.stock -= cantidad;
        }
    
        public String toCSV() {
            return String.format("%s;%s;%.2f;%d", id, nombre, precioBase, stock);
        }
    }