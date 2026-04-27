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
    private By homeValueField      = By.id("homevalue");
    private By downPaymentField    = By.id("downpayment");
    private By downPaymentPctBtn   = By.xpath("//span[@id='downpaymenttypebuttons']//button[text()='%']");
    private By loanInsuranceField  = By.id("loaninsurance");
    private By interestRateField   = By.id("homeloaninterest");
    private By tenureField         = By.id("loanterm");
    private By tenureYearBtn       = By.xpath("//span[@id='loantermtypebuttons']//button[text()='Yr']");
    private By loanFeesField       = By.id("loanfees");
    private By loanFeesPctBtn      = By.xpath("//span[@id='loanfeestypebuttons']//button[text()='%']");

    // --- Homeowner Expense Fields ---
    private By oneTimeExpensesField   = By.id("onetimeexpenses");
    private By propertyTaxesField     = By.id("propertytaxes");
    private By homeInsuranceField     = By.id("homeinsurance");
    private By maintenanceField       = By.id("maintenance");

    // --- Result Fields ---
    private By loanAmountResult       = By.id("loanamount");
    private By emiResult              = By.id("emi");
    private By totalPaymentResult     = By.id("totalpayable");

    // --- Year-on-Year Table ---
    private By yearlyTable            = By.id("emipaymentyearlysummary");
    private By yearlyTableRows        = By.xpath("//table[@id='emipaymentyearlysummary']/tbody/tr");

    // --- Constructor ---
    public HomeLoanPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.helper = new HelperUtils(driver, wait);
    }

    // --- Navigate to Home Loan page from main menu ---
    public void navigateToHomeLoan() {
        // Some layouts show menu directly without click, others need menu open first
        try {
            helper.clickElement(menuButton);
        } catch (Exception ignored) {
            // menu already visible — skip
        }
        helper.clickElement(homeLoanMenuLink);
        System.out.println("Navigated to Home Loan EMI Calculator.");
    }

    // --- Individual input methods ---

    public void enterHomeValue(String value) {
        WebElement field = driver.findElement(homeValueField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        System.out.println("Home value entered : " + value);
    }

    public void enterDownPayment(String percent) {
        // Make sure % toggle is selected (not ₹)
        helper.clickElement(downPaymentPctBtn);

        WebElement field = driver.findElement(downPaymentField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(percent);
        field.sendKeys(Keys.TAB);
        System.out.println("Down payment entered : " + percent + "%");
    }

    public void enterLoanInsurance(String value) {
        WebElement field = driver.findElement(loanInsuranceField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
        System.out.println("Loan insurance entered : " + value);
    }

    public void enterInterestRate(String rate) {
        WebElement field = driver.findElement(interestRateField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(rate);
        field.sendKeys(Keys.TAB);
        System.out.println("Interest rate entered : " + rate);
    }

    public void enterTenure(String tenure) {
        // Make sure 'Yr' toggle is selected
        helper.clickElement(tenureYearBtn);

        WebElement field = driver.findElement(tenureField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(tenure);
        field.sendKeys(Keys.TAB);
        System.out.println("Tenure entered : " + tenure + " Year");
    }

    public void enterLoanFees(String fees) {
        helper.clickElement(loanFeesPctBtn);

        WebElement field = driver.findElement(loanFeesField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(fees);
        field.sendKeys(Keys.TAB);
        System.out.println("Loan fees entered : " + fees + "%");
    }

    public void enterOneTimeExpenses(String value) {
        WebElement field = driver.findElement(oneTimeExpensesField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
    }

    public void enterPropertyTaxes(String value) {
        WebElement field = driver.findElement(propertyTaxesField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
    }

    public void enterHomeInsurance(String value) {
        WebElement field = driver.findElement(homeInsuranceField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
    }

    public void enterMaintenance(String value) {
        WebElement field = driver.findElement(maintenanceField);
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
        field.sendKeys(Keys.TAB);
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

    public String getLoanAmount() {
        return helper.getText(loanAmountResult);
    }

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