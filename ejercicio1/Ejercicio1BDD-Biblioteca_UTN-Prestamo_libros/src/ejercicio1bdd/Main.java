package ejercicio1bdd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final List<Libros> listaLibros = new ArrayList<>();
    private static final List<Revistas> listaRevistas = new ArrayList<>();
    private static final List<Usuario> listaUsuarios = new ArrayList<>();
    private static final List<Ejemplar> listaEjemplares = new ArrayList<>();
    private static final List<Prestamo> listaPrestamos = new ArrayList<>();

    private static final String ARCHIVO_LIBROS = "libros.txt";
    private static final String ARCHIVO_REVISTAS = "revistas.txt";
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_EJEMPLARES = "ejemplares.txt";
    private static final String ARCHIVO_PRESTAMOS = "prestamos.txt";

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SISTEMA DE BIBLIOTECA UTN - GESTION DE PRESTAMOS");
        System.out.println("==================================================");

        cargarDatosIniciales();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    menuGestionMateriales();
                    break;
                case 2:
                    menuGestionUsuarios();
                    break;
                case 3:
                    registrarNuevoPrestamo();
                    break;
                case 4:
                    registrarDevolucion();
                    break;
                case 5:
                    menuReportes();
                    break;
                case 6:
                    guardarDatosEnArchivos();
                    break;
                case 0:
                    guardarDatosEnArchivos();
                    System.out.println("Saliendo del sistema...");
                    salir = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Gestion de Materiales");
        System.out.println("2. Gestion de Usuarios");
        System.out.println("3. Registrar Prestamo");
        System.out.println("4. Registrar Devolucion");
        System.out.println("5. Reportes");
        System.out.println("6. Guardar Datos");
        System.out.println("0. Salir");
    }

    private static void menuGestionMateriales() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- GESTION DE MATERIALES ---");
            System.out.println("1. Registrar Libro");
            System.out.println("2. Registrar Revista");
            System.out.println("3. Agregar Ejemplares");
            System.out.println("4. Listar Catalogo");
            System.out.println("0. Volver");

            int op = leerEntero("Seleccione una opcion: ");
            switch (op) {
                case 1:
                    registrarLibro();
                    break;
                case 2:
                    registrarRevista();
                    break;
                case 3:
                    agregarEjemplares();
                    break;
                case 4:
                    listarCatalogo();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static void registrarLibro() {
        System.out.println("\n-- REGISTRAR NUEVO LIBRO --");
        String id = leerTexto("Ingrese ID del libro (ej. LIB-001): ");
        if (buscarLibro(id) != null) {
            System.out.println("Error: Ya existe un libro con ese ID.");
            return;
        }
        String titulo = leerTexto("Ingrese Titulo: ");
        String autor = leerTexto("Ingrese Autor: ");
        String editorial = leerTexto("Ingrese Editorial: ");
        int anio = leerEntero("Ingrese Anio de publicacion: ");
        int ejemplares = leerEntero("Ingrese Cantidad de ejemplares iniciales: ");

        Libros nuevoLibro = new Libros(id, titulo, autor, editorial, anio, ejemplares);
        listaLibros.add(nuevoLibro);

        for (int i = 1; i <= ejemplares; i++) {
            String codEjemplar = id + "-EJ" + i;
            listaEjemplares.add(new Ejemplar(codEjemplar, id, "LIBRO", titulo, "Estanteria A"));
        }

        System.out.println("Libro registrado exitosamente con " + ejemplares + " ejemplares fisicos.");
    }

    private static void registrarRevista() {
        System.out.println("\n-- REGISTRAR NUEVA REVISTA --");
        String id = leerTexto("Ingrese ID de la revista (ej. REV-001): ");
        if (buscarRevista(id) != null) {
            System.out.println("Error: Ya existe una revista con ese ID.");
            return;
        }
        String titulo = leerTexto("Ingrese Titulo: ");
        int edicion = leerEntero("Ingrese Numero de edicion: ");
        String issn = leerTexto("Ingrese ISSN: ");
        int anio = leerEntero("Ingrese Anio de publicacion: ");
        int ejemplares = leerEntero("Ingrese Cantidad de ejemplares iniciales: ");

        Revistas nuevaRevista = new Revistas(id, titulo, edicion, issn, anio, ejemplares);
        listaRevistas.add(nuevaRevista);

        for (int i = 1; i <= ejemplares; i++) {
            String codEjemplar = id + "-EJ" + i;
            listaEjemplares.add(new Ejemplar(codEjemplar, id, "REVISTA", titulo, "Hemeroteca B"));
        }

        System.out.println("Revista registrada exitosamente con " + ejemplares + " ejemplares fisicos.");
    }

    private static void agregarEjemplares() {
        System.out.println("\n-- AGREGAR EJEMPLARES --");
        System.out.println("1. Agregar a Libro");
        System.out.println("2. Agregar a Revista");
        int tipo = leerEntero("Seleccione tipo: ");

        if (tipo == 1) {
            String id = leerTexto("Ingrese ID del libro: ");
            Libros lib = buscarLibro(id);
            if (lib == null) {
                System.out.println("Libro no encontrado.");
                return;
            }
            int cant = leerEntero("Cantidad de ejemplares a sumar: ");
            int totalPrevio = lib.getEjemplaresTotales();
            lib.agregarEjemplares(cant);
            for (int i = 1; i <= cant; i++) {
                String cod = id + "-EJ" + (totalPrevio + i);
                listaEjemplares.add(new Ejemplar(cod, id, "LIBRO", lib.getTitulo(), "Estanteria A"));
            }
            System.out.println("Ejemplares agregados. Nuevo stock disponible: " + lib.getEjemplaresDisponibles() + "/" + lib.getEjemplaresTotales());
        } else if (tipo == 2) {
            String id = leerTexto("Ingrese ID de la revista: ");
            Revistas rev = buscarRevista(id);
            if (rev == null) {
                System.out.println("Revista no encontrada.");
                return;
            }
            int cant = leerEntero("Cantidad de ejemplares a sumar: ");
            int totalPrevio = rev.getEjemplaresTotales();
            rev.agregarEjemplares(cant);
            for (int i = 1; i <= cant; i++) {
                String cod = id + "-EJ" + (totalPrevio + i);
                listaEjemplares.add(new Ejemplar(cod, id, "REVISTA", rev.getTitulo(), "Hemeroteca B"));
            }
            System.out.println("Ejemplares agregados. Nuevo stock disponible: " + rev.getEjemplaresDisponibles() + "/" + rev.getEjemplaresTotales());
        } else {
            System.out.println("Tipo no valido.");
        }
    }

    private static void listarCatalogo() {
        System.out.println("\n=== CATALOGO DE LIBROS ===");
        if (listaLibros.isEmpty()) {
            System.out.println("(No hay libros registrados)");
        } else {
            for (Libros l : listaLibros) {
                System.out.println(l);
            }
        }

        System.out.println("\n=== CATALOGO DE REVISTAS ===");
        if (listaRevistas.isEmpty()) {
            System.out.println("(No hay revistas registradas)");
        } else {
            for (Revistas r : listaRevistas) {
                System.out.println(r);
            }
        }
    }

    private static void menuGestionUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- GESTION DE USUARIOS ---");
            System.out.println("1. Registrar Usuario");
            System.out.println("2. Consultar Usuario");
            System.out.println("3. Cobrar Multa");
            System.out.println("4. Listar Usuarios");
            System.out.println("0. Volver");

            int op = leerEntero("Seleccione una opcion: ");
            switch (op) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    consultarUsuario();
                    break;
                case 3:
                    cobrarMulta();
                    break;
                case 4:
                    listarUsuarios();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static void registrarUsuario() {
        System.out.println("\n-- REGISTRAR USUARIO --");
        String id = leerTexto("Ingrese Cedula / ID Usuario: ");
        if (buscarUsuario(id) != null) {
            System.out.println("Error: Ya existe un usuario con esa identificacion.");
            return;
        }
        String nombre = leerTexto("Ingrese Nombre completo: ");
        String correo = leerTexto("Ingrese Correo electronico: ");
        String carrera = leerTexto("Ingrese Carrera o Facultad: ");

        Usuario nuevo = new Usuario(id, nombre, correo, carrera);
        listaUsuarios.add(nuevo);
        System.out.println("Usuario registrado exitosamente.");
    }

    private static void consultarUsuario() {
        String id = leerTexto("Ingrese Cedula / ID del usuario a consultar: ");
        Usuario u = buscarUsuario(id);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        System.out.println("\nDetalle del Usuario:");
        System.out.println(u);
    }

    private static void cobrarMulta() {
        String id = leerTexto("Ingrese Cedula / ID del usuario: ");
        Usuario u = buscarUsuario(id);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        if (u.getMultaPendiente() <= 0.001) {
            System.out.println("El usuario no tiene multas pendientes ($0.00).");
            return;
        }
        System.out.println("Multa pendiente actual: $" + String.format("%.2f", u.getMultaPendiente()));
        double pago = leerDecimal("Ingrese monto a pagar: ");
        u.pagarMulta(pago);
        System.out.println("Pago registrado. Saldo pendiente restante: $" + String.format("%.2f", u.getMultaPendiente()));
        if (u.puedePrestar()) {
            System.out.println("El usuario ahora se encuentra HABILITADO para solicitar prestamos.");
        }
    }

    private static void listarUsuarios() {
        System.out.println("\n=== LISTA DE USUARIOS REGISTRADOS ===");
        if (listaUsuarios.isEmpty()) {
            System.out.println("(No hay usuarios registrados)");
        } else {
            for (Usuario u : listaUsuarios) {
                System.out.println(u);
            }
        }
    }

    private static void registrarNuevoPrestamo() {
        System.out.println("\n--- REGISTRO DE PRESTAMO ---");
        String idUser = leerTexto("Ingrese ID / Cedula del usuario solicitante: ");
        Usuario user = buscarUsuario(idUser);
        if (user == null) {
            System.out.println("Error: El usuario no existe.");
            return;
        }

        if (!user.puedePrestar()) {
            System.out.println("Error: Prestamo no permitido. Motivo: " + user.getMotivoBloqueo());
            return;
        }

        System.out.println("Que material desea prestar?");
        System.out.println("1. Libro (7 dias de plazo - $0.25 multa/dia)");
        System.out.println("2. Revista (3 dias de plazo - $0.50 multa/dia)");
        int tipo = leerEntero("Seleccione opcion: ");

        String idMat;
        String tituloMat;
        String tipoMatStr;
        Ejemplar ejemplarElegido = null;

        if (tipo == 1) {
            idMat = leerTexto("Ingrese ID del libro: ");
            Libros lib = buscarLibro(idMat);
            if (lib == null) {
                System.out.println("Error: Libro no encontrado.");
                return;
            }
            if (!lib.hayDisponibles()) {
                System.out.println("Error: No hay ejemplares disponibles de este libro (Stock en 0).");
                return;
            }
            for (Ejemplar ej : listaEjemplares) {
                if (ej.getIdMaterial().equalsIgnoreCase(idMat) && ej.isDisponible()) {
                    ejemplarElegido = ej;
                    break;
                }
            }
            if (ejemplarElegido == null) {
                System.out.println("Error: No se encontro un ejemplar fisico disponible.");
                return;
            }

            lib.prestarEjemplar();
            tituloMat = lib.getTitulo();
            tipoMatStr = "LIBRO";

        } else if (tipo == 2) {
            idMat = leerTexto("Ingrese ID de la revista: ");
            Revistas rev = buscarRevista(idMat);
            if (rev == null) {
                System.out.println("Error: Revista no encontrada.");
                return;
            }
            if (!rev.hayDisponibles()) {
                System.out.println("Error: No hay ejemplares disponibles de esta revista (Stock en 0).");
                return;
            }
            for (Ejemplar ej : listaEjemplares) {
                if (ej.getIdMaterial().equalsIgnoreCase(idMat) && ej.isDisponible()) {
                    ejemplarElegido = ej;
                    break;
                }
            }
            if (ejemplarElegido == null) {
                System.out.println("Error: No se encontro un ejemplar fisico disponible.");
                return;
            }

            rev.prestarEjemplar();
            tituloMat = rev.getTitulo();
            tipoMatStr = "REVISTA";

        } else {
            System.out.println("Tipo no valido.");
            return;
        }

        ejemplarElegido.prestar();
        user.incrementarPrestamos();

        String idPrestamo = "P-" + (listaPrestamos.size() + 1);
        LocalDate fechaHoy = LocalDate.now();
        Prestamo nuevoPrestamo = new Prestamo(idPrestamo, idUser, ejemplarElegido.getCodigoEjemplar(), idMat, tituloMat, tipoMatStr, fechaHoy);
        listaPrestamos.add(nuevoPrestamo);

        System.out.println("\nPrestamo registrado con exito!");
        System.out.println("ID Prestamo: " + idPrestamo);
        System.out.println("Ejemplar asignado: " + ejemplarElegido.getCodigoEjemplar());
        System.out.println("Fecha prestamo: " + fechaHoy);
        System.out.println("Fecha limite de devolucion: " + nuevoPrestamo.getFechaLimite());
        System.out.println("Prestamos activos del usuario: " + user.getPrestamosActivos() + "/3");
    }

    private static void registrarDevolucion() {
        System.out.println("\n--- REGISTRO DE DEVOLUCION ---");
        String idPrestamo = leerTexto("Ingrese ID del prestamo a devolver (ej. P-1): ");
        Prestamo prestamo = buscarPrestamo(idPrestamo);
        if (prestamo == null) {
            System.out.println("Error: Prestamo no encontrado.");
            return;
        }

        if (prestamo.isDevuelto()) {
            System.out.println("Error: Este prestamo ya fue devuelto previamente. No se puede devolver dos veces.");
            return;
        }

        System.out.println("Prestamo actual: " + prestamo);
        System.out.println("Fecha limite acordada: " + prestamo.getFechaLimite());
        System.out.println("\nSeleccione fecha de devolucion:");
        System.out.println("1. Devolver hoy (" + LocalDate.now() + ")");
        System.out.println("2. Simular fecha de devolucion con retraso (para probar multas)");
        int opcionFecha = leerEntero("Opcion: ");

        LocalDate fechaDev;
        if (opcionFecha == 2) {
            int diasExtra = leerEntero("Cuantos dias despues de la fecha limite desea simular la entrega?: ");
            fechaDev = prestamo.getFechaLimite().plusDays(diasExtra);
        } else {
            fechaDev = LocalDate.now();
        }

        prestamo.registrarDevolucion(fechaDev);

        Ejemplar ej = buscarEjemplar(prestamo.getCodigoEjemplar());
        if (ej != null) {
            ej.devolver();
        }

        if (prestamo.getTipoMaterial().equalsIgnoreCase("LIBRO")) {
            Libros lib = buscarLibro(prestamo.getIdMaterial());
            if (lib != null) {
                lib.devolverEjemplar();
            }
        } else {
            Revistas rev = buscarRevista(prestamo.getIdMaterial());
            if (rev != null) {
                rev.devolverEjemplar();
            }
        }

        Usuario user = buscarUsuario(prestamo.getIdUsuario());
        if (user != null) {
            user.decrementarPrestamos();
            if (prestamo.getMultaGenerada() > 0) {
                user.agregarMulta(prestamo.getMultaGenerada());
            }
        }

        System.out.println("\nDevolucion completada exitosamente.");
        System.out.println("Fecha de entrega: " + fechaDev);
        if (prestamo.getDiasRetraso() > 0) {
            System.out.println("ALERTA: Retraso de " + prestamo.getDiasRetraso() + " dias.");
            System.out.println("Multa generada: $" + String.format("%.2f", prestamo.getMultaGenerada()));
            System.out.println("La multa fue cargada al usuario. Nueva deuda del usuario: $" + String.format("%.2f", user != null ? user.getMultaPendiente() : 0.0));
        } else {
            System.out.println("Entrega a tiempo. Sin multas aplicadas.");
        }
    }

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- REPORTES ---");
            System.out.println("1. Prestamos vencidos a la fecha");
            System.out.println("2. Historial de prestamos de un usuario");
            System.out.println("3. Materiales mas prestados");
            System.out.println("4. Total de multas generadas en un periodo");
            System.out.println("5. Auditoria de ejemplares disponibles");
            System.out.println("0. Volver");

            int op = leerEntero("Seleccione una opcion: ");
            switch (op) {
                case 1:
                    reportePrestamosVencidos();
                    break;
                case 2:
                    reporteHistorialUsuario();
                    break;
                case 3:
                    reporteMaterialesMasPrestados();
                    break;
                case 4:
                    reporteTotalMultas();
                    break;
                case 5:
                    reporteAuditoriaEjemplares();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static void reportePrestamosVencidos() {
        System.out.println("\n=== REPORTE: PRESTAMOS VENCIDOS A LA FECHA ===");
        LocalDate hoy = LocalDate.now();
        int cont = 0;
        for (Prestamo p : listaPrestamos) {
            if (p.isVencido(hoy)) {
                System.out.println(p + " | Dias vencido: " + p.calcularDiasRetraso(hoy));
                cont++;
            }
        }
        if (cont == 0) {
            System.out.println("No hay prestamos vencidos a la fecha actual (" + hoy + ").");
        }
    }

    private static void reporteHistorialUsuario() {
        System.out.println("\n=== REPORTE: HISTORIAL DE UN USUARIO ===");
        String idUser = leerTexto("Ingrese Cedula / ID del usuario: ");
        Usuario u = buscarUsuario(idUser);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        System.out.println("Historial de: " + u.getNombre() + " (ID: " + u.getIdUsuario() + ")");
        int cont = 0;
        for (Prestamo p : listaPrestamos) {
            if (p.getIdUsuario().equalsIgnoreCase(idUser)) {
                System.out.println(p);
                cont++;
            }
        }
        if (cont == 0) {
            System.out.println("El usuario no registra prestamos en su historial.");
        }
    }

    private static void reporteMaterialesMasPrestados() {
        System.out.println("\n=== REPORTE: MATERIALES MAS PRESTADOS ===");
        if (listaPrestamos.isEmpty()) {
            System.out.println("No hay registros de prestamos todavia.");
            return;
        }
        Map<String, Integer> conteo = new HashMap<>();
        for (Prestamo p : listaPrestamos) {
            String clave = p.getTipoMaterial() + ": " + p.getTituloMaterial();
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            System.out.println("- " + entry.getKey() + " -> " + entry.getValue() + " prestamo(s)");
        }
    }

    private static void reporteTotalMultas() {
        System.out.println("\n=== REPORTE: TOTAL DE MULTAS GENERADAS ===");
        double totalMultas = 0.0;
        int prestamosConMulta = 0;
        for (Prestamo p : listaPrestamos) {
            if (p.getMultaGenerada() > 0) {
                totalMultas += p.getMultaGenerada();
                prestamosConMulta++;
                System.out.println("- Prestamo " + p.getIdPrestamo() + " (" + p.getTituloMaterial() + "): $" + String.format("%.2f", p.getMultaGenerada()));
            }
        }
        System.out.println("Total acumulado de multas generadas: $" + String.format("%.2f", totalMultas));
        System.out.println("Cantidad de devoluciones con mora: " + prestamosConMulta);
    }

    private static void reporteAuditoriaEjemplares() {
        System.out.println("\n=== AUDITORIA DE EJEMPLARES DISPONIBLES ===");
        if (listaEjemplares.isEmpty()) {
            System.out.println("No hay ejemplares registrados.");
            return;
        }
        int disponibles = 0;
        int prestados = 0;
        for (Ejemplar ej : listaEjemplares) {
            System.out.println(ej);
            if (ej.isDisponible()) {
                disponibles++;
            } else {
                prestados++;
            }
        }
        System.out.println("\nResumen de Auditoria:");
        System.out.println("Total de ejemplares fisicos: " + listaEjemplares.size());
        System.out.println("Ejemplares Disponibles: " + disponibles);
        System.out.println("Ejemplares Prestados: " + prestados);
    }

    private static void cargarDatosIniciales() {
        File fLibros = new File(ARCHIVO_LIBROS);
        if (fLibros.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fLibros))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Libros lib = Libros.fromLineaArchivo(linea);
                    if (lib != null) listaLibros.add(lib);
                }
            } catch (Exception ignored) {}
        }

        File fRevistas = new File(ARCHIVO_REVISTAS);
        if (fRevistas.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fRevistas))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Revistas rev = Revistas.fromLineaArchivo(linea);
                    if (rev != null) listaRevistas.add(rev);
                }
            } catch (Exception ignored) {}
        }

        File fUsuarios = new File(ARCHIVO_USUARIOS);
        if (fUsuarios.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fUsuarios))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Usuario u = Usuario.fromLineaArchivo(linea);
                    if (u != null) listaUsuarios.add(u);
                }
            } catch (Exception ignored) {}
        }

        File fEjemplares = new File(ARCHIVO_EJEMPLARES);
        if (fEjemplares.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fEjemplares))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Ejemplar ej = Ejemplar.fromLineaArchivo(linea);
                    if (ej != null) listaEjemplares.add(ej);
                }
            } catch (Exception ignored) {}
        }

        File fPrestamos = new File(ARCHIVO_PRESTAMOS);
        if (fPrestamos.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fPrestamos))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    Prestamo p = Prestamo.fromLineaArchivo(linea);
                    if (p != null) listaPrestamos.add(p);
                }
            } catch (Exception ignored) {}
        }

        if (listaLibros.isEmpty() && listaRevistas.isEmpty()) {
            cargarDatosPorDefecto();
        }
    }

    private static void cargarDatosPorDefecto() {
        Libros l1 = new Libros("LIB-001", "Fundamentos de Bases de Datos", "Silberschatz, Korth", "McGraw-Hill", 2020, 3, 2);
        Libros l2 = new Libros("LIB-002", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008, 1);
        Libros l3 = new Libros("LIB-003", "Inteligencia Artificial", "Stuart Russell", "Pearson", 2021, 1, 0);
        listaLibros.add(l1);
        listaLibros.add(l2);
        listaLibros.add(l3);

        Ejemplar ej1 = new Ejemplar("LIB-001-EJ1", "LIB-001", "LIBRO", l1.getTitulo(), false, "Estanteria A");
        listaEjemplares.add(ej1);
        for (int i = 2; i <= 3; i++) {
            listaEjemplares.add(new Ejemplar("LIB-001-EJ" + i, "LIB-001", "LIBRO", l1.getTitulo(), "Estanteria A"));
        }
        listaEjemplares.add(new Ejemplar("LIB-002-EJ1", "LIB-002", "LIBRO", l2.getTitulo(), "Estanteria A"));

        Revistas r1 = new Revistas("REV-001", "IEEE Software Magazine", 45, "0740-7459", 2023, 2);
        Revistas r2 = new Revistas("REV-002", "Communications of the ACM", 66, "0001-0782", 2023, 1);
        listaRevistas.add(r1);
        listaRevistas.add(r2);

        for (int i = 1; i <= 2; i++) {
            listaEjemplares.add(new Ejemplar("REV-001-EJ" + i, "REV-001", "REVISTA", r1.getTitulo(), "Hemeroteca B"));
        }
        listaEjemplares.add(new Ejemplar("REV-002-EJ1", "REV-002", "REVISTA", r2.getTitulo(), "Hemeroteca B"));

        listaUsuarios.add(new Usuario("1001", "Carlos Perez", "carlos@utn.edu.ec", "Software", 1, 0.0));
        listaUsuarios.add(new Usuario("1002", "Ana Sanchez", "ana@utn.edu.ec", "Sistemas", 3, 0.0));
        listaUsuarios.add(new Usuario("1003", "Luis Gomez", "luis@utn.edu.ec", "Computacion", 0, 1.50));
        listaUsuarios.add(new Usuario("1004", "Maria Lopez", "maria@utn.edu.ec", "Software"));

        listaPrestamos.add(new Prestamo("P-1", "1001", "LIB-001-EJ1", "LIB-001", l1.getTitulo(), "LIBRO", LocalDate.now().minusDays(2)));
    }

    private static void guardarDatosEnArchivos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_LIBROS))) {
            for (Libros l : listaLibros) pw.println(l.toLineaArchivo());
        } catch (Exception ignored) {}

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_REVISTAS))) {
            for (Revistas r : listaRevistas) pw.println(r.toLineaArchivo());
        } catch (Exception ignored) {}

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario u : listaUsuarios) pw.println(u.toLineaArchivo());
        } catch (Exception ignored) {}

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_EJEMPLARES))) {
            for (Ejemplar ej : listaEjemplares) pw.println(ej.toLineaArchivo());
        } catch (Exception ignored) {}

        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_PRESTAMOS))) {
            for (Prestamo p : listaPrestamos) pw.println(p.toLineaArchivo());
        } catch (Exception ignored) {}

        System.out.println("Datos guardados exitosamente en archivos.");
    }

    private static Libros buscarLibro(String id) {
        for (Libros l : listaLibros) {
            if (l.getId().equalsIgnoreCase(id)) return l;
        }
        return null;
    }

    private static Revistas buscarRevista(String id) {
        for (Revistas r : listaRevistas) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    private static Usuario buscarUsuario(String id) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdUsuario().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    private static Ejemplar buscarEjemplar(String codigo) {
        for (Ejemplar ej : listaEjemplares) {
            if (ej.getCodigoEjemplar().equalsIgnoreCase(codigo)) return ej;
        }
        return null;
    }

    private static Prestamo buscarPrestamo(String id) {
        for (Prestamo p : listaPrestamos) {
            if (p.getIdPrestamo().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String linea = scanner.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero entero.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String linea = scanner.nextLine().trim().replace(",", ".");
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un valor numerico decimal.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
