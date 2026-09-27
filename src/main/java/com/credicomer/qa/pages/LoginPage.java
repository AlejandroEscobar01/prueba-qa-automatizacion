package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Pantalla de inicio de sesión. */
public class LoginPage extends BasePage {

    private final By formularioLogin = By.id("login-form");
    private final By campoCorreo = By.cssSelector("#login-form input[name='email']");
    private final By campoContrasena = By.cssSelector("#login-form input[name='password']");
    private final By botonIniciarSesion = By.cssSelector("#login-form button[type='submit']");
    private final By mensajeError = By.cssSelector("#login-form .alert-danger, .login-form .alert-danger");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean formularioVisible() {
        try {
            return esperarVisible(formularioLogin).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean campoCorreoVisible() {
        try {
            return esperarVisible(campoCorreo).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean campoContrasenaVisible() {
        try {
            return esperarVisible(campoContrasena).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void iniciarSesion(String correo, String contrasena) {
        WebElement correoInput = esperarVisible(campoCorreo);
        correoInput.clear();
        correoInput.sendKeys(correo);

        WebElement contrasenaInput = esperarVisible(campoContrasena);
        contrasenaInput.clear();
        contrasenaInput.sendKeys(contrasena);

        esperarClickeable(botonIniciarSesion).click();
        esperarCargaCompleta();
    }

    public boolean seMuestraMensajeError() {
        try {
            return esperarVisible(mensajeError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerMensajeError() {
        return esperarVisible(mensajeError).getText();
    }
}

