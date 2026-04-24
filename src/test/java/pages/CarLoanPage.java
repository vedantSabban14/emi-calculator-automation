package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.HelperUtils;

public class CarLoanPage {
    WebDriver driver;
    HelperUtils helper;
    public CarLoanPage(WebDriver driver, WebDriverWait wait){
        this.driver=driver;
        helper=new HelperUtils(driver,wait);
    }
    By carLoanCategory= By.xpath("//a[normalize-space()='Car Loan']");
    By carLoanAmount= By.xpath("//div[@class='input-group']/input[@id='loanamount']");
    By carLoanInterest= By.xpath("//div[@class='input-group']/input[@id='loaninterest']");
    By carLoanTenure= By.xpath("//div[@class='input-group']/input[@id='loanterm']");
    By calculateBtn= By.xpath("//body");


    public void enterCarLoanAmount(String amount){
        helper.clickElement(carLoanCategory);
        helper.typeInBox(carLoanAmount,amount);
    }

    public void enterCarLoanInterest(String interest){
        helper.typeInBox(carLoanInterest,interest);
    }

    public void enterCarLoanTenure(String tenure){
        helper.typeInBox(carLoanTenure,tenure);
        helper.clickElement(calculateBtn);
        System.out.println("Values Entered Successfully");
    }

    public String getLoanAmount(){
        return driver.findElement(carLoanAmount).getText();
    }

    public String getLoanInterest(){
        return driver.findElement(carLoanInterest).getText();
    }

    public String getLoanTenure(){
        return driver.findElement(carLoanTenure).getText();
    }
}


//// Result fields
//private By emiResult         = By.id("emi");
//private By totalInterest     = By.id("totalinterest");
//private By totalAmount       = By.id("totalpayable");
//
//// Monthly table — Month 1 row
//private By month1Principal   = By.xpath("(//table[@id='emipaymenttable']//tbody//tr)[1]/td[2]");
//private By month1Interest    = By.xpath("(//table[@id='emipaymenttable']//tbody//tr)[1]/td[3]");
//
//// --- Constructor ---
//public CarLoanPage(WebDriver driver, WebDriverWait wait) {
//    this.driver = driver;
//    this.helper = new HelperUtils(driver, wait);
//}
//
//// --- Individual input actions ---
//
//public void enterLoanAmount(String amount) {
//    helper.typeInBox(loanAmountField, amount);
//    System.out.println("Loan amount entered : " + amount);
//}
//
//public void enterInterestRate(String rate) {
//    helper.typeInBox(interestRateField, rate);
//    System.out.println("Interest rate entered : " + rate);
//}
//
//public void enterTenure(String tenure) {
//    // Make sure 'Year' is selected before typing tenure
//    helper.clickElement(tenureYearBtn);
//    helper.typeInBox(tenureField, tenure);
//    System.out.println("Tenure entered : " + tenure + " Year");
//}
//
//// --- Combined action: fill entire form ---
//public void fillCarLoanForm(String amount, String rate, String tenure) {
//    enterLoanAmount(amount);
//    enterInterestRate(rate);
//    enterTenure(tenure);
//
//    // Click outside the input field to trigger recalculation
//    // emicalculator.net auto-calculates, but tab-out ensures refresh
//    helper.scrollDown(200);
//}
//
//// --- Getters for result values ---
//
//public String getEMI() {
//    return helper.getText(emiResult);
//}
//
//public String getTotalInterest() {
//    return helper.getText(totalInterest);
//}
//
//public String getTotalAmount() {
//    return helper.getText(totalAmount);
//}
//
//// --- Getters for Month 1 row (for W-14 later) ---
//
//public String getMonth1Principal() {
//    helper.scrollToElement(month1Principal);
//    return helper.getText(month1Principal);
//}
//
//public String getMonth1Interest() {
//    helper.scrollToElement(month1Interest);
//    return helper.getText(month1Interest);
//}
//}
