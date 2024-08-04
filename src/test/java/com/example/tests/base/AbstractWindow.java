package com.example.tests.base;

import io.appium.java_client.AppiumDriver;


public abstract class AbstractWindow {
    protected AppiumDriver driver;
    protected String title;

    public AbstractWindow(AppiumDriver driver, String title) {
        this.driver = driver;
        this.title = title;
    }
}
