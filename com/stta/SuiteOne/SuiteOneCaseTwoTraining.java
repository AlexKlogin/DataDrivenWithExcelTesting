package com.stta.SuiteOne;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SuiteOneCaseTwoTraining extends SuiteOneBase{
	
@Test
	public void ToolTiptest() throws IOException, InterruptedException {
	//init();
	//loadWebBrowser();		
	
	//To navigate to URL. It will read site URL from Param.properties file
	driver.get(Param.getProperty("siteURL")+"//2015/03/chart.html");	
	Thread.sleep(3000);
	Actions builder = new Actions(driver);
	WebElement btn1 = driver.findElement( By.xpath("//a[contains(text(),'Hover over me')]" ));
	builder.moveToElement(btn1).build().perform();
	WebElement btn3 = driver.findElement(By.xpath("//a[starts-with(@aria-describedby,'ui-tooltip')]"));
	Assert.assertTrue(btn3.isDisplayed());
	//Thread.sleep(3000);
	//WebElement btn2 = driver.findElement( By.xpath("//input[@id='tooltip-1']" ));
	//builder.moveToElement(btn2).build().perform();
	
	//driver.findElement( By.xpath("//a[contains(text(),'Hover over me')]" )).click();
	}


}
