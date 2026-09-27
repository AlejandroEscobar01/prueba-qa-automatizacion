package com.credicomer.qa.tests;

import com.credicomer.qa.pages.CartConfirmationModal;
import com.credicomer.qa.pages.HomePage;
import com.credicomer.qa.pages.ProductPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CP01BusquedaCarritoTest extends BaseTest {

    @Test
    public void cp01_busquedaProductoYVerificarDetalle() {
        // Paso 1
        HomePage homePage = new HomePage(driver);
        String titulo = homePage.obtenerTitulo();
        System.out.println("Título de la página: " + titulo);
        assertTrue(titulo.contains("Test Store"),
                "El título no corresponde a la tienda. Título actual: " + titulo);

        // Paso 2
        homePage.escribirEnBuscador("best");
        List<WebElement> sugerencias = homePage.obtenerSugerencias();
        boolean algunaContieneBest = sugerencias.stream()
                .anyMatch(s -> s.getText().toLowerCase().contains("best"));
        assertTrue(algunaContieneBest, "Ninguna sugerencia contiene la palabra 'best'");

        // Paso 3
        homePage.seleccionarSugerenciaPorTexto("best is yet to come");

        // Paso 4
        ProductPage productPage = new ProductPage(driver);
        String nombre = productPage.obtenerNombreProducto();
        assertTrue(nombre.toLowerCase().contains("best is yet to come"),
                "El nombre del producto no coincide. Nombre actual: " + nombre);

        // Paso 5
        productPage.scrollHastaDescripcion();
        assertTrue(productPage.seccionDescripcionEsVisible(),
                "La sección de descripción no quedó visible tras el scroll");

        // Paso 7
        productPage.establecerCantidad(3);
        assertTrue(productPage.obtenerCantidad().equals("3"),
                "El campo de cantidad no refleja el valor 3. Valor actual: " + productPage.obtenerCantidad());

        // Paso 8
        productPage.agregarAlCarrito();
        CartConfirmationModal modal = new CartConfirmationModal(driver);

        String nombreEnModal = modal.obtenerNombreProducto();
        String cantidadEnModal = modal.obtenerCantidad();
        double precioUnitario = modal.obtenerPrecioUnitario();
        double subtotal = modal.obtenerSubtotal();

        System.out.println("Modal -> nombre: " + nombreEnModal + " | cantidad: " + cantidadEnModal + " | subtotal: " + subtotal);

        assertTrue(nombreEnModal.toLowerCase().contains("best is yet to come"),
                "El nombre en el modal no coincide");
        //cambio
        int cantidadNumerica = Integer.parseInt(cantidadEnModal.replaceAll("[^0-9]", ""));
        assertTrue(cantidadNumerica >= 3,
                "La cantidad en el modal (" + cantidadNumerica + ") es menor a la cantidad agregada (3). Valor actual: " + cantidadEnModal);
        double subtotalEsperadoMinimo = precioUnitario * 3;
        assertTrue(subtotal >= subtotalEsperadoMinimo - 0.01,
                "El subtotal del carrito ($" + subtotal + ") es menor a lo que debería aportar este producto ($" +
                        subtotalEsperadoMinimo + "). El producto no parece haberse agregado con el valor correcto.");

        // Paso 9
        modal.continuarComprando();
        int contador = productPage.obtenerContadorCarrito();
        //cambio
        assertTrue(contador >= 3,
                "El contador del carrito (" + contador + ") es menor a la cantidad agregada (3).");
    }
}