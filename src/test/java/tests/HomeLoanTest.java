package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomeLoanPage;
import utils.EMICalculator;
import utils.ExcelUtils;
import utils.HelperUtils;

public class HomeLoanTest extends BaseTest {

    HomeLoanPage homeLoanPage;
    HelperUtils helper;

    private static final String EXCEL_PATH = "test-data/LoanTestData.xlsx";
    private static final String SHEET_NAME = "HomeLoan";

    // ±100 tolerance — Home Loan involves more rounding than Car Loan
    private static final int TOLERANCE = 100;

    @BeforeMethod
    public void initPages() {
        homeLoanPage = new HomeLoanPage(driver, wait);
        helper       = new HelperUtils(driver, wait);
    }

    @DataProvider(name = "homeLoanData")
    public Object[][] getHomeLoanData() {
        return ExcelUtils.readHomeLoanExcel(EXCEL_PATH, SHEET_NAME);
    }

    @Test(dataProvider = "homeLoanData")
    public void verifyHomeLoanCalculation(
            String homeValue, String downPayment, String loanInsurance,
            String interestRate, String tenure, String loanFees,
            String oneTimeExpenses, String propertyTaxes,
            String homeInsurance, String maintenance,
            String testCaseName, int rowNum) {

        System.out.println("\n=========== Running: " + testCaseName + " ===========");

        // --- Step 1: Calculate expected values ---
        int expectedLoanAmount = calculateExpectedLoanAmount(
            homeValue, downPayment, loanInsurance
        );

        int expectedEMI = EMICalculator.calculateEMI(
            (double) expectedLoanAmount,
            Double.parseDouble(interestRate),
            Integer.parseInt(tenure)
        );

        int expectedTotalPayment = expectedEMI * Integer.parseInt(tenure) * 12;

        // --- Step 2: Navigate to Home Loan page and fill form ---
        homeLoanPage.navigateToHomeLoan();
        homeLoanPage.fillHomeLoanForm(
            homeValue, downPayment, loanInsurance, interestRate, tenure,
            loanFees, oneTimeExpenses, propertyTaxes, homeInsurance, maintenance
        );

        // --- Step 3: Read actual values from website ---
        int actualLoanAmount   = cleanAndConvert(homeLoanPage.getLoanAmount());
        int actualEMI          = cleanAndConvert(homeLoanPage.getEMI());
        int actualTotalPayment = cleanAndConvert(homeLoanPage.getTotalPayment());

        // --- Step 4: Print comparison ---
        System.out.println("============= Comparison =============");
        System.out.printf("Loan Amount     | Expected: %d | Actual: %d%n",
                          expectedLoanAmount, actualLoanAmount);
        System.out.printf("EMI             | Expected: %d | Actual: %d%n",
                          expectedEMI, actualEMI);
        System.out.printf("Total Payment   | Expected: %d | Actual: %d%n",
                          expectedTotalPayment, actualTotalPayment);
        System.out.println("======================================");

        // --- Step 5: Determine overall status ---
        boolean loanAmountOk   = Math.abs(actualLoanAmount - expectedLoanAmount) <= TOLERANCE;
        boolean emiOk          = Math.abs(actualEMI - expectedEMI) <= TOLERANCE;
        boolean totalPaymentOk = Math.abs(actualTotalPayment - expectedTotalPayment) <= TOLERANCE;

        String status = (loanAmountOk && emiOk && totalPaymentOk) ? "PASS" : "FAIL";

        System.out.println("Status : " + status);

        // --- Step 6: Write all results back to Excel ---
        ExcelUtils.writeHomeLoanResult(
            EXCEL_PATH, SHEET_NAME, rowNum,
            expectedLoanAmount, actualLoanAmount,
            expectedEMI, actualEMI,
            expectedTotalPayment, actualTotalPayment,
            status
        );

        // --- Step 7: Take screenshot ---
        helper.takeScreenshot(testCaseName);

        // --- Step 8: TestNG assertion ---
        Assert.assertEquals(status, "PASS",
            "Home Loan validation failed for " + testCaseName);

        System.out.println("=========== " + status + ": " + testCaseName + " ===========\n");
    }

    // ------------------- Helper methods -------------------

    // Loan Amount = Home Value + Loan Insurance − Down Payment (when DP is in ₹)
    // If DP is in %: Loan Amount = Home Value × (1 - DP%/100) + Loan Insurance
    private int calculateExpectedLoanAmount(String homeValue, String downPayment,
                                            String loanInsurance) {
        double hv = Double.parseDouble(homeValue);
        double dp = Double.parseDouble(downPayment);   // assumed in %
        double li = Double.parseDouble(loanInsurance);

        double loanAmount = hv - (hv * dp / 100) + li;
        return (int) Math.round(loanAmount);
    }

    // Cleans "₹ 32,00,000" → 3200000
    private int cleanAndConvert(String text) {
        String cleaned = text.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleaned);
    }
}
