package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

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
public class CucumberRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios()
    {
        return super.scenarios();
    }
}