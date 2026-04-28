package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utils.configReader;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String URL = "https://emicalculator.net/";

//    Launch the Browser
    @Parameters("browser")
    @BeforeMethod
    public void setUp(String browser) {
        switch (browser.toLowerCase()){
            case "chrome": driver = new ChromeDriver();
            break;
            case "edge": driver = new EdgeDriver();
            break;
            default:
                throw new IllegalArgumentException("Invalid browser name: " + browser);
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));
        driver.get(configReader.getURL());
        System.out.println("Browser launched!");
    }

//    Close the Browser
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed!");
        }
    }
}