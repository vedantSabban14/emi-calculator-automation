package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {"stepDefinitions", "hooks"},   // ← include hooks package
        plugin   = {
                "pretty",
                "html:test-output/cucumber-reports/report.html",
                "json:test-output/cucumber-reports/report.json"
        },
        monochrome = true
)
public class testRunner extends AbstractTestNGCucumberTests {

}