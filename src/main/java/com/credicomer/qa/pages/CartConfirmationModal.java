package com.credicomer.qa.pages;

import com.credicomer.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartConfirmationModal extends BasePage {

    private final By nombreProducto = By.cssSelector("#blockcart-modal .product-name");
    private final By precioUnitario = By.cssSelector("#blockcart-modal .product-price");
    private final By cantidadProducto = By.cssSelector("#blockcart-modal .product-quantity");
    private final By subtotalModal = By.cssSelector("#blockcart-modal .subtotal.value");
    private final By botonContinuarComprando = By.cssSelector("#blockcart-modal button.btn-secondary");

    public CartConfirmationModal(WebDriver driver) {
        super(driver);
    }

    public String obtenerNombreProducto() {
        return esperarVisible(nombreProducto).getText();
    }

    public double obtenerPrecioUnitario() {
        return convertirPrecio(esperarVisible(precioUnitario).getText());
    }

    public String obtenerCantidad() {
        return esperarVisible(cantidadProducto).getText();
    }

    public double obtenerSubtotal() {
        return convertirPrecio(esperarVisible(subtotalModal).getText());
    }

    public void continuarComprando() {
        esperarVisible(botonContinuarComprando).click();
    }
}