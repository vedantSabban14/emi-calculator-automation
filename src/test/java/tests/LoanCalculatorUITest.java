package tests;
import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoanCalculatorPage;
import utils.HelperUtils;
public class LoanCalculatorUITest extends BaseTest {
    LoanCalculatorPage loanCalcPage;
    HelperUtils helper;

    @BeforeMethod
    public void initPages() {
        loanCalcPage = new LoanCalculatorPage(driver, wait);
        helper       = new HelperUtils(driver, wait);
        // Navigate to Loan Calculator before every test
        loanCalcPage.navigateToLoanCalculator();
    }

    //   TEST 1 — Verify all 4 tabs are present and clickable

    @Test(priority = 1)
    public void verifyAllTabsArePresent() {
        System.out.println("\n=========== TEST 1: Verify all tabs ===========");
        // Switch through each tab and confirm no errors
        loanCalcPage.switchToTab("emi");
        loanCalcPage.switchToTab("loanamount");
        loanCalcPage.switchToTab("loantenure");
        loanCalcPage.switchToTab("interestrate");
        System.out.println("PASS — All 4 tabs are clickable.");
    }

    //   TEST 2 — Verify input fields are visible on EMI tab
    @Test(priority = 2)
    public void verifyEMITabFieldsVisible() {
        System.out.println("\n=========== TEST 2: EMI tab fields visible ===========");
        loanCalcPage.switchToTab("emi");
        Assert.assertTrue(loanCalcPage.isElementDisplayed(loanCalcPage.getLoanAmountField()),
                "Loan Amount field should be visible on EMI tab");
        Assert.assertTrue(loanCalcPage.isElementDisplayed(loanCalcPage.getInterestField()),
                "Interest Rate field should be visible on EMI tab");
        Assert.assertTrue(loanCalcPage.isElementDisplayed(loanCalcPage.getTenureField()),
                "Tenure field should be visible on EMI tab");
        Assert.assertTrue(loanCalcPage.isElementDisplayed(loanCalcPage.getFeesField()),
                "Fees field should be visible on EMI tab");
        System.out.println("PASS — All input fields visible on EMI tab.");
    }

    //   TEST 3 — Verify text-box ↔ slider sync (EMI tab Loan Amount)
    @Test(priority = 3)
    public void verifySliderUpdatesTextBox_EMITab() {
        System.out.println("\n=========== TEST 3: Slider updates text box ===========");
        loanCalcPage.switchToTab("emi");
        // Read initial value
        String initialValue = loanCalcPage.getLoanAmountFieldValue();
        System.out.println("Initial Loan Amount: " + initialValue);
        // Move slider to a new value
        loanCalcPage.setLoanAmount(2000000);
        // Read new value
        String newValue = loanCalcPage.getLoanAmountFieldValue();
        System.out.println("After slider move: " + newValue);
        // Verify text box updated
        Assert.assertNotEquals(newValue, initialValue,
                "Text box should change when slider moves");
        System.out.println("PASS — Slider movement reflected in text box.");
    }

    //   TEST 4 — Verify Year/Month toggle changes tenure unit

    @Test(priority = 4)
    public void verifyYearMonthToggle() {
        System.out.println("\n=========== TEST 4: Year/Month toggle ===========");
        loanCalcPage.switchToTab("emi");
        // Start with Year mode
        loanCalcPage.toggleToYear();
        Assert.assertTrue(loanCalcPage.isYearSelected(),
                "Year radio should be selected after toggleToYear()");
        System.out.println("Year mode active: " + loanCalcPage.isYearSelected());
        String yearValue = loanCalcPage.getTenureFieldValue();
        System.out.println("Tenure in years: " + yearValue);
        // Switch to Month mode
        loanCalcPage.toggleToMonth();
        Assert.assertTrue(loanCalcPage.isMonthSelected(),
                "Month radio should be selected after toggleToMonth()");
        System.out.println("Month mode active: " + loanCalcPage.isMonthSelected());
        String monthValue = loanCalcPage.getTenureFieldValue();
        System.out.println("Tenure in months: " + monthValue);
        // Switch back to Year
        loanCalcPage.toggleToYear();
        Assert.assertTrue(loanCalcPage.isYearSelected(),
                "Year radio should be selected after switching back");
        System.out.println("PASS — Year/Month toggle works correctly.");
    }

    //   TEST 5 — Verify EMI is displayed after filling form
    @Test(priority = 5)
    public void verifyEMIIsCalculatedOnEMITab() {
        System.out.println("\n=========== TEST 5: EMI calculated ===========");
        loanCalcPage.switchToTab("emi");
        loanCalcPage.setLoanAmount(2000000);
        loanCalcPage.setInterestRate(9);
        loanCalcPage.setTenureInYears(10);
        String emi = loanCalcPage.getResultEMI();
        System.out.println("Calculated EMI: ₹" + emi);
        Assert.assertFalse(emi.isEmpty(),
                "EMI value should be displayed in summary panel");
        System.out.println("PASS — EMI displayed after filling form.");
    }

    //   TEST 6 — Verify Loan Amount Calculator tab
    @Test(priority = 6)
    public void verifyLoanAmountCalculatorTab() {
        System.out.println("\n=========== TEST 6: Loan Amount Calculator tab ===========");
        loanCalcPage.switchToTab("loanamount");
        // Set values that don't depend on Loan Amount
        loanCalcPage.setEMI(25000);
        loanCalcPage.setInterestRate(9);
        loanCalcPage.setTenureInYears(10);
        String loanAmount = loanCalcPage.getResultPrincipalLoanAmount();
        System.out.println("Calculated Loan Amount: ₹" + loanAmount);
        Assert.assertFalse(loanAmount.isEmpty(),
                "Principal Loan Amount should be displayed");
        System.out.println("PASS — Loan Amount calculated.");
    }

    //   TEST 7 — Verify Loan Tenure Calculator tab
    @Test(priority = 7)
    public void verifyLoanTenureCalculatorTab() {
        System.out.println("\n=========== TEST 7: Loan Tenure Calculator tab ===========");
        loanCalcPage.switchToTab("loantenure");
        loanCalcPage.setLoanAmount(2000000);
        loanCalcPage.setEMI(25000);
        loanCalcPage.setInterestRate(9);
        String tenure = loanCalcPage.getResultTenure();
        System.out.println("Calculated Tenure: " + tenure + " months");
        Assert.assertFalse(tenure.isEmpty(),
                "Tenure should be displayed");
        System.out.println("PASS — Loan Tenure calculated.");
    }

    //   TEST 8 — Reusable validation across all 3 tabs (DataProvider)
    //   Verifies that input fields and sliders work on every tab
    @DataProvider(name = "tabsToValidate")
    public Object[][] tabsToValidate() {
        return new Object[][] {
                {"emi"},
                {"loanamount"},
                {"loantenure"}
        };
    }

    @Test(priority = 8, dataProvider = "tabsToValidate")
    public void verifyTextBoxSliderSyncOnAllTabs(String tabName) {
        System.out.println("\n=========== TEST 8: Slider sync on " + tabName + " tab ===========");
        loanCalcPage.switchToTab(tabName);
        // Use Interest Rate slider — common to all 3 tabs and always visible
        String initialInterest = loanCalcPage.getInterestFieldValue();
        System.out.println("Initial interest on " + tabName + ": " + initialInterest);
        loanCalcPage.setInterestRate(10);
        String newInterest = loanCalcPage.getInterestFieldValue();
        System.out.println("After slider move: " + newInterest);
        Assert.assertNotEquals(newInterest, initialInterest,
                "Interest text box should update when slider moves on " + tabName + " tab");
        System.out.println("PASS — Slider sync works on " + tabName + " tab.");
    }

    //   TEST 9 — Verify EMI Scheme toggle (Advance / Arrears)
    @Test(priority = 9)
    public void verifyEMISchemeToggle() {
        System.out.println("\n=========== TEST 9: EMI Scheme toggle ===========");
        loanCalcPage.switchToTab("emi");
        // Click EMI in Advance
        loanCalcPage.selectEMIInAdvance();
        try { Thread.sleep(800); } catch (InterruptedException e) {}
        String emiAdvance = loanCalcPage.getResultEMI();
        System.out.println("EMI in Advance: ₹" + emiAdvance);
        // Click EMI in Arrears
        loanCalcPage.selectEMIInArrears();
        try { Thread.sleep(800); } catch (InterruptedException e) {}
        String emiArrears = loanCalcPage.getResultEMI();
        System.out.println("EMI in Arrears: ₹" + emiArrears);
        // Both should produce a non-empty result
        Assert.assertFalse(emiAdvance.isEmpty(), "Advance EMI should display");
        Assert.assertFalse(emiArrears.isEmpty(), "Arrears EMI should display");
        // The two EMIs should typically differ (Advance EMI is slightly less)
        Assert.assertNotEquals(emiAdvance, emiArrears,
                "EMI values should differ between Advance and Arrears schemes");
        System.out.println("PASS — EMI Scheme toggle changes the calculated EMI.");
    }
}
