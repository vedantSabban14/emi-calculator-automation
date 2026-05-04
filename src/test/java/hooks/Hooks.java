package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.configReader;

import java.time.Duration;

public class Hooks {

    // Make driver and wait STATIC so step definition files can access them
    public static WebDriver driver;
    public static WebDriverWait wait;

    // ===== Runs BEFORE every scenario =====
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("\n========== Starting Scenario: " + scenario.getName() + " ==========");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait   = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(configReader.getURL());

        System.out.println("Browser launched and navigated to: " + configReader.getURL());
    }

    // ===== Runs AFTER every scenario =====
    @After
    public void tearDown(Scenario scenario) {

        // If scenario failed, attach a screenshot to the report
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
            System.out.println("Scenario FAILED — screenshot attached to report.");
        } else {
            System.out.println("Scenario PASSED.");
        }

        if (driver != null) {
            driver.quit();
        }

        System.out.println("========== End of Scenario: " + scenario.getName() + " ==========\n");
    }
}