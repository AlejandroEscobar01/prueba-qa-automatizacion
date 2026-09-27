package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Encabezado del sitio. Es común a todas las páginas, por eso se modela como un
 * componente aparte y no dentro de una página específica.
 */
public class HeaderComponent extends BasePage {

    // Enlace "Sign in" del encabezado: apunta a controller=authentication (verificado en el sitio)
    private final By enlaceIniciarSesion = By.cssSelector("#_desktop_user_info .user-info a[href*='controller=authentication']");
    private final By enlaceCerrarSesion = By.cssSelector("#_desktop_user_info a.logout");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public void irAIniciarSesion() {
        esperarClickeable(enlaceIniciarSesion).click();
        esperarCargaCompleta();
    }

    public boolean opcionIniciarSesionVisible() {
        List<WebElement> enlaces = driver.findElements(enlaceIniciarSesion);
        return !enlaces.isEmpty() && enlaces.get(0).isDisplayed();
    }

    public String obtenerTextoOpcionIniciarSesion() {
        return esperarVisible(enlaceIniciarSesion).getText();
    }

    /** El enlace "Sign out" solo aparece cuando hay un usuario autenticado. */
    public boolean usuarioAutenticado() {
        return !driver.findElements(enlaceCerrarSesion).isEmpty();
    }
}
