package com.example.tests.pages;

import com.example.tests.base.AbstractWindow;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BoardPage extends AbstractWindow {
    private Map<String, Map<String, Integer>> kutular;

    public BoardPage(AppiumDriver driver, String title) {
        super(driver, title);
        this.kutular = loadKutuKoordinatlari();
    }

    public BoardPage(AppiumDriver driver) {
        this(driver, "");
    }

    // Koordinatları bir dosyadan veya başka bir kaynaktan oku
    private Map<String, Map<String, Integer>> loadKutuKoordinatlari() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileReader fileReader = new FileReader("kutu_koordinatlari.json")) {
            return objectMapper.readValue(fileReader, new TypeReference<Map<String, Map<String, Integer>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void kutuyaDokun(String kutuAdi) {
        Map<String, Integer> kutu = kutular.get(kutuAdi);
        int x = kutu.get("x");
        int y = kutu.get("y");

        // W3C WebDriver protokolü ile dokunma işlemi
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tap));
    }
}
