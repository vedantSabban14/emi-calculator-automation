package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CarLoanPage;
import utils.HelperUtils;
import utils.configReader;

public class CarLoanTest extends BaseTest {
    CarLoanPage carLoanPage;
    HelperUtils helper;
    @BeforeMethod
    public void initPages()
    {
        carLoanPage=new CarLoanPage(driver,wait);
        helper=new HelperUtils(driver,wait);
    }

    @Test(priority = 1)
    public void verifyPageTitle(){
        String actualTitle= driver.getTitle();
        System.out.println("Page Title: "+actualTitle);

        Assert.assertTrue(actualTitle.contains("EMI Calculator for Home Loan, Car Loan & Personal Loan in India"),"Page Title does not match");
        System.out.println("Page Title Verified");
    }

    @Test(priority = 2)
    public void fillCarLoanDetails(){
        carLoanPage.enterCarLoanAmount(configReader.getCarLoanAmount());
        carLoanPage.enterCarLoanAmount(configReader.getInterestRate());
        carLoanPage.enterCarLoanAmount(configReader.getTenure());



    }


}
