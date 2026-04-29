package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.HelperUtils;

public class LoanCalculatorPage {
    private WebDriver driver;
    private HelperUtils helper;

    private By menuButton = By.xpath("//button[@class='navbar-toggler']");
    private By loanCalculatorLnk = By.xpath("//a[@title='Loan Calculator']");
}
