package Running;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DocumentUpload {

	FileInputStream fStream = null;

	public static void main(String[] args) throws Exception {
		DocumentUpload obj = new DocumentUpload();
		obj.uploadDocuments();
	}

	public List<File> uploadDocuments() throws Exception {
		Properties prop = new Properties();
		
		fStream = new FileInputStream("D:\\UploadFilesDemo\\Data.properties");
	
		try {
			prop.load(fStream);
		} catch (Exception e) {
// TODO: handle exception
			e.printStackTrace();
		}
// load directoryName from properties
		String directoryName = prop.getProperty("AllFilesPath");
		System.out.println(directoryName);
		ExcelSheet excel = new ExcelSheet(prop.getProperty("ExcelPathForStatus"));
		WebDriver driver;
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		System.setProperty("webdriver.chrome.driver", prop.getProperty("ChromeDriverPath"));
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(prop.getProperty("URL"));
		driver.findElement(By.xpath(".//input[@id='username']")).sendKeys(prop.getProperty("UserName"));
		driver.findElement(By.xpath(".//input[@id='password']")).sendKeys(prop.getProperty("Password"));
		driver.findElement(By.xpath(".//input[@id='Login']")).click();
		System.out.println("User Logged On");
		Thread.sleep(1000);
		driver.findElement(By.xpath(".//*[text()='More']")).click();
		Thread.sleep(1000);
		WebElement element = driver.findElement(By.xpath("(.//*[text()='Documents'])[2]"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
		Thread.sleep(1000);
		WebElement element5 = driver.findElement(By.xpath(".//*[@title='Select List View']"));
		executor.executeScript("arguments[0].click();", element5);
		sliderBarProcessing(driver);
		WebElement element6 = driver.findElement(By.xpath("//span[text()='All']"));
		executor.executeScript("arguments[0].click();", element6);
		List<File> testList = new ArrayList<>();
		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				testList.add(listOfFiles[i]);
				System.out.println(listOfFiles[i].getName());
			}
		}
		int row = 1;
		for (File newfile : testList) {
			String fname = newfile.getName();
			fname = fname.substring(0, fname.indexOf("."));
			String obsfileNameString = newfile.getAbsolutePath();
			System.out.println(fname + obsfileNameString);
			Thread.sleep(4000);
			WebElement ele = driver.findElement(By.xpath(".//*[@placeholder='Search this list...']"));
			ele.clear();
			ele.sendKeys(fname);
			ele.sendKeys("\n");
			sliderBarProcessing(driver);
			By noData = By.xpath("//p[text()='No items to display.']");
			if (!isElementPresent(driver, noData)) {
				driver.findElement(
						By.xpath("(.//*[@class='slds-grid slds-grid--align-spread forceInlineEditCell']//a)[1]"))
						.click();
				Thread.sleep(4000);
				driver.findElement(By.xpath("(.//*[text()='Related'])[last()]")).click();
				Thread.sleep(5000);
				By upload = By.xpath("(//span[text()='Files'])[last()]//following::span[text()='Upload Files']");
				if (isElementPresent(driver, upload)) {
					driver.findElement(By.xpath("(.//*[text()='Upload Files'])[last()]")).click();
					Thread.sleep(1000);
					StringSelection sel1 = new StringSelection(obsfileNameString);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel1, null);
					Robot robot = new Robot();
					robot.setAutoDelay(2000);
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_V);
					robot.keyRelease(KeyEvent.VK_V);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					Thread.sleep(2500);
					robot.keyPress(KeyEvent.VK_ENTER);
					robot.keyRelease(KeyEvent.VK_ENTER);
					try {
						progressLoading(driver);
                  // WebDriverWait wait = new WebDriverWait(driver, 40);
                 // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[text()='Done']")));
						WebElement ele4 = driver.findElement(By.xpath(".//*[text()='Done']"));
						boolean actualValue = ele4.isEnabled();
						Thread.sleep(5000);
						if (actualValue) {
							excel.writedata(row, fname, "PASS");
							ele4.click();
							System.out.println("File uploaded successfully");
						} else {
							System.out.println("File not uploaded");
							excel.writedata(row, fname, "FAIL");
						}
					} catch (Exception e) {
						System.out.println("File not uploaded");
						excel.writedata(row, fname, "FAIL");
					}
				} else {
					System.out.println("File already uploaded");
					excel.writedata(row, fname, "File Already Uploaded");
				}
			} else {
				System.out.println("No files are existed with " + fname + " name");
				excel.writedata(row, fname, "Policy Not Found");
			}
			WebElement element4 = driver.findElement(By.xpath("(.//*[text()='Documents'])[1]"));
			executor.executeScript("arguments[0].click();", element4);
			sliderBarProcessing(driver);
		   WebElement element8 = driver.findElement(By.xpath(".//*[@title='Select List View']"));
			executor.executeScript("arguments[0].click();", element8);
			sliderBarProcessing(driver);
			WebElement element7 = driver.findElement(By.xpath("//span[text()='All']"));
			executor.executeScript("arguments[0].click();", element7);
			row++;
		}
		driver.quit();
		return (testList);
	}

	public static void sliderBarProcessing(WebDriver driver) throws InterruptedException {
		while (true) {
			try {
				WebElement ele = driver.findElement(By.xpath(".//div[@class='slds-spinner_container slds-grid']"));
				if (ele.isDisplayed())
					Thread.sleep(1000);
				else
					break;
			} catch (Exception e) {
				break;
			}

		}
	}

	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			WebElement ele = driver.findElement(by);
			if (ele.isDisplayed())
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static void progressLoading(WebDriver driver) {
		while (true)
			try {
				WebElement ele = driver.findElement(By.xpath("//div[@role='progressbar']"));
				if (ele.isDisplayed()) {
					Thread.sleep(1000);
					WebElement ele4 = driver.findElement(By.xpath(".//*[text()='Done']"));
					if (ele4.isEnabled()) {
						break;
					}
				} else {
					break;
				}
			} catch (Exception e) {
				break;
			}
      
	}

		
}