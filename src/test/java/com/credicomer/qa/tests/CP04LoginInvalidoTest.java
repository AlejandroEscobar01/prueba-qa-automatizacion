package com.credicomer.qa.tests;

import com.credicomer.qa.pages.HeaderComponent;
import com.credicomer.qa.pages.LoginPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CP04LoginInvalidoTest extends BaseTest {

    private static final String CORREO_INEXISTENTE = "qa.candidato@example.com";
    private static final String CONTRASENA_CUALQUIERA = "ClaveInvalida123";

    @Test
    public void cp04_inicioSesionConCredencialesInvalidas() {
        HeaderComponent header = new HeaderComponent(driver);
        LoginPage loginPage = new LoginPage(driver);

        // Paso 1: ingresar a la pantalla de inicio de sesión
        header.irAIniciarSesion();
        assertTrue(loginPage.formularioVisible(), "No se muestra el formulario de inicio de sesión");
        assertTrue(loginPage.campoCorreoVisible(), "No se muestra el campo de correo");
        assertTrue(loginPage.campoContrasenaVisible(), "No se muestra el campo de contraseña");

        // Paso 2: ingresar un correo inexistente con cualquier contraseña y enviar el formulario
        loginPage.iniciarSesion(CORREO_INEXISTENTE, CONTRASENA_CUALQUIERA);
        assertTrue(loginPage.seMuestraMensajeError(), "No se muestra el mensaje de error de autenticación");
        String mensaje = loginPage.obtenerMensajeError();
        System.out.println("Mensaje de error mostrado: " + mensaje);
        assertTrue(mensaje.contains("Authentication failed"),
                "El mensaje no corresponde a error de autenticación. Mensaje actual: " + mensaje);

        // Paso 3: verificar que el usuario sigue sin autenticar
        assertFalse(header.usuarioAutenticado(), "El usuario quedó autenticado con credenciales inválidas");
        assertTrue(header.opcionIniciarSesionVisible(), "La opción de iniciar sesión ya no es visible");
        System.out.println("Opción visible en el encabezado: " + header.obtenerTextoOpcionIniciarSesion());
    }
}
