package com.credicomer.qa.base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected WebElement esperarVisible(By localizador) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(localizador));
    }

    protected WebElement esperarPresente(By localizador) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(localizador));
    }

    protected WebElement esperarClickeable(By localizador) {
        return wait.until(ExpectedConditions.elementToBeClickable(localizador));
    }

    /**
     * Espera a que la página termine de cargar y a que no haya peticiones AJAX pendientes.
     * PrestaShop usa jQuery para refrescar partes de la página (bloque de compra, carrito,
     * contador del encabezado), así que "jQuery.active == 0" indica que ya respondió el servidor.
     */
    protected void esperarCargaCompleta() {
        wait.until(d -> {
            Object listo = ((JavascriptExecutor) d).executeScript(
                    "return document.readyState === 'complete' && "
                            + "(typeof jQuery === 'undefined' || jQuery.active === 0);");
            return Boolean.TRUE.equals(listo);
        });
    }

    /** Convierte un texto de precio como "$29.00" en un número (29.0). */
    protected double convertirPrecio(String precioTexto) {
        String limpio = precioTexto.replaceAll("[^0-9.]", "");
        return Double.parseDouble(limpio);
    }
}