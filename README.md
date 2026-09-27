# Prueba QA Automatizada — Credicomer

Proyecto de automatización de pruebas funcionales sobre la tienda de demostración
https://teststore.automationtesting.co.uk/index.php, usando Selenium WebDriver + Java,
con el patrón de diseño Page Object Model (POM).

## Stack técnico

- Java 17 o superior (JDK)
- Selenium WebDriver 4.25.0
- Maven 3.8+ (con Maven Surefire Plugin 3.2.5)
- JUnit 5
- WebDriverManager (gestión automática del driver del navegador)
- Google Chrome (última versión estable)

## Requisitos previos

1. Tener instalado el **JDK 17 o superior** (`java -version` para verificar).
2. Tener instalado **Maven 3.8+** (`mvn -version` para verificar), o usar el Maven incluido en IntelliJ IDEA.
3. Tener instalado **Google Chrome**. No es necesario descargar el ChromeDriver: WebDriverManager lo gestiona automáticamente.

## Instalación

```bash
git clone https://github.com/<AlejandroEscobar01>/prueba-qa-automatizacion.git
cd prueba-qa-automatizacion
mvn clean install -DskipTests
```

## Ejecución de las pruebas

Ejecutar todas las pruebas:

```bash
mvn test
```

Ejecutar un caso específico:

```bash
mvn test -Dtest=CP01BusquedaCarritoTest
mvn test -Dtest=CP02CarritoTest
mvn test -Dtest=CP03BusquedaSinResultadosTest
mvn test -Dtest=CP04LoginInvalidoTest
```

También pueden ejecutarse desde IntelliJ IDEA:

- Un caso: clic derecho sobre la clase de prueba → **Run**.
- Todos: panel **Maven** → **Lifecycle** → doble clic en **test**.

El reporte de JUnit generado por Surefire queda en `target/surefire-reports/`.

## Estructura del proyecto

```
├── evidencia/                             → capturas de la ejecución de cada caso
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/credicomer/qa/
    │   ├── base/BasePage.java                 → configuración común de las páginas (driver, esperas explícitas, utilidades)
    │   └── pages/
    │       ├── HeaderComponent.java           → encabezado del sitio (Sign in / Sign out), común a todas las páginas
    │       ├── HomePage.java                  → pantalla de inicio (buscador y sugerencias)
    │       ├── SearchResultsPage.java         → página de resultados de búsqueda
    │       ├── ProductPage.java               → detalle de producto (cantidad, agregar al carrito)
    │       ├── CartConfirmationModal.java     → ventana de confirmación al agregar al carrito
    │       ├── CartPage.java                  → página del carrito (líneas, cantidades, totales, eliminar)
    │       └── LoginPage.java                 → pantalla de inicio de sesión
    └── test/java/com/credicomer/qa/tests/
        ├── BaseTest.java                      → ciclo de vida del navegador, URL base y captura de evidencia
        ├── CP01BusquedaCarritoTest.java       → caso de prueba CP-01
        ├── CP02CarritoTest.java               → caso de prueba CP-02
        ├── CP03BusquedaSinResultadosTest.java → caso de prueba CP-03
        └── CP04LoginInvalidoTest.java         → caso de prueba CP-04
```

## Decisiones técnicas

- **Page Object Model:** cada pantalla o componente tiene su propia clase con sus localizadores
  (`By` declarados como constantes) y las acciones del usuario. Las clases de prueba solo
  contienen el flujo y las aserciones; no tienen localizadores.
- **Componentes separados de las páginas:** el modal de confirmación del carrito
  (`CartConfirmationModal`) y el encabezado (`HeaderComponent`) se modelaron como clases
  propias. El modal es un componente independiente de la página de producto, y el encabezado
  es común a todas las páginas, así se reutilizan sin duplicar localizadores.
- **Clase base para páginas y para pruebas:** `BasePage` concentra las esperas y utilidades
  comunes; `BaseTest` concentra la apertura y el cierre del navegador, la URL base y la captura
  de evidencia.
- **Esperas explícitas en lugar de pausas fijas:** se usa `WebDriverWait` y, en `BasePage`, el
  método `esperarCargaCompleta()`, que espera a que la página termine de cargar y a que no haya
  peticiones AJAX pendientes (`jQuery.active == 0`). PrestaShop actualiza partes de la página
  por AJAX (bloque de compra, carrito, contador), y esperar ese estado evita condiciones de carrera.
- **Validaciones sin precios fijos:** los precios se leen de la página en el momento de la
  ejecución y los cálculos (precio × cantidad) se hacen a partir de esos valores, como pide
  el enunciado.
- **Textos obtenidos del sitio:** los mensajes validados ("No matches were found for your
  search", "Authentication failed.") se obtuvieron de la aplicación al implementar las pruebas.
- **Líneas del carrito buscadas por nombre de producto:** así la prueba trabaja sobre su propio
  producto aunque el carrito contenga otros.
- **Evidencia automática:** al finalizar cada prueba, `BaseTest` guarda una captura del
  navegador en la carpeta `evidencia/` antes de cerrarlo.

## CP-01 — Búsqueda de producto y agregado al carrito

Producto utilizado: **"The best is yet to come' Framed poster"** (resultado de buscar "best").

### Observaciones — Paso 6 (dimensión)

El producto sí ofrece opciones de dimensión (40x60cm, entre otras), pero el precio mostrado no
varía entre ellas: se mantiene en $29.00 independientemente de la dimensión seleccionada. No fue
posible validar con una aserción significativa el cambio de precio para