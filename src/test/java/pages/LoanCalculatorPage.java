package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.HelperUtils;

public class LoanCalculatorPage {

    private WebDriver driver;
    private HelperUtils helper;
    private Actions actions;

    //   SLIDER STEP SIZES
    private static final double STEP_LOAN_AMOUNT  = 100000;
    private static final double STEP_EMI          = 500;
    private static final double STEP_INTEREST     = 0.25;
    private static final double STEP_TENURE_YEAR  = 0.5;
    private static final double STEP_FEES         = 500;

    //   MENU NAVIGATION
    private By menuButton          = By.xpath("//button[@class='navbar-toggler']");
    private By loanCalculatorMenu  = By.xpath("//a[@title='Loan Calculator']");

    //   TAB BUTTONS — actual IDs from page source
    private By emiCalcTab          = By.xpath("//li[@id='emi-calc']/a");
    private By loanAmountCalcTab   = By.xpath("//li[@id='loan-amount-calc']/a");
    private By loanTenureCalcTab   = By.xpath("//li[@id='loan-tenure-calc']/a");
    private By interestRateCalcTab = By.xpath("//li[@id='interest-rate-calc']/a");

    //   COMMON INPUT FIELDS (shared across all tabs)
    private By loanAmountField   = By.id("loanamount");
    private By emiField          = By.id("loanemi");
    private By interestField     = By.id("loaninterest");
    private By tenureField       = By.id("loanterm");
    private By feesField         = By.id("loanfees");

    //   COMMON SLIDERS
    private By loanAmountSlider  = By.cssSelector("#loanamountslider .ui-slider-handle");
    private By emiSlider         = By.cssSelector("#loanemislider .ui-slider-handle");
    private By interestSlider    = By.cssSelector("#loaninterestslider .ui-slider-handle");
    private By tenureSlider      = By.cssSelector("#loantermslider .ui-slider-handle");
    private By feesSlider        = By.cssSelector("#loanfeesslider .ui-slider-handle");

    //   TENURE TOGGLE (Yr / Mo)
    private By tenureYearLabel   = By.xpath("//label[input[@id='loanyears']]");
    private By tenureMonthLabel  = By.xpath("//label[input[@id='loanmonths']]");
    private By tenureYearInput   = By.id("loanyears");
    private By tenureMonthInput  = By.id("loanmonths");

    //   EMI SCHEME (Advance / Arrears)
    private By emiInAdvanceLabel = By.xpath("//label[input[@id='emiadvance']]");
    private By emiInArrearsLabel = By.xpath("//label[input[@id='emiarrears']]");

    //   RESULT FIELDS
    private By result_principalLoanAmount = By.cssSelector("#loansummary-loanamount p span");
    private By result_emi                 = By.cssSelector("#loansummary-emi p span");
    private By result_tenure              = By.cssSelector("#loansummary-tenure p span");
    private By result_interestRate        = By.cssSelector("#loansummary-interestrate p span");
    private By result_apr                 = By.cssSelector("#loansummary-apr p span");
    private By result_totalInterest       = By.cssSelector("#loansummary-totalinterest p span");
    private By result_totalAmount         = By.cssSelector("#loansummary-totalamount p span");

    public LoanCalculatorPage(WebDriver driver, WebDriverWait wait) {
        this.driver  = driver;
        this.helper  = new HelperUtils(driver, wait);
        this.actions = new Actions(driver);
    }

    public void navigateToLoanCalculator() {
        try {
            helper.clickElement(menuButton);
        } catch (Exception ignored) {}

        helper.clickElement(loanCalculatorMenu);
        System.out.println("Navigated to Loan Calculator page.");

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    public void switchToTab(String tabName) {
        switch (tabName.toLowerCase()) {
            case "emi":
                helper.clickElement(emiCalcTab);
                System.out.println("Switched to EMI Calculator tab.");
                break;
            case "loanamount":
                helper.clickElement(loanAmountCalcTab);
                System.out.println("Switched to Loan Amount Calculator tab.");
                break;
            case "loantenure":
                helper.clickElement(loanTenureCalcTab);
                System.out.println("Switched to Loan Tenure Calculator tab.");
                break;
            case "interestrate":
                helper.clickElement(interestRateCalcTab);
                System.out.println("Switched to Interest Rate Calculator tab.");
                break;
            default:
                throw new IllegalArgumentException("Invalid tab: " + tabName);
        }
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    public void moveSliderToValue(By sliderLocator, By textFieldLocator,
                                  double targetValue, double stepSize) {

        WebElement slider = driver.findElement(sliderLocator);
        slider.click();

        double currentValue = readFieldValue(textFieldLocator);
        System.out.println("Current: " + currentValue + " | Target: " + targetValue
                + " | Step: " + stepSize);

        double diff = Math.abs(targetValue - currentValue);
        int steps = (int) Math.round(diff / stepSize);

        int maxSteps = 500;
        if (steps > maxSteps) {
            System.out.println("WARNING: Too many steps (" + steps + "), capping at " + maxSteps);
            steps = maxSteps;
        }

        Keys arrow = (targetValue > currentValue) ? Keys.ARROW_RIGHT : Keys.ARROW_LEFT;
        for (int i = 0; i < steps; i++) {
            slider.sendKeys(arrow);
        }

        double finalValue = readFieldValue(textFieldLocator);
        System.out.println("Slider final value: " + finalValue);
    }

    private double readFieldValue(By locator) {
        String text = driver.findElement(locator).getAttribute("value");
        text = text.replaceAll("[^0-9.]", "");
        if (text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }

    public void setLoanAmount(double value) {
        moveSliderToValue(loanAmountSlider, loanAmountField, value, STEP_LOAN_AMOUNT);
    }

    public void setEMI(double value) {
        moveSliderToValue(emiSlider, emiField, value, STEP_EMI);
    }

    public void setInterestRate(double value) {
        moveSliderToValue(interestSlider, interestField, value, STEP_INTEREST);
    }

    public void setTenureInYears(double value) {
        helper.jsClick(tenureYearLabel);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        moveSliderToValue(tenureSlider, tenureField, value, STEP_TENURE_YEAR);
    }

    public void setFees(double value) {
        moveSliderToValue(feesSlider, feesField, value, STEP_FEES);
    }

    public void toggleToYear() {
        helper.jsClick(tenureYearLabel);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    public void toggleToMonth() {
        helper.jsClick(tenureMonthLabel);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    public boolean isYearSelected() {
        return driver.findElement(tenureYearInput).isSelected();
    }

    public boolean isMonthSelected() {
        return driver.findElement(tenureMonthInput).isSelected();
    }

    public void selectEMIInAdvance() {
        helper.jsClick(emiInAdvanceLabel);
    }

    public void selectEMIInArrears() {
        helper.jsClick(emiInArrearsLabel);
    }

    public String getLoanAmountFieldValue() { return getFieldValue(loanAmountField); }
    public String getEMIFieldValue()        { return getFieldValue(emiField); }
    public String getInterestFieldValue()   { return getFieldValue(interestField); }
    public String getTenureFieldValue()     { return getFieldValue(tenureField); }
    public String getFeesFieldValue()       { return getFieldValue(feesField); }

    public String getFieldValue(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    public String getResultPrincipalLoanAmount() {
        return helper.getText(result_principalLoanAmount);
    }

    public String getResultEMI() {
        return helper.getText(result_emi);
    }

    public String getResultTenure() {
        return helper.getText(result_tenure);
    }

    public String getResultInterestRate() {
        return helper.getText(result_interestRate);
    }

    public String getResultAPR() {
        return helper.getText(result_apr);
    }

    public String getResultTotalInterest() {
        return helper.getText(result_totalInterest);
    }

    public String getResultTotalAmount() {
        return helper.getText(result_totalAmount);
    }

    public boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public By getLoanAmountField()  { return loanAmountField; }
    public By getEMIField()         { return emiField; }
    public By getInterestField()    { return interestField; }
    public By getTenureField()      { return tenureField; }
    public By getFeesField()        { return feesField; }
    public By getLoanAmountSlider() { return loanAmountSlider; }
    public By getEMISlider()        { return emiSlider; }
    public By getInterestSlider()   { return interestSlider; }
    public By getTenureSlider()     { return tenureSlider; }
    public By getFeesSlider()       { return feesSlider; }
}