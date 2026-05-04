package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.CarLoanPage;
import utils.EMICalculator;
import utils.HelperUtils;
import utils.configReader;

public class CarLoanSteps {

    private CarLoanPage carLoanPage;
    private HelperUtils helper;

    private int expectedEMI;
    private int actualEMI;
    private static final int TOLERANCE = 100;

    public CarLoanSteps() {
        // Get driver and wait from Hooks (already set up)
        this.carLoanPage = new CarLoanPage(Hooks.driver, Hooks.wait);
        this.helper      = new HelperUtils(Hooks.driver, Hooks.wait);
    }

    @Given("user is on the EMI calculator website")
    public void userIsOnEMICalculatorWebsite() {
        // Already done in Hooks @Before — just verify URL
        String currentUrl = Hooks.driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
    }

    @And("user navigates to Car Loan section")
    public void userNavigatesToCarLoanSection() {
        System.out.println("On Car Loan section.");
    }

    @When("user enters loan amount as {string}")
    public void userEntersLoanAmount(String amount) {
        carLoanPage.enterLoanAmount(amount);
    }

    @And("user enters interest rate as {string}")
    public void userEntersInterestRate(String rate) {
        carLoanPage.enterInterestRate(rate);
    }

    @And("user enters tenure as {string} years")
    public void userEntersTenure(String tenure) {
        carLoanPage.enterTenure(tenure);

        String amount = carLoanPage.getTotalAmount().replaceAll("[^0-9]", "");
        String rate   = carLoanPage.getTotalInterest().replaceAll("[^0-9.]", "");

        expectedEMI = EMICalculator.calculateEMI(
                Double.parseDouble(amount),
                Double.parseDouble(rate),
                Integer.parseInt(tenure)
        );
    }

    @Then("the displayed EMI should match the calculated EMI for {string}")
    public void theDisplayedEMIShouldMatch(String testName) {

        String emiText = carLoanPage.getEMI();
        actualEMI = Integer.parseInt(emiText.replaceAll("[^0-9]", "").split("\\.")[0]);

        System.out.println("Test: " + testName);
        System.out.println("Expected EMI: " + expectedEMI);
        System.out.println("Actual EMI: " + actualEMI);

        boolean isMatch = Math.abs(actualEMI - expectedEMI) <= TOLERANCE;

//        helper.takeScreenshot(testName);

        Assert.assertTrue(isMatch,
                testName + " failed. Expected: " + expectedEMI + ", Actual: " + actualEMI);

        System.out.println("Status: PASS");
    }
}