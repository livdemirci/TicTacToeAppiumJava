package com.example.tests.tests;

import com.example.tests.pages.BoardPage;
import com.example.tests.pages.MenuPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TicTacToeTest {
    private static AndroidDriver driver;
    private static MenuPage menuPage;
    private static BoardPage boardPage;


        @BeforeClass
        public static void setUp() throws IOException {
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("emulator-5554");
            options.setAppPackage("com.chisw.TicTacXO");
            options.setAppActivity("com.example.tic_tac_toe.MainActivity");

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
            FileUtils.writeStringToFile(new File("session_id.txt"), driver.getSessionId().toString(), "UTF-8");
            System.out.println(driver.getSessionId());

            menuPage = new MenuPage(driver);
            boardPage = new BoardPage(driver);
        }

        @AfterClass
        public static void tearDown () {
            if (driver != null) {
                driver.quit();
            }
        }

        private boolean isBlankCell (BufferedImage img){
            int height = img.getHeight();
            int width = img.getWidth();
            Set<Integer> colorCodes = new HashSet<>();

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int color = img.getRGB(j, i);
                    colorCodes.add(color);
                    if (colorCodes.size() >= 1000) {
                        return false;
                    }
                }
            }

            return colorCodes.size() < 1000;
        }

        private List<String> captureAndGetEmptyCells () throws IOException {
            File screenshot = driver.getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);
            BufferedImage croppedImg = fullImg.getSubimage(92, 960, 1160, 1140);
            ImageIO.write(croppedImg, "png", new File("screenshot.png"));

            BufferedImage jpegImg = ImageIO.read(new File("screenshot.png"));

            int width = jpegImg.getWidth() / 3;
            int height = jpegImg.getHeight() / 3;

            File dir = new File("cropped_images");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            List<String> emptyCells = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Integer>> jsonData = objectMapper.readValue(
                    new FileInputStream("kutu_koordinatlari.json"),
                    new TypeReference<Map<String, Map<String, Integer>>>() {
                    }
            );

            int index = 0;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    BufferedImage cropped = jpegImg.getSubimage(col * width, row * height, width, height);
                    File file = new File(dir, "output_" + row + "_" + col + ".png");
                    ImageIO.write(cropped, "png", file);
                    System.out.println("Cropped image saved at: " + file.getAbsolutePath());

                    if (isBlankCell(cropped)) {
                        String key = (String) jsonData.keySet().toArray()[index];
                        emptyCells.add(key);
                    }
                    index++;
                }
            }

            return emptyCells;
        }

        @Test
        public void playsGameOfTicTacToe () throws Exception {
            Thread.sleep(2000);
            menuPage.uclukTahtayaTikla();
            Thread.sleep(1000);
            menuPage.singlePlayerTikla();
            Set<String> contexts = driver.getContextHandles();
            System.out.println(contexts);

            menuPage.carpiyatikla(370, 1400);
            menuPage.startGameTikla(650, 2215);

            boardPage.kutuyaDokun("dorduncu_kutu");
            Thread.sleep(3000);

            // Boş hücreleri başlangıçta al
            List<String> emptyCells = captureAndGetEmptyCells();

            // 'Player 1' elementinin görüntülendiği sürece döngü devam etsin
            while (true) {
                try {
                    WebElement player1Element = driver.findElement(AppiumBy.accessibilityId("Player 1"));
                    if (!player1Element.isDisplayed()) {
                        break;
                    }

                    // Rastgele bir boş hücre seç
                    String emptyCell = emptyCells.get(new Random().nextInt(emptyCells.size()));
                    System.out.println("Next move: empty cell selected: " + emptyCell);

                    // Seçilen hücreye dokun
                    boardPage.kutuyaDokun(emptyCell);
                    Thread.sleep(3000);

                    // Boş hücreleri güncelle
                    emptyCells = captureAndGetEmptyCells();

                    // Boş hücre kalmadıysa döngüden çık
                    if (emptyCells.isEmpty()) {
                        break;
                    }
                } catch (NoSuchElementException e) {
                    // 'Player 1' elementi bulunamazsa döngüyü sessizce sonlandır
                    break;
                }
            }

            System.out.println("No more empty cells left or reached 8 moves.");

            // 'Play again' butonunun görüntülenip görüntülenmediğini kontrol et
            WebElement playAgainButton = driver.findElement(AppiumBy.accessibilityId("Play again"));
            assertTrue(playAgainButton.isDisplayed());
        }
    }
