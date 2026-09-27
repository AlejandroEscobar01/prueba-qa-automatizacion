package com.credicomer.qa.tests;

import com.credicomer.qa.pages.HomePage;
import com.credicomer.qa.pages.SearchResultsPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CP03BusquedaSinResultadosTest extends BaseTest {

    private static final String TEXTO_INEXISTENTE = "xyzqw123";

    @Test
    public void cp03_busquedaSinResultados() {
        // Paso 1: desde la página principal, buscar un texto que no existe en el catálogo
        HomePage homePage = new HomePage(driver);
        homePage.buscar(TEXTO_INEXISTENTE);

        SearchResultsPage resultados = new SearchResultsPage(driver);
        resultados.esperarResultados();

        // Condición 1: no se muestra ningún producto en los resultados
        int cantidadProductos = resultados.obtenerCantidadProductos();
        System.out.println("Productos encontrados para '" + TEXTO_INEXISTENTE + "': " + cantidadProductos);
        assertEquals(0, cantidadProductos,
                "La búsqueda de un texto inexistente no debería mostrar productos");

        // Condición 2: la página presenta el mensaje de búsqueda sin resultados
        assertTrue(resultados.seMuestraMensajeSinResultados(),
                "No se muestra el mensaje de búsqueda sin resultados");
        String mensaje = resultados.obtenerMensajeSinResultados();
        System.out.println("Mensaje mostrado: " + mensaje);
        assertTrue(mensaje.contains("No matches were found for your search"),
                "El mensaje no corresponde a búsqueda sin resultados. Mensaje actual: " + mensaje);
        }
        }