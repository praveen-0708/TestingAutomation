package com.tests;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestNGSampleTest {

    WebDriver webDriver;

    @BeforeClass
    public void openBrowser() {
        System.out.println("Opening browser");
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

    }
    @Parameters({"firstCity","secondCity"})
    @BeforeMethod
    public void loadURL(String firstCity,String secondCity){
        webDriver.get("https://savvytime.com/converter");
        webDriver.findElement(By.xpath("//input[@placeholder='Add Time Zone, City or Town']")).sendKeys(firstCity);
        webDriver.findElements(By.xpath("//div[@id='converter-quick-search-result']//a")).get(0).click();
        webDriver.findElement(By.xpath("//input[@placeholder='Add Time Zone, City or Town']")).sendKeys(secondCity);
        webDriver.findElements(By.xpath("//div[@id='converter-quick-search-result']//a")).get(0).click();

    }

    @Test(description = "Checking whether Savvy page is loaded or not.")
    public void pageLoad() {
        Assert.assertTrue(webDriver.findElement(By.xpath("//h5[@class='logo-text']")).getText().contains("Savvy Time"));
    }

    @Parameters({"firstCity"})
    @Test(description = "Enter First City")
    public void enterFirstCity(String firstCity) {

        webDriver.findElement(By.xpath("//input[@placeholder='Add Time Zone, City or Town']")).sendKeys(firstCity);
        webDriver.findElements(By.xpath("//div[@id='converter-quick-search-result']//a")).get(0).click();
        Assert.assertTrue(webDriver.findElement(By.xpath("//h1[@class='title']")).getText().contains(firstCity));

    }

    @Parameters({"secondCity"})
    @Test(description = "Enter Second City")
    public void enterSecondCity(String secondCity) {
        webDriver.findElement(By.xpath("//input[@placeholder='Add Time Zone, City or Town']")).sendKeys(secondCity);
        webDriver.findElements(By.xpath("//div[@id='converter-quick-search-result']//a")).get(0).click();
        Assert.assertTrue(webDriver.findElement(By.xpath("//h1[@class='title']")).getText().contains(secondCity));

    }

    @Test(description = "Checking the time difference")
    public void checkTimeDifference() throws ParseException {
        List<WebElement> listOfTime = webDriver.findElements(By.xpath("//input[@class='time ampm format12 form-control ui-timepicker-input']"));
        String time1 = listOfTime.get(0).getAttribute("value");
        String time2 = listOfTime.get(1).getAttribute("value");
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        Date date1 = format.parse(time1);
        System.out.println(date1);
        Date date2 = format.parse(time2);
        System.out.println(date2);
        Assert.assertEquals(Math.abs(date1.getTime() - date2.getTime()) / 60000, 330);
    }

    @Test(description = "Checking swap button")
    public void checkSwapButton(){
        List<WebElement> beforeSwap=webDriver.findElements(By.xpath("//a[@class='time-abb']"));
        List<String> beforeSwapCities=new ArrayList<String>();
        for(WebElement webElement:beforeSwap)
            beforeSwapCities.add(webElement.getText());

        webDriver.findElement(By.xpath("//a[@class='swap-tz btn']")).click();

        List<WebElement> afterSwap=webDriver.findElements(By.xpath("//a[@class='time-abb']"));
        List<String> afterSwapCities=new ArrayList<String>();
        for(WebElement webElement:afterSwap)
            afterSwapCities.add(webElement.getText());


        Collections.reverse(afterSwapCities);
        Assert.assertEquals(afterSwapCities, beforeSwapCities);


    }

    @Test(description = "Checking delete button")
    public void checkDelete(){
        List<WebElement> elements;
        elements=webDriver.findElements(By.xpath("//div[@class='table-time row']"));
        int lengthBeforeDelete=elements.size();
        elements.get(0).click();
        webDriver.findElement(By.xpath("//a[@class='delete-btn btn']")).click();
        elements=webDriver.findElements(By.xpath("//div[@class='table-time row']"));
        int lengthAfterDelete=elements.size();
        Assert.assertEquals(lengthBeforeDelete,lengthAfterDelete+1);
    }



    @Test(description = "Changing date")
    public void changeDate(){
        webDriver.findElement(By.xpath("//span[@class='input-group-addon']")).click();

        String currentDate=webDriver.findElement(By.xpath("//div[@class='datepicker-days']//th[@class='datepicker-switch']")).getText();
        System.out.println(currentDate);

        webDriver.findElement(By.xpath("//div[@class='datepicker-days']//table[@class=' table-condensed']//tr[3]//td[4]")).click();

        Assert.assertTrue(webDriver.findElement(By.xpath("//div[@class='tz-date']")).getText().contains(currentDate.substring(0,3)+" 15"));

    }

    @AfterClass
    public void closeBrowser() {
        webDriver.quit();
    }

}