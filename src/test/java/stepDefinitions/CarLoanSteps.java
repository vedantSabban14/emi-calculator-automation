package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.testng.Assert;
import pages.CarLoanPage;
import utils.EMICalculator;
import utils.HelperUtils;

public class CarLoanSteps {

    private CarLoanPage carLoanPage;
    private HelperUtils helper;

    private String loanAmount;
    private String interestRate;
    private String tenure;

    private int expectedEMI;
    private int actualEMI;
    private static final int TOLERANCE = 100;

    public CarLoanSteps() {
        this.carLoanPage = new CarLoanPage(Hooks.driver, Hooks.wait);
        this.helper      = new HelperUtils(Hooks.driver, Hooks.wait);
    }

    @Given("user is on the EMI calculator website")
    public void userIsOnEMICalculatorWebsite() {
        System.out.println("Current URL: " + Hooks.driver.getCurrentUrl());
    }

    @And("user navigates to Car Loan section")
    public void userNavigatesToCarLoanSection() {
        System.out.println("On Car Loan section.");
    }

    @When("user enters loan amount as {string}")
    public void userEntersLoanAmount(String amount) {
        this.loanAmount = amount;
        carLoanPage.enterLoanAmount(amount);
    }

    @And("user enters interest rate as {string}")
    public void userEntersInterestRate(String rate) {
        this.interestRate = rate;
        carLoanPage.enterInterestRate(rate);
    }

    @And("user enters tenure as {string} years")
    public void userEntersTenure(String tenure) {
        this.tenure = tenure;
        carLoanPage.enterTenure(tenure);

        expectedEMI = EMICalculator.calculateEMI(
                Double.parseDouble(loanAmount),
                Double.parseDouble(interestRate),
                Integer.parseInt(tenure)
        );

        System.out.println("Expected EMI calculated: " + expectedEMI);
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

    @Then("the first month principal and interest should be displayed")
    public void firstMonthValuesDisplayed() {
        String principal = carLoanPage.getMonth1Principal();
        String interest  = carLoanPage.getMonth1Interest();
        System.out.println("Month 1 Principal: " + principal);
        System.out.println("Month 1 Interest: " + interest);
        Assert.assertFalse(principal.isEmpty(), "Month 1 Principal should be displayed");
        Assert.assertFalse(interest.isEmpty(), "Month 1 Interest should be displayed");
    }

    @Then("the total interest payable should be displayed")
    public void totalInterestDisplayed() {
        String totalInterest = carLoanPage.getTotalInterest();
        System.out.println("Total Interest: " + totalInterest);
        Assert.assertFalse(totalInterest.isEmpty(), "Total Interest should be displayed");
    }

    @Then("the total payment should equal loan amount plus total interest")
    public void totalPaymentEqualsLoanPlusInterest() {
        int loanAmt       = clean(this.loanAmount);
        int totalInterest = clean(carLoanPage.getTotalInterest());
        int totalAmount   = clean(carLoanPage.getTotalAmount());

        int expected = loanAmt + totalInterest;
        int diff = Math.abs(totalAmount - expected);

        System.out.println("Loan + Interest: " + expected);
        System.out.println("Total Payment:   " + totalAmount);

        Assert.assertTrue(diff <= 100,
                "Total Payment should equal Loan Amount + Total Interest");
    }

    @Then("the loan amount field should be visible")
    public void loanAmountFieldVisible() {
        Assert.assertTrue(
                Hooks.driver.findElement(By.id("loanamount")).isDisplayed(),
                "Loan amount field should be visible");
    }

    @Then("the interest rate field should be visible")
    public void interestFieldVisible() {
        Assert.assertTrue(
                Hooks.driver.findElement(By.id("loaninterest")).isDisplayed(),
                "Interest rate field should be visible");
    }

    @Then("the tenure field should be visible")
    public void tenureFieldVisible() {
        Assert.assertTrue(
                Hooks.driver.findElement(By.id("loanterm")).isDisplayed(),
                "Tenure field should be visible");
    }

    @Then("the EMI result should be displayed")
    public void emiResultDisplayed() {
        String emi = carLoanPage.getEMI();
        Assert.assertFalse(emi.isEmpty(), "EMI result should be displayed");
    }

    private int clean(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", "").split("\\.")[0]);
    }
}