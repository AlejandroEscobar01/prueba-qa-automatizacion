package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página de resultados de búsqueda. */
public class SearchResultsPage extends BasePage {

    private final By productosEnResultados = By.cssSelector("#js-product-list .product-miniature");
    private final By mensajeSinResultados = By.cssSelector(".page-not-found");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Espera a que la página de resultados termine de cargar: o aparecen productos
     * o aparece el mensaje de "sin resultados".
     */
    public void esperarResultados() {
        wait.until(d -> !d.findElements(productosEnResultados).isEmpty()
                || !d.findElements(mensajeSinResultados).isEmpty());
        esperarCargaCompleta();
    }

    public int obtenerCantidadProductos() {
        return driver.findElements(productosEnResultados).size();
    }

    public boolean seMuestraMensajeSinResultados() {
        return !driver.findElements(mensajeSinResultados).isEmpty()
                && driver.findElement(mensajeSinResultados).isDisplayed();
    }

    public String obtenerMensajeSinResultados() {
        return esperarVisible(mensajeSinResultados).getText();
    }
}
