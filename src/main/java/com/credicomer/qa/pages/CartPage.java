package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {

    private static final String URL_CARRITO =
            "https://teststore.automationtesting.co.uk/index.php?controller=cart&action=show";

    private final By lineasCarrito = By.cssSelector("ul.cart-items li.cart-item");
    private final By nombreEnLinea = By.cssSelector(".product-line-info a.label");
    private final By cantidadEnLinea = By.cssSelector("input.js-cart-line-product-quantity");
    private final By precioUnitarioEnLinea = By.cssSelector(".product-price.h5 .price");
    private final By totalEnLinea = By.cssSelector("div.price span.product-price strong");
    private final By botonAumentar = By.cssSelector("button.js-increase-product-quantity");
    private final By botonDisminuir = By.cssSelector("button.js-decrease-product-quantity");
    private final By botonEliminar = By.cssSelector("a.remove-from-cart");
    private final By subtotalProductos = By.cssSelector("#cart-subtotal-products .value");
    private final By contadorCarritoHeader = By.id("_desktop_cart");
    private final By mensajeCarritoVacio = By.cssSelector("span.no-items");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void irAlCarrito() {
        driver.get(URL_CARRITO);
        esperarCargaCompleta();
    }

    public List<WebElement> obtenerLineas() {
        return driver.findElements(lineasCarrito);
    }

    /** Busca la línea del carrito cuyo nombre contiene el texto indicado. */
    private WebElement obtenerLinea(String nombreProducto) {
        String buscado = nombreProducto.toLowerCase();
        try {
            return wait.until(d -> {
                for (WebElement linea : d.findElements(lineasCarrito)) {
                    try {
                        String nombre = linea.findElement(nombreEnLinea).getText().toLowerCase();
                        if (nombre.contains(buscado)) {
                            return linea;
                        }
                    } catch (Exception e) {
                        // la línea se redibujó mientras la leíamos; se reintenta
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException("No se encontró en el carrito el producto: " + nombreProducto, e);
        }
    }

    public int obtenerCantidadLinea(String nombreProducto) {
        String valor = obtenerLinea(nombreProducto).findElement(cantidadEnLinea).getAttribute("value");
        return Integer.parseInt(valor);
    }

    public double obtenerPrecioUnitarioLinea(String nombreProducto) {
        return convertirPrecio(obtenerLinea(nombreProducto).findElement(precioUnitarioEnLinea).getText());
    }

    public double obtenerTotalLinea(String nombreProducto) {
        return convertirPrecio(obtenerLinea(nombreProducto).findElement(totalEnLinea).getText());
    }

    /** Subtotal de productos del resumen del carrito (sin envío). */
    public double obtenerSubtotalCarrito() {
        return convertirPrecio(esperarVisible(subtotalProductos).getText());
    }

    /** Nombres de todos los productos en el carrito (útil para diagnosticar productos ajenos). */
    public List<String> obtenerNombresEnCarrito() {
        List<String> nombres = new java.util.ArrayList<>();
        for (WebElement linea : obtenerLineas()) {
            nombres.add(linea.findElement(nombreEnLinea).getText());
        }
        return nombres;
    }

    /**
     * Cambia la cantidad usando los botones + / - del carrito, uno a uno, esperando
     * en cada clic a que el servidor actualice la línea.
     */
    public void cambiarCantidadLinea(String nombreProducto, int cantidadDeseada) {
        int actual = obtenerCantidadLinea(nombreProducto);
        while (actual != cantidadDeseada) {
            By boton = (actual < cantidadDeseada) ? botonAumentar : botonDisminuir;
            obtenerLinea(nombreProducto).findElement(boton).click();

            int valorAntes = actual;
            wait.until(d -> {
                try {
                    return obtenerCantidadLinea(nombreProducto) != valorAntes;
                } catch (Exception e) {
                    return false;
                }
            });
            esperarCargaCompleta();
            actual = obtenerCantidadLinea(nombreProducto);
        }

        // Esperamos a que el total de la línea termine de recalcularse antes de devolver el control
        double precioUnitario = obtenerPrecioUnitarioLinea(nombreProducto);
        double totalEsperado = precioUnitario * cantidadDeseada;
        wait.until(d -> {
            try {
                return Math.abs(obtenerTotalLinea(nombreProducto) - totalEsperado) < 0.01;
            } catch (Exception e) {
                return false;
            }
        });
        esperarCargaCompleta();
    }

    /**
     * Elimina el producto del carrito. Si la página no refleja la eliminación, recarga el
     * carrito para ver el estado real del servidor y reintenta (el sitio público a veces
     * ignora la primera petición).
     */
    public void eliminarLinea(String nombreProducto) {
        for (int intento = 1; intento <= 3; intento++) {
            try {
                obtenerLinea(nombreProducto).findElement(botonEliminar).click();
            } catch (StaleElementReferenceException e) {
                continue; // la línea se redibujó justo al hacer clic; se reintenta
            }
            if (esperarQueDesaparezca(nombreProducto)) {
                return;
            }
            irAlCarrito();
            if (!contieneProducto(nombreProducto)) {
                return;
            }
            System.out.println("Intento " + intento + ": el producto seguía en el carrito, se reintenta.");
        }
        throw new IllegalStateException("No fue posible eliminar del carrito el producto: " + nombreProducto);
    }

    private boolean esperarQueDesaparezca(String nombreProducto) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !contieneProducto(nombreProducto));
            esperarCargaCompleta();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Indica si en el carrito hay una línea cuyo nombre contiene el texto indicado. */
    public boolean contieneProducto(String nombreProducto) {
        String buscado = nombreProducto.toLowerCase();
        for (WebElement linea : obtenerLineas()) {
            try {
                if (linea.findElement(nombreEnLinea).getText().toLowerCase().contains(buscado)) {
                    return true;
                }
            } catch (Exception e) {
                return true; // la línea se está redibujando; todavía no se puede confirmar
            }
        }
        return false;
    }

    /**
     * Deja el carrito vacío para que el caso no dependa de ejecuciones anteriores.
     * Si el sitio no refleja una eliminación, recarga el carrito y vuelve a intentar.
     */
    public void vaciarCarritoCompleto() {
        for (int intento = 0; intento < 10 && !obtenerLineas().isEmpty(); intento++) {
            int cantidadAntes = obtenerLineas().size();
            try {
                obtenerLineas().get(0).findElement(botonEliminar).click();
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(d -> obtenerLineas().size() < cantidadAntes);
                esperarCargaCompleta();
            } catch (Exception e) {
                irAlCarrito(); // no se reflejó el cambio: recargamos y volvemos a intentar
            }
        }
    }

    public boolean estaVacio() {
        return obtenerLineas().isEmpty();
    }

    public boolean seMuestraMensajeCarritoVacio() {
        return !driver.findElements(mensajeCarritoVacio).isEmpty();
    }

    public String obtenerMensajeCarritoVacio() {
        return esperarVisible(mensajeCarritoVacio).getText();
    }

    public int obtenerContadorCarrito() {
        try {
            String texto = esperarVisible(contadorCarritoHeader).getText();
            return Integer.parseInt(texto.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}