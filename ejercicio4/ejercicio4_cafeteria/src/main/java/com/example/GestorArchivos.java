package com.example;

import java.io.*;

public class GestorArchivos {
    private static final String RUTA_DATOS = "datos" + File.separator;

    private static void asegurarCarpeta() {
        File carpeta = new File("datos");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
    public static void guardarDatos(GestorCafeteria gestor) {
        asegurarCarpeta();
        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_DATOS + "ingredientes.txt"))) {
            for (Ingrediente i : gestor.getIngredientes().values()) {
                pw.println(i.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error guardando ingredientes: " + e.getMessage());
        }

      
        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_DATOS + "bebidas.txt"))) {
            for (Bebida b : gestor.getBebidas().values()) {
                pw.println(b.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error guardando bebidas: " + e.getMessage());
        }
        try (PrintWriter pwPlatos = new PrintWriter(new FileWriter(RUTA_DATOS + "platos.txt"));
             PrintWriter pwRecetas = new PrintWriter(new FileWriter(RUTA_DATOS + "recetas.txt"))) {
            for (Plato p : gestor.getPlatos().values()) {
                pwPlatos.println(p.toCSV());
                for (DetalleReceta d : p.getReceta()) {
                    pwRecetas.println(d.toCSV(p.getId()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando platos y recetas: " + e.getMessage());
        }
    }

    public static void cargarDatos(GestorCafeteria gestor) {
        File fIng = new File(RUTA_DATOS + "ingredientes.txt");
        if (fIng.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fIng))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] datos = linea.split(";");
                    gestor.agregarIngrediente(new Ingrediente(
                            datos[0], datos[1], datos[2],
                            Double.parseDouble(datos[3].replace(",", ".")),
                            Double.parseDouble(datos[4].replace(",", ".")),
                            Double.parseDouble(datos[5].replace(",", "."))
                    ));
                }
            } catch (Exception e) {
                System.err.println("Error cargando ingredientes: " + e.getMessage());
            }
        }

        File fBeb = new File(RUTA_DATOS + "bebidas.txt");
        if (fBeb.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fBeb))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] datos = linea.split(";");
                    gestor.agregarBebida(new Bebida(
                            datos[0], datos[1],
                            Double.parseDouble(datos[2].replace(",", ".")),
                            Integer.parseInt(datos[3])
                    ));
                }
            } catch (Exception e) {
                System.err.println("Error cargando bebidas: " + e.getMessage());
            }
        }
    }
}
