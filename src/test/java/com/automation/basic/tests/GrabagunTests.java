package com.automation.basic.tests;

import au.com.bytecode.opencsv.CSVReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by tbranstiter on 12/2/2016.
 */
public class GrabagunTests {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "resources/drivers/chrome/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void openPage() {
        driver.get("http://www.grabagun.com/giveaway");
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    @DataProvider(name = "users")
    public Object[][] provider() throws InterruptedException {
        Object[][] data = null;
        String csvFile = "src/main/resources/data/users.csv";
        CSVReader reader = null;
        String[] nextLine;

        int i = 0;
        try {
            data = new Object[6][7];
            reader = new CSVReader(new FileReader(csvFile));
            while ((nextLine = reader.readNext()) != null) {
                data[i][0] = nextLine[0];
                data[i][1] = nextLine[1];
                data[i][2] = nextLine[2];
                data[i][3] = nextLine[3];
                data[i][4] = nextLine[4];
                data[i][5] = nextLine[5];
                data[i][6] = nextLine[6];
                i++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Test(dataProvider = "users")
    public void testRegisterForGiveaway(String firstName, String lastName, String email, String address, String city, String state, String zipcode) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 60);
        String url = "http://www.grabagun.com/giveaway";
        boolean flag = true;

        Actions builder = new Actions(driver);

        while(flag) {
            builder.moveToElement(driver.findElement(By.id("home-slider")), 10, 25).click().build().perform();
            wait.until(ExpectedConditions.titleIs("Giveaway"));
            driver.findElement(By.id("order_shipping_address_shipping_firstname")).sendKeys(firstName);
            driver.findElement(By.id("order_shipping_address_shipping_lastname")).sendKeys(lastName);
            driver.findElement(By.id("order_shipping_address_shipping_email")).sendKeys(email);
            driver.findElement(By.id("order_shipping_address_shipping_street")).sendKeys(address);
            driver.findElement(By.id("order_shipping_address_shipping_city")).sendKeys(city);
            Select dropdown = new Select(driver.findElement(By.id("order_shipping_address_shipping_state")));
            dropdown.selectByValue(state);
            driver.findElement(By.id("order_shipping_address_shipping_postcode")).sendKeys(zipcode);
            driver.findElement(By.id("order_shipping_address_shipping_terms")).click();

            driver.switchTo().frame(driver.findElement(By.name("undefined")));

            Boolean isChecked = Boolean.valueOf(driver.findElement(By.id("recaptcha-anchor")).getAttribute("aria-checked"));

            while(!isChecked) {
                isChecked = Boolean.valueOf(driver.findElement(By.id("recaptcha-anchor")).getAttribute("aria-checked"));
            }

            driver.switchTo().defaultContent();

            driver.findElements(By.className("button")).get(1).click();

            wait.until(ExpectedConditions.titleIs("Giveaway Success"));

            driver.get(url);
        }
    }

}
