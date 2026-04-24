package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CarLoanPage;
import utils.EMICalculator;
import utils.ExcelUtils;
import utils.HelperUtils;

public class CarLoanTest extends BaseTest {

    CarLoanPage carLoanPage;
    HelperUtils helper;

    // File path
    private static final String EXCEL_PATH = "test-data/CarLoanTestData.xlsx";
    private static final String SHEET_NAME = "Sheet1";

    @BeforeMethod
    public void initPages() {
        carLoanPage = new CarLoanPage(driver, wait);
        helper      = new HelperUtils(driver, wait);
    }

    @DataProvider(name = "carLoanData")
    public Object[][] getCarLoanData() {
        return ExcelUtils.readExcel(EXCEL_PATH, SHEET_NAME);
    }

    @Test(dataProvider = "carLoanData")
    public void verifyEMI(String amount, String rate, String tenure,
                          String testCaseName, int rowNum) {

        System.out.println("\n=========== Running: " + testCaseName + " ===========");

        // Calculate expected EMI
        int expectedEMI = EMICalculator.calculateEMI(
                Double.parseDouble(amount),
                Double.parseDouble(rate),
                Integer.parseInt(tenure)
        );

        // Fill form on website
        carLoanPage.fillCarLoanForm(amount, rate, tenure);

        // Read actual EMI from website
        String actualEMIText = carLoanPage.getEMI();
        int actualEMI = cleanAndConvert(actualEMIText);

        System.out.println("Expected EMI : " + expectedEMI);
        System.out.println("Actual EMI   : " + actualEMI);

        // Determine status
        String status = (actualEMI==expectedEMI) ? "PASS" : "FAIL";

        // Write results back to Excel
        ExcelUtils.writeResultToExcel(EXCEL_PATH, SHEET_NAME, rowNum,
                expectedEMI, actualEMI, status);

        Assert.assertEquals(status, "PASS",
                "EMI validation failed for " + testCaseName);

        System.out.println(status + ": " + testCaseName);
    }

    private int cleanAndConvert(String text) {
        String cleaned = text.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleaned);
    }
}