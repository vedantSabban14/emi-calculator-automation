package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.HelperUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeLoanPage {

    private WebDriver driver;
    private HelperUtils helper;

    // --- Menu Navigation ---
    private By menuButton          = By.xpath("//button[@class='navbar-toggler']");
    private By homeLoanMenuLink    = By.xpath("//a[contains(text(),'Home Loan EMI Calculator')]");

    // --- Home Loan Input Fields ---
    private By homeValueField      = By.id("homeprice");
    private By downPaymentField    = By.id("downpayment");
    private By downPaymentPctBtn   = By.xpath("//label//input[@id='downpaymentpercentage']]");
    private By loanInsuranceField  = By.id("homeloaninsuranceamount");
    private By interestRateField   = By.id("homeloaninterest");
    private By tenureField         = By.id("homeloanterm");
    private By tenureYearBtn       = By.xpath("//label//input[@id='homeloanyears']");
    private By loanFeesField       = By.id("loanfees");
    private By loanFeesPctBtn      = By.xpath("//input[@id='loanfeespercentage']");

    // --- Homeowner Expense Fields ---
    private By oneTimeExpensesField   = By.id("onetimeexpenses");
    private By propertyTaxesField     = By.id("propertytaxes");
    private By homeInsuranceField     = By.id("homeinsurance");
    private By maintenanceField       = By.id("maintenanceexpenses");

    // --- Result Fields ---
    private By emiResult              = By.id("monthlyprincipalandinterestdef");
    private By totalPaymentResult     = By.id("monthlypayment");

    // --- Year-on-Year Table ---
    private By yearlyTable            = By.xpath("//div[@class='homeloanemicalculatorcontainer']");
    private By yearlyTableRows        = By.xpath("//div[@id='paymentschedule']/table/tbody/tr[@class='row no-margin yearlypaymentdetails']");

    // --- Constructor ---
    public HomeLoanPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.helper = new HelperUtils(driver, wait);
    }

    // --- Navigate to Home Loan page from main menu ---
    public void navigateToHomeLoan() {
        // Some layouts show menu directly without click, others need menu open first
        helper.clickElement(menuButton);
        helper.clickElement(homeLoanMenuLink);
        System.out.println("Navigated to Home Loan EMI Calculator.");
    }

    // --- Individual input methods ---
    public void enterHomeValue(String value) {
       helper.typeInBox(homeValueField, value);
        System.out.println("Home value entered : " + value);
    }

    public void enterDownPayment(String percent) {
        // Make sure % toggle is selected (not ₹)
        helper.clickElement(downPaymentPctBtn);
        helper.typeInBox(downPaymentField, percent);
        System.out.println("Down payment entered : " + percent + "%");
    }

    public void enterLoanInsurance(String value) {
        helper.typeInBox(loanInsuranceField, value);
        System.out.println("Loan insurance entered : " + value);
    }

    public void enterInterestRate(String rate) {
        helper.typeInBox(interestRateField, rate);
        System.out.println("Interest rate entered : " + rate);
    }

    public void enterTenure(String tenure) {
        // Make sure 'Yr' toggle is selected
        helper.clickElement(tenureYearBtn);
        helper.typeInBox(tenureField, tenure);
        System.out.println("Tenure entered : " + tenure + " Year");
    }

    public void enterLoanFees(String fees) {
        helper.clickElement(loanFeesPctBtn);
        helper.typeInBox(loanFeesField, fees);
        System.out.println("Loan fees entered : " + fees + "%");
    }

    public void enterOneTimeExpenses(String value) {
        helper.typeInBox(oneTimeExpensesField, value);
    }

    public void enterPropertyTaxes(String value) {
        helper.typeInBox(propertyTaxesField, value);
    }

    public void enterHomeInsurance(String value) {
        helper.typeInBox(homeInsuranceField, value);
    }

    public void enterMaintenance(String value) {
        helper.typeInBox(maintenanceField, value);
    }

    // --- Combined: fill entire form with one method call ---
    public void fillHomeLoanForm(String homeValue, String downPayment, String loanInsurance,
                                 String interestRate, String tenure, String loanFees,
                                 String oneTimeExpenses, String propertyTaxes,
                                 String homeInsurance, String maintenance) {

        enterHomeValue(homeValue);
        enterDownPayment(downPayment);
        enterLoanInsurance(loanInsurance);
        enterInterestRate(interestRate);
        enterTenure(tenure);
        enterLoanFees(loanFees);
        enterOneTimeExpenses(oneTimeExpenses);
        enterPropertyTaxes(propertyTaxes);
        enterHomeInsurance(homeInsurance);
        enterMaintenance(maintenance);

        // Wait briefly for site's JS to recalculate
        helper.scrollDown(200);
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
    }

    // --- Getters for result values ---

    public String getEMI() {
        return helper.getText(emiResult);
    }

    public String getTotalPayment() {
        return helper.getText(totalPaymentResult);
    }

    // --- Read year-on-year table for Excel export ---
    public List<String[]> getYearlyTableData() {
        helper.scrollToElement(yearlyTable);

        // Wait for table to be fully rendered
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        List<WebElement> rows = driver.findElements(yearlyTableRows);
        List<String[]> data = new ArrayList<>();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));

            // Skip empty rows
            if (cells.isEmpty()) continue;

            String[] rowData = new String[cells.size()];
            for (int i = 0; i < cells.size(); i++) {
                rowData[i] = cells.get(i).getText().trim();
            }
            data.add(rowData);
        }

        System.out.println("Yearly table extracted with " + data.size() + " rows.");
        return data;
    }
}