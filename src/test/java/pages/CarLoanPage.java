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

    // Result fields
    private By emiResult         = By.xpath("//h4[normalize-space()='Loan EMI']/following-sibling::p/span");
    private By totalInterest     = By.xpath("//h4[normalize-space()='Total Interest Payable']/following-sibling::p/span");
    private By totalAmount       = By.xpath("//div[@id='emitotalamount']/p/span");

    // Monthly table — Month 1 row
    private By month1Principal   = By.xpath("//tr[@id='monthyear2026']/td/div/table/tbody/tr[1]/td[2]");
    private By month1Interest    = By.xpath("//tr[@id='monthyear2026']/td/div/table/tbody/tr[1]/td[3]");


//  Individual input actions
    public void enterLoanAmount(String amount) {
        driver.findElement(carLoanCategory).click();
        helper.typeInBox(carLoanAmount, amount);
        System.out.println("Loan amount entered : " + amount);
    }

    public void enterInterestRate(String rate) {
        helper.typeInBox(carLoanInterest, rate);
        System.out.println("Interest rate entered : " + rate);
    }

    public void enterTenure(String tenure) {
        helper.typeInBox(carLoanTenure, tenure);
        System.out.println("Tenure entered : " + tenure + " Year");
    }

    public void fillCarLoanForm(String amount, String rate, String tenure) {
        enterLoanAmount(amount);
        enterInterestRate(rate);
        enterTenure(tenure);
    }

// --- Getters for result values ---

    public String getEMI() {
        return helper.getText(emiResult);
    }

    public String getTotalInterest() {
        return helper.getText(totalInterest);
    }

    public String getTotalAmount() {
        return helper.getText(totalAmount);
    }

//  Getters for Month 1 row (for W-14 later)
    public String getMonth1Principal(){
        helper.scrollDown(1500);
        driver.findElement(By.id("year2026")).click();
        return helper.getText(month1Principal);
    }

    public String getMonth1Interest() {
        return helper.getText(month1Interest);
    }
}


