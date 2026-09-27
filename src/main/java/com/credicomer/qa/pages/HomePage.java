package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Keys;
import java.util.List;

public class HomePage extends BasePage {

    private final By campoBusqueda = By.cssSelector("#search_widget input[name='s']");
    private final By listaSugerencias = By.cssSelector("ul.ui-autocomplete li.ui-menu-item a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public String obtenerTitulo() {
        return driver.getTitle();
    }

    public void escribirEnBuscador(String texto) {
        WebElement input = esperarVisible(campoBusqueda);
        input.clear();
        input.sendKeys(texto);
    }

    /** Escribe el texto en el buscador y presiona Enter para ir a la página de resultados. */
    public void buscar(String texto) {
        WebElement input = esperarVisible(campoBusqueda);
        input.clear();
        input.sendKeys(texto, Keys.ENTER);
    }

    public List<WebElement> obtenerSugerencias() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(listaSugerencias));
        return driver.findElements(listaSugerencias);
    }

    public void seleccionarSugerenciaPorTexto(String textoParcial) {
        List<WebElement> sugerencias = obtenerSugerencias();
        for (WebElement sugerencia : sugerencias) {
            if (sugerencia.getText().toLowerCase().contains(textoParcial.toLowerCase())) {
                sugerencia.click();
                return;
            }
        }
        throw new RuntimeException("No se encontró ninguna sugerencia que contenga: " + textoParcial);
    }
}