package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductPage extends BasePage {

    private final By tituloProducto = By.cssSelector("h1.h1");
    private final By seccionDescripcion = By.id("description");
    private final By inputCantidad = By.id("quantity_wanted");
    private final By botonAgregar = By.cssSelector("button.add-to-cart");
    private final By contadorCarrito = By.id("_desktop_cart");

    private static final int INTENTOS_CANTIDAD = 3;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String obtenerNombreProducto() {
        return esperarVisible(tituloProducto).getText();
    }

    public void scrollHastaDescripcion() {
        WebElement seccion = esperarVisible(seccionDescripcion);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", seccion);
    }

    public boolean seccionDescripcionEsVisible() {
        try {
            return esperarVisible(seccionDescripcion).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Escribe la cantidad en el campo "touchspin".
     *
     * Al abrir la página, PrestaShop hace una recarga AJAX del bloque de compra. Si se escribe
     * la cantidad antes de que esa recarga termine, el bloque se vuelve a dibujar y el campo
     * regresa a 1. Por eso: (1) se espera a que no haya AJAX pendiente antes de escribir,
     * (2) se vuelve a buscar el campo en cada intento (puede haber sido reemplazado) y
     * (3) si el valor no quedó fijo, se reintenta.
     */
    public void establecerCantidad(int cantidadDeseada) {
        String esperado = String.valueOf(cantidadDeseada);
        esperarVisible(inputCantidad);
        esperarCargaCompleta();

        for (int intento = 1; intento <= INTENTOS_CANTIDAD; intento++) {
            try {
                WebElement campo = esperarVisible(inputCantidad);
                // CONTROL+A selecciona el valor actual y el texto nuevo lo reemplaza directamente,
                // sin dejar el campo vacío (un campo vacío hace que el sitio lo regrese a 1).
                campo.sendKeys(Keys.chord(Keys.CONTROL, "a"), esperado, Keys.TAB);
            } catch (StaleElementReferenceException e) {
                continue; // el bloque se redibujó mientras escribíamos; se reintenta
            }
            esperarCargaCompleta();
            if (cantidadSeMantiene(esperado)) {
                return;
            }
        }
        throw new IllegalStateException("No fue posible fijar la cantidad en " + cantidadDeseada
                + " después de " + INTENTOS_CANTIDAD + " intentos. Valor actual: " + obtenerCantidad());
    }

    /** Lee el campo varias veces seguidas para confirmar que el valor ya no cambia. */
    private boolean cantidadSeMantiene(String esperado) {
        try {
            for (int i = 0; i < 3; i++) {
                if (!esperado.equals(obtenerCantidad())) {
                    return false;
                }
                Thread.sleep(300);
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String obtenerCantidad() {
        for (int i = 0; i < 3; i++) {
            try {
                return esperarVisible(inputCantidad).getAttribute("value");
            } catch (StaleElementReferenceException e) {
                // el campo se redibujó; se vuelve a buscar
            }
        }
        return esperarVisible(inputCantidad).getAttribute("value");
    }

    /** Hace clic en "Add to cart" y espera a que el servidor responda (sin pausas fijas). */
    public void agregarAlCarrito() {
        esperarCargaCompleta();
        esperarClickeable(botonAgregar).click();
        esperarCargaCompleta();
    }

    public int obtenerContadorCarrito() {
        try {
            String texto = esperarVisible(contadorCarrito).getText();
            return Integer.parseInt(texto.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}