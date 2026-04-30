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

import java.util.List;

public class HomeLoanTest extends BaseTest {

    HomeLoanPage homeLoanPage;
    HelperUtils helper;

    private static final String EXCEL_PATH = "test-data/TestData.xlsx";
    private static final String SHEET_NAME = "HomeLoan";

    // ±100 tolerance — Home Loan involves more rounding than Car Loan
    private static final int TOLERANCE = 100;
    private static final double percentTolerance = 0.05;

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

        // Parse all inputs once at the top
        double homeVal      = Double.parseDouble(homeValue);
        double dpAmount     = Double.parseDouble(downPayment);
        double loanIns      = Double.parseDouble(loanInsurance);
        double loanFeesPct  = Double.parseDouble(loanFees);
        double oneTimePct   = Double.parseDouble(oneTimeExpenses);
        double propTaxPct   = Double.parseDouble(propertyTaxes);
        double homeInsPct   = Double.parseDouble(homeInsurance);
        double monthlyMaint = Double.parseDouble(maintenance);

        int totalMonths = Integer.parseInt(tenure) * 12;

// 1. Principal (Loan Amount)
        int principal = (int) (homeVal + loanIns - dpAmount);

// 2. Down Payment, Fees & One-time Expenses
        double dp = (homeVal * dpAmount / 100)
                + (principal * loanFeesPct / 100)        // Loan Amount × Fees%
                + (homeVal * oneTimePct / 100);          // Home Value × One-time%

// 3. Interest
        int interest = (expectedEMI * totalMonths) - principal;

// 4. Taxes, Home Insurance & Maintenance
        double totalTax = ((propTaxPct / 100) * homeVal / 12) * totalMonths
                + ((homeInsPct / 100) * homeVal / 12) * totalMonths
                + (monthlyMaint * totalMonths);

// 5. Total Payment
        long expectedTotalPayment = (long) dp + principal + interest + (long) totalTax;

        // --- Step 2: Navigate to Home Loan page and fill form ---
        homeLoanPage.navigateToHomeLoan();
        homeLoanPage.fillHomeLoanForm(
            homeValue, downPayment, loanInsurance, interestRate, tenure,
            loanFees, oneTimeExpenses, propertyTaxes, homeInsurance, maintenance
        );

        // --- Step 3: Read actual values from website ---
        long actualEMI          = cleanAndConvert(homeLoanPage.getEMI());
        long actualTotalPayment = cleanAndConvert(homeLoanPage.getTotalPayment());

        // --- Step 4: Print comparison ---
        System.out.println("============= Comparison =============");
        System.out.printf("EMI             | Expected: %d | Actual: %d%n",
                          expectedEMI, actualEMI);
        System.out.printf("Total Payment   | Expected: %d | Actual: %d%n",
                          expectedTotalPayment, actualTotalPayment);
        System.out.println("======================================");

        // --- Step 5: Determine overall status ---
        boolean emiOk          = Math.abs(actualEMI - expectedEMI) <= TOLERANCE;
        long diff = Math.abs(actualTotalPayment - expectedTotalPayment);
        boolean totalPaymentOk;
        if (Math.abs(expectedTotalPayment) < 100000) {
            totalPaymentOk =  diff <= TOLERANCE;
        }else{
            double allowedDiff = Math.abs(expectedTotalPayment) * (percentTolerance / 100);
            totalPaymentOk = diff <= allowedDiff;
        }

        String status = (emiOk && totalPaymentOk) ? "PASS" : "FAIL";

        System.out.println("Status : " + status);

        // --- Step 6: Write all results back to Excel ---
        ExcelUtils.writeHomeLoanResult(
            EXCEL_PATH, SHEET_NAME, rowNum,
            expectedLoanAmount,
            expectedEMI, actualEMI,
            expectedTotalPayment, actualTotalPayment,
            status
        );

        // --- Step 9: Export year-on-year data to Excel ---
        List<String[]> yearlyData = homeLoanPage.getYearlyTableData();

        String yearlyOutputPath = "test-output/excel/HomeLoan_Yearly_"
                + testCaseName + ".xlsx";

        ExcelUtils.exportYearlyDataToExcel(yearlyData, yearlyOutputPath);

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
    private long cleanAndConvert(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("WARNING: Empty value received from website.");
            return 0;
        }

        String cleaned = text.split("\\.")[0].replaceAll("[^0-9]", "");

        if (cleaned.isEmpty()) {
            System.out.println("WARNING: No digits found in: '" + text + "'");
            return 0;
        }

        return Long.parseLong(cleaned);
    }
}
