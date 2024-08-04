package com.example.tests.pages;

import com.example.tests.base.AbstractWindow;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

public class MenuPage extends AbstractWindow {

    public MenuPage(AppiumDriver driver, String title) {
        super(driver, title);
    }

    public MenuPage(AppiumDriver driver) {
        this(driver, "");
    }

    public void uclukTahtayaTikla() {
        driver.findElement(AppiumBy.accessibilityId("3x3")).click();
    }

    public void beslikTahtayaTikla() {
        driver.findElement(AppiumBy.accessibilityId("5x5")).click();
    }

    public void singlePlayerTikla() {
        driver.findElement(AppiumBy.accessibilityId("Single Player")).click();
    }

    public void carpiyatikla(int x, int y) {
        performClickAtCoordinates(x, y);
    }

    public void startGameTikla(int x, int y) {
        performClickAtCoordinates(x, y);
    }

    private void performClickAtCoordinates(int x, int y) {
        // W3C WebDriver protokolü ile dokunma işlemi
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tap));
    }
}
