# Trabajo 1 - Base de Datos: POO y Manejo de Archivos

**Universidad Técnica del Norte (UTN)**  
**Facultad de Ingeniería en Ciencias Aplicadas (FICA)**  
**Asignatura:** Base de Datos  
**Docente:** Ing. Marco R. Pusdá-Chulde, PhD.

---

## Integrantes del Grupo

- Emely Colta
- Marilin Martinez
- Tupak Cachimuel
- Jeremy Luna
- Robinho Liquinchano
- Integrante 6

---

## Descripción del Proyecto

Este repositorio contiene la solución a los 5 ejercicios planteados en el **Trabajo 1**, desarrollados bajo el paradigma de **Programación Orientada a Objetos (POO)** en el lenguaje **Java** y utilizando **archivos de texto plano (.txt / .csv)** para la persistencia de datos (sin motores de bases de datos relacionales externos).

### Resumen de los 5 Ejercicios:
1. **Ejercicio 1 - Biblioteca UTN:** Préstamo y devolución de libros (7 días, multa $0.25/día) y revistas (3 días, multa $0.50/día), control de ejemplares disponibles, límite de 3 préstamos por usuario, bloqueo por multas y reportes de auditoría.
2. **Ejercicio 2 - Cooperativa de Ahorro y Crédito:** Gestión de cuentas de ahorros (4% interés anual) y corrientes (sobregiro de hasta $200), transferencias entre cuentas y auditoría de saldos.
3. **Ejercicio 3 - Cooperativa de Transporte:** Venta y anulación de boletos (rutas Ibarra-Quito, Ibarra-Tulcán, Ibarra-Otavalo), control de asientos y descuentos (50% tercera edad/discapacidad, 85% paradas intermedias).
4. **Ejercicio 4 - Cafetería UTN:** Control de stock de ingredientes (kg/litros), recetas de platos preparados (costo + 100% margen), bebidas embotelladas (+15% IVA) y ventas con descuento automático de stock.
5. **Ejercicio 5 - Tienda de Artesanías en Consignación:** Recepción, ventas y devolución de textiles (20% comisión) y tallas en madera (25% comisión), liquidaciones a artesanos y control de piezas con más de 90 días.

---

## Estructura del Repositorio

```text
base_datos/
│
├── Documentos/
│   └── Tr1-BDD.pdf                               # Enunciado del trabajo
│
├── docs/
│   ├── diagramas/                                # Diagramas de clases UML
│   ├── casos-prueba/                             # Tablas y evidencias de pruebas
│   └── capturas/                                 # Capturas de pantalla de ejecución
│
├── ejercicio1/
│   └── Ejercicio1BDD-Biblioteca_UTN-Prestamo_libros/  # Proyecto NetBeans Ejercicio 1
│       ├── src/ejercicio1bdd/                    # Código fuente Java
│       ├── nbproject/                            # Configuración de NetBeans / Ant
│       └── build.xml
│
├── ejercicio2/
│   └── Ejercicio2Cooperativa/                    # Proyecto NetBeans Ejercicio 2
│
├── ejercicio3/                                   # Proyecto NetBeans Ejercicio 3
├── ejercicio4/                                   # Proyecto NetBeans Ejercicio 4
└── ejercicio5/                                   # Proyecto NetBeans Ejercicio 5
```

---

## Requisitos del Entorno

- **Java Development Kit (JDK):** Versión 17, 21 o superior instalada y configurada en el `PATH`.
- **IDE Recomendado:** Apache NetBeans IDE (versión 18 o superior).
- **Herramienta de compilación:** Apache Ant (integrada en Apache NetBeans).
- **Control de versiones:** Git.

# 

# \- Emely Colta

# \- Marilin Martinez

# \- Tupak Cachimuel

# \- Jeremy Luna

# \- Mateo Pupiales

# \- Integrante 6

# 

# \## Descripción

# 

# Proyecto académico desarrollado para la asignatura de Base de Datos. El repositorio contiene la implementación de los ejercicios propuestos utilizando Java, aplicando conceptos de Programación Orientada a Objetos, diagramas UML, control de versiones con Git y almacenamiento de datos mediante archivos.

# 

# \## Estructura del Repositorio

# 

# ```text

# Trabajo1-BDD/

# │

# ├── docs/

# │

# ├── ejercicio1-biblioteca/

# ├── ejercicio2-cooperativa/

# ├── ejercicio3-cooperativa-transporte/

# ├── ejercicio4-cafeteria/

# └── ejercicio5-tienda/

# ```

# 

# \## Tecnologías Utilizadas

# 

# \- Java

# \- Apache NetBeans

# \- Git

# \- GitHub

# 

# \## Requisitos

# 

# \- Java JDK 17 o superior

# \- Apache NetBeans IDE

# 

# \## Compilación y Ejecución

# 

# 1\. Clonar el repositorio.

# 2\. Abrir la carpeta correspondiente al ejercicio en Apache NetBeans.

# 3\. Compilar el proyecto.

# 4\. Ejecutar la clase principal del ejercicio seleccionado.

# 

# \## Diagramas UML

# 

# Los diagramas de clases se encuentran en:

# 

# docs/diagramas/

# 

# \## Datos de Prueba

# 

# Los archivos de prueba utilizados por cada ejercicio se encuentran en las carpetas:

# 

# datos/

# 

# correspondientes a cada ejercicio.

# 

# \## Control de Versiones

# 

# El desarrollo del proyecto se realizó mediante Git y GitHub, utilizando ramas individuales para cada integrante y registrando los avances mediante commits.

## Instrucciones para Compilar y Ejecutar

### Método 1: Desde Apache NetBeans (Recomendado)

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Anahx07/Trabajo1-BDD.git
   ```
2. **Abrir NetBeans IDE.**
3. **Abrir el proyecto deseado:**
   - En el menú superior, ve a **File** > **Open Project...** (o presiona `Ctrl + Shift + O`).
   - Navega hasta la carpeta del ejercicio que deseas ejecutar. Por ejemplo:
     - Para Ejercicio 1: Selecciona la carpeta `ejercicio1/Ejercicio1BDD-Biblioteca_UTN-Prestamo_libros` (debe mostrar el ícono de taza de café de NetBeans).
     - Para Ejercicio 2: Selecciona la carpeta `ejercicio2/Ejercicio2Cooperativa`.
   - Haz clic en **Open Project**.
4. **Compilar el proyecto:**
   - En la pestaña lateral izquierda **Projects**, haz clic derecho sobre el proyecto abierto.
   - Selecciona **Clean and Build** (o presiona `Shift + F11`).
   - Verifica en la ventana *Output* inferior que aparezca el mensaje `BUILD SUCCESSFUL`.
5. **Ejecutar el proyecto:**
   - Haz clic derecho sobre el proyecto y selecciona **Run** (o presiona `F6`).
   - También puedes hacer clic derecho directamente sobre la clase principal (`Main.java`) y seleccionar **Run File** (`Shift + F6`).
   - El menú interactivo por consola se iniciará en la pestaña *Output*.

---

### Método 2: Desde la Terminal con Apache Ant

Si tienes Apache Ant instalado o usas la terminal integrada:

1. Abrir la terminal y situarse en la carpeta raíz del ejercicio específico:
   ```bash
   cd "ejercicio1/Ejercicio1BDD-Biblioteca_UTN-Prestamo_libros"
   ```
2. Limpiar y compilar el proyecto:
   ```bash
   ant clean compile
   ```
3. Ejecutar la aplicación:
   ```bash
   ant run
   ```

---

### Método 3: Compilación Directa por Terminal con JDK (`javac` / `java`)

Para compilar y correr el código manualmente sin herramientas de construcción:

```bash
# 1. Ingresar a la carpeta del ejercicio
cd "ejercicio1/Ejercicio1BDD-Biblioteca_UTN-Prestamo_libros"

# 2. Crear el directorio de clases compiladas
mkdir -p build/classes

# 3. Compilar todos los archivos .java del paquete
javac -encoding UTF-8 -d build/classes src/ejercicio1bdd/*.java

# 4. Ejecutar la clase principal
java -cp build/classes ejercicio1bdd.Main
```

---


## Persistencia de Datos en Archivos

Cada ejercicio almacena y recupera su estado utilizando archivos de texto plano estructurados (delimitados por punto y coma `;`), garantizando que la información se conserve al cerrar y reabrir la aplicación:

- **Lectura/Carga:** Al iniciar el aplicativo, se leen los registros línea por línea y se reconstruyen las instancias en memoria mediante métodos de deserialización estáticos (`fromLineaArchivo`).
- **Escritura/Guardado:** Al seleccionar la opción de guardar o salir del sistema, se vuelcan las colecciones activas a los archivos correspondientes (`toLineaArchivo`).

---

## Diagramas de Clases (UML)

Los diagramas UML detallados de cada ejercicio se encuentran organizados en la carpeta `docs/diagramas/`.

---

## Control de Versiones

El proyecto se gestionó mediante **Git y GitHub** siguiendo las directrices de trabajo colaborativo:
- Cada integrante contó con una rama personal (`nombre_apellido`) para registrar sus contribuciones individuales mediante commits frecuentes.
- La rama `main` consolida las versiones estables de todos los ejercicios.

---

## Validación y Casos de Prueba (QA)

Como parte del control de calidad e integración de los entregables, se implementó una matriz de validación funcional para verificar que las reglas de negocio y restricciones de persistencia se cumplan en cada módulo:

| Módulo / Ejercicio | Regla de Negocio Crítica Verificada | Tipo de Archivo / Persistencia | Resultado de Prueba |
| :--- | :--- | :--- | :---: |
| **1. Biblioteca UTN** | Límite máx. 3 préstamos activos y cálculo de multas ($0.25 libro / $0.50 revista). | Archivos de texto delimitados (`;`) | **PASSED** |
| **2. Cooperativa Imbabura** | Saldo mínimo $5 en Ahorro, sobregiro máx. $200 en Corriente y transferencias atómicas. | `.txt` (`cuentas.txt`, `movimientos.txt`, `socios.txt`) | **PASSED** |
| **3. Cooperativa Transporte** | Descuentos (85% intermedias, 50% 3ra edad/discapacidad) y bloqueo de anulación < 2h. | `.txt` (`datos_cooperativa.txt`) | **PASSED** |
| **4. Cafetería UTN** | Precio = 2x costo, IVA 15% en bebidas y rechazo atómico de venta si falta insumo. | `.txt` (`bebidas.txt`, `ingredientes.txt`, `recetas.txt`) | **PASSED** |
| **5. Tienda Artesanías** | Comisiones (20% textil, 25% madera) y marcado automático de devolución > 90 días. | JSON (`tienda.json`) | **PASSED** |


---

## Declaración de Uso de IA (Compromiso 7 del Sílabo)

El desarrollo del proyecto contó con la asistencia puntual de **Google Gemini** para:
- Estructuración de los métodos de serialización y deserialización `toLineaArchivo()` y `fromLineaArchivo()` en formato CSV delimitado por punto y coma (`;`).
- Verificación de lógica temporal (`java.time.Duration`) y algoritmos de consistencia transaccional.

Todas las salidas generadas fueron revisadas, depuradas y validadas manualmente por el equipo estudiantil para garantizar la integridad de los requerimientos académicos.
