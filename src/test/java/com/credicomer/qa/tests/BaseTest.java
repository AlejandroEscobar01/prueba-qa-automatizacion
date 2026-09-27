package com.credicomer.qa.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BaseTest {

    protected WebDriver driver;
    protected final String URL_BASE = "https://teststore.automationtesting.co.uk/index.php";

    private static final String CARPETA_EVIDENCIA = "evidencia";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL_BASE);
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (driver != null) {
            guardarEvidencia(testInfo);
            driver.quit(); // comentado a propósito para revisar el resultado visualmente
        }
    }

    /** Guarda una captura del estado final del navegador en la carpeta "evidencia". */
    private void guardarEvidencia(TestInfo testInfo) {
        try {
            Path carpeta = Paths.get(CARPETA_EVIDENCIA);
            Files.createDirectories(carpeta);
            String nombreArchivo = testInfo.getTestClass().map(Class::getSimpleName).orElse("prueba") + ".png";
            File captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destino = carpeta.resolve(nombreArchivo);
            Files.copy(captura.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Evidencia guardada en: " + destino.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            System.out.println("No se pudo guardar la evidencia: " + e.getMessage());
        }
    }
}