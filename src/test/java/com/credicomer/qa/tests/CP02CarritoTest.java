package com.credicomer.qa.tests;

import com.credicomer.qa.pages.CartPage;
import com.credicomer.qa.pages.HomePage;
import com.credicomer.qa.pages.ProductPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

public class CP02CarritoTest extends BaseTest {

    private static final String NOMBRE_PRODUCTO = "best is yet to come";
    private static final double TOLERANCIA = 0.01;

    @Test
    public void cp02_validacionYModificacionDelCarrito() {
        // Estado inicial propio: el carrito del sitio es compartido, así que se vacía primero
        CartPage cartPage = new CartPage(driver);
        cartPage.irAlCarrito();
        cartPage.vaciarCarritoCompleto();
        assertTrue(cartPage.estaVacio(), "El carrito no quedó vacío al preparar el estado inicial");

        // Paso 1: agregar 3 unidades del producto y entrar al carrito
        HomePage homePage = new HomePage(driver);
        homePage.escribirEnBuscador("best");
        homePage.seleccionarSugerenciaPorTexto(NOMBRE_PRODUCTO);

        ProductPage productPage = new ProductPage(driver);
        productPage.establecerCantidad(3);
        productPage.agregarAlCarrito();


        cartPage.irAlCarrito();
        int cantidadInicial = cartPage.obtenerCantidadLinea(NOMBRE_PRODUCTO);
        System.out.println("Cantidad en el carrito tras agregar: " + cantidadInicial);
        assertEquals(3, cantidadInicial, "El carrito no muestra 3 unidades del producto");

        // Paso 2: total de la línea = precio unitario x cantidad
        double precioUnitario = cartPage.obtenerPrecioUnitarioLinea(NOMBRE_PRODUCTO);
        double totalLinea = cartPage.obtenerTotalLinea(NOMBRE_PRODUCTO);
        double totalEsperado = precioUnitario * cantidadInicial;
        System.out.println("Precio unitario: " + precioUnitario + " | Total línea: " + totalLinea
                + " | Esperado: " + totalEsperado);
        assertEquals(totalEsperado, totalLinea, TOLERANCIA,
                "El total de la línea no es igual a precio unitario x cantidad");

        // Paso 3: cambiar la cantidad a 1 y verificar que se recalculan línea y carrito.
        // Se valida la DIFERENCIA del subtotal (no el valor absoluto) porque el carrito del
        // sitio es compartido y puede contener productos que este test no agregó.
        System.out.println("Productos en el carrito: " + cartPage.obtenerNombresEnCarrito());
        double subtotalAntes = cartPage.obtenerSubtotalCarrito();

        cartPage.cambiarCantidadLinea(NOMBRE_PRODUCTO, 1);

        int cantidadTrasCambio = cartPage.obtenerCantidadLinea(NOMBRE_PRODUCTO);
        double totalLineaTrasCambio = cartPage.obtenerTotalLinea(NOMBRE_PRODUCTO);
        double subtotalDespues = cartPage.obtenerSubtotalCarrito();
        double disminucionEsperada = precioUnitario * (cantidadInicial - 1);
        System.out.println("Cantidad tras cambio: " + cantidadTrasCambio + " | Total línea: " + totalLineaTrasCambio
                + " | Subtotal antes: " + subtotalAntes + " | Subtotal después: " + subtotalDespues);

        assertEquals(1, cantidadTrasCambio, "La cantidad no se actualizó a 1");
        assertEquals(precioUnitario, totalLineaTrasCambio, TOLERANCIA,
                "El total de la línea no se recalculó correctamente");
        assertEquals(disminucionEsperada, subtotalAntes - subtotalDespues, TOLERANCIA,
                "El total del carrito no disminuyó en precio unitario x unidades quitadas");

        // Paso 4: eliminar el producto y verificar carrito vacío, aviso y contador en cero
        cartPage.eliminarLinea(NOMBRE_PRODUCTO);
        assertFalse(cartPage.contieneProducto(NOMBRE_PRODUCTO),
                "El producto sigue en el carrito después de eliminarlo");

        // El sitio es público y compartido: si aparecen productos que este test no agregó,
        // se registran en consola y se eliminan para poder validar el estado final pedido.
        List<String> productosAjenos = cartPage.obtenerNombresEnCarrito();
        if (!productosAjenos.isEmpty()) {
            System.out.println("Productos ajenos detectados (no agregados por este test): "
                    + productosAjenos + ". Se eliminan para validar el carrito vacío.");
            cartPage.vaciarCarritoCompleto();
        }

        assertTrue(cartPage.estaVacio(), "El carrito no quedó vacío");
        assertTrue(cartPage.seMuestraMensajeCarritoVacio(), "No se muestra el aviso de carrito vacío");
        String aviso = cartPage.obtenerMensajeCarritoVacio();
        System.out.println("Aviso mostrado: " + aviso);
        assertFalse(aviso.isBlank(), "El aviso de carrito vacío no tiene texto");
        assertEquals(0, cartPage.obtenerContadorCarrito(), "El contador del encabezado no volvió a cero");
    }
}