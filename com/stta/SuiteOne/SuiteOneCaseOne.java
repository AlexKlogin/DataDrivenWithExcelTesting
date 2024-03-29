//Find More Tutorials On WebDriver at -> http://software-testing-tutorials-automation.blogspot.com
package com.stta.SuiteOne;

import java.io.IOException;
import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//SuiteOneCaseOne Class Inherits From SuiteOneBase Class.
//So, SuiteOneCaseOne Class Is Child Class Of SuiteOneBase Class And SuiteBase Class.
public class SuiteOneCaseOne extends SuiteOneBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;	
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();			
		//To set SuiteOne.xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelOne;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		//Bellow given syntax will Insert log In applog.log file.
		Add_Log.info(TestCaseName+" : Execution started.");
		
		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else It will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
			//To report result as skip for test cases In TestCasesList sheet.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//To throw skip exception for this test case.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}	
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData);
	}
	
	//Accepts 4 column's String data In every Iteration.
	@Test(dataProvider="SuiteOneCaseOneData")
	public void SuiteOneCaseOneTest(String DataCol1,String DataCol2,String DataCol3,String ExpectedResult) throws InterruptedException{
		
		DataSet++;
		
		//Created object of testng SoftAssert class.
		s_assert = new SoftAssert();
		
		//If found DataToRun = "N" for data set then execution will be skipped for that data set.
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){	
			Add_Log.info(TestCaseName+" : DataToRun = N for data set line "+(DataSet+1)+" So skipping Its execution.");
			//If DataToRun = "N", Set Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No Or Blank. So Skipping Its Execution.");
		}
		
		//If found DataToRun = "Y" for data set then bellow given lines will be executed.
		//To Convert data from String to Integer
		int ValueOne = Integer.parseInt(DataCol1);
		int ValueTwo = Integer.parseInt(DataCol2);
		int ValueThree = Integer.parseInt(DataCol3);
		int ExpectedResultInt =  Integer.parseInt(ExpectedResult);
		
		//To Initialize Firefox browser.
		loadWebBrowser();		
		
		//To navigate to URL. It will read site URL from Param.properties file
		driver.get(Param.getProperty("siteURL")+"/2014/04/calc.html");		
		
		//Simple calc test.
		//Xpath of Calc Result text box Is stored Inside object repository file Objects.propertis.
		//We can read It using Its key txt_Result. Syntax Is Object.getProperty("txt_Result").
		driver.findElement(By.name(Object.getProperty("txt_Result"))).clear();
		
		//Xpath of calc number buttons Is divided In two parts because we need to use variable values(ValueOne, ValueTwo, etc..) In between xpath to build full xpath of buttons.
		//First part of xpath Is stored In 'btn_Calc_PrePart' and second part of xpath Is stored In 'btn_Calc_PostPart' In Objects.propertis file.
		//driver.findElement(By.xpath(Object.getProperty("btn_Calc_PrePart")+ValueOne+Object.getProperty("btn_Calc_PostPart"))).click();
		typeNumber(DataCol1);
		Thread.sleep(3000);
		//Xpath of '+' button Is stored Inside Objects.propertis file with key=btn_Plus
		driver.findElement(By.xpath(Object.getProperty("btn_Plus"))).click();
		
		
		//driver.findElement(By.xpath(Object.getProperty("btn_Calc_PrePart")+ValueTwo+Object.getProperty("btn_Calc_PostPart"))).click();
		typeNumber(DataCol2);
		Thread.sleep(3000);
		driver.findElement(By.xpath(Object.getProperty("btn_Plus"))).click();
				
		//driver.findElement(By.xpath(Object.getProperty("btn_Calc_PrePart")+ValueThree+Object.getProperty("btn_Calc_PostPart"))).click();
		typeNumber(DataCol3);
		Thread.sleep(3000);
		//Xpath of '=' button Is stored Inside Objects.propertis file with key=btn_Equals
		driver.findElement(By.cssSelector(Object.getProperty("btn_Equals"))).click();
		Thread.sleep(3000);		
		String Result = driver.findElement(By.name(Object.getProperty("txt_Result"))).getAttribute("value");
		
		System.out.println(Result);
		Add_Log.info("Calc Sum Test Result Is :"+ Result);
		
		if(!Result.equals(ExpectedResult))
			{
				Testfail = true;
				s_assert.assertEquals(Result, ExpectedResult, "The sum is not equal to Expected Result: ");
			}
		if(Testfail){
			//At last, test data assertion failure will be reported In testNG reports and It will mark your test data, test case and test suite as fail.
			s_assert.assertAll();		
		}
	}
	
	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){		
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//To make object reference null after reporting In report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail In excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");			
		}else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}
	
	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] SuiteOneCaseOneData(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of SuiteOneCaseOne data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}	
	
	public void typeNumber(String value)
	{
		for(int i=0;i<value.length();i++)
		{
			char temp = value.charAt(i);
			driver.findElement(By.xpath(Object.getProperty("btn_Calc_PrePart") + temp + Object.getProperty("btn_Calc_PostPart"))).click();
		}
	}
	
	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser(){
		//To Close the web browser at the end of test.
		closeWebBrowser();
		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");			
		}
	}
}