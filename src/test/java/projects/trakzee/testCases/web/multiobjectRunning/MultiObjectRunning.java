package projects.trakzee.testCases.web.multiobjectRunning;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Description;

public class MultiObjectRunning {
	static {
		System.setProperty("headless", "false");	
		}
	String objectFileFromTestXMLFile;
	String pathFileFromTestXMLFile;
	String serverFromXMLFile;
	
	private static WebDriver driver;
	//xpath
	public static final By alreadyPresentIndexCount = By.xpath("//tbody[@id='configurations']//tr//td[1]");
	public static final String imeiNoStringFormat = "(//td//input[@name='imei_no'])[%s]";
	public static final String serverIPStringFormat = "(//td//input[@name='server_ip'])[%s]";
	public static final String CSVFileStringFormat = "//td//input[@name='csv_file']";
	public static final String timeIntervalStringFormat = "(//td//input[@name='update_interval'])[%s]";
	public static final String statusStringFormat = "(//td//div[@class='custom-control custom-switch'])[%s]";
	public static final String statusBasedOnIMEINo = "//input[@name='imei_no' and @value='%s']/ancestor::td/following-sibling::td//div[@class='custom-control custom-switch']";
	public static final String submitStringFormat = "(//td//button[text()='Submit'])[%s]";
	public static final String removeStringFormat = "(//td//button[text()='Remove'])[%s]";
	public static final String removeStringFormatBasedOnIMEINo = "//input[@name='imei_no' and @value='%s']/ancestor::td/following-sibling::td//button[text()='Remove']";
	
	public static final By addConfiguration = By.xpath("//button[@id='add_configuration']");

	
	SoftAssert softAssert = new SoftAssert();
	public void setDataInFieldWithClear(WebDriver driver, String locatorAddress, String value) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locatorAddress)));

			scrollElementTillNotVisible(driver, element);
			element.click();
			element.sendKeys(Keys.CONTROL, "a");
			element.sendKeys(Keys.DELETE);
			element.sendKeys(value);
			System.out.println("Data set successfully in field: " + value);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			softAssert.assertTrue(false, "Not set " + value + " data in field: " + locatorAddress);
		}

	}
	
	public static void scrollElementTillNotVisible(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}


	public void clickOnButton(WebDriver driver, String locatorAddress) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		try {
			WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locatorAddress)));
			loginButton.click();
			System.out.println("Clicked on the given element: " + locatorAddress);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			softAssert.assertTrue(false, "Not click on the button: " + locatorAddress);
		}

	}
	
	public String lastIndex() {
		int totalIndex = getWebDriver().findElements(alreadyPresentIndexCount).size();
		return totalIndex + "";
	}

	public void setIMEINo(String imieNo) {
		setDataInFieldWithClear(getWebDriver(), String.format(imeiNoStringFormat, lastIndex()), imieNo);
	}

	public void setServerIP(String serverIP) {
		setDataInFieldWithClear(getWebDriver(), String.format(serverIPStringFormat, lastIndex()), serverIP);
	}

	public void setTimeInterval(String timeInterval) {
		setDataInFieldWithClear(getWebDriver(), String.format(timeIntervalStringFormat, lastIndex()), timeInterval);
	}

	public void setCSVFile(String csvFile) {
		WebElement ele = getWebDriver().findElement(By.xpath(CSVFileStringFormat));
		ele.sendKeys(csvFile);
	}

	public void clickOnSubmit() throws InterruptedException {
		clickOnButton(getWebDriver(), String.format(submitStringFormat, lastIndex()));
		acceptAlert();
	}

	public void acceptAlert() {
		try {
			// WebDriverWait wait = new WebDriverWait(getWebDriver(),
			// Duration.ofSeconds(10));// selenium [4.26]
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			wait.until(ExpectedConditions.alertIsPresent()); // Wait for alert

			Alert alert = getWebDriver().switchTo().alert(); // Switch to the alert
			System.out.println("Alert Text: " + alert.getText()); // Print alert text
			alert.accept(); // Accept (OK button)
			System.out.println("Alert accepted.");
		} catch (NoAlertPresentException e) {
			System.out.println("No alert found.");
		}

	}

	public boolean clickOnAddConfiguration() {
		return clickOnButtonWithBooleanReturn(getWebDriver(), addConfiguration);
	}
	
	public boolean clickOnButtonWithBooleanReturn(WebDriver driver, By locatorAddress) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		try {
			WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(locatorAddress));
			loginButton.click();
			System.out.println("Clicked on the given element: " + locatorAddress);
			return true;
		} catch (Exception e) {
			System.out.println("Exception: " + e.getStackTrace());
			softAssert.assertTrue(false, "Not click on the button: " + locatorAddress);
		}

		return false;
	}

	public void clickOnRemove() {
		clickOnButton(getWebDriver(), String.format(removeStringFormat, lastIndex()));
		acceptAlert();
	}
	
	public void clickOnRemove(String imeiNo) {
		clickOnButton(getWebDriver(), String.format(removeStringFormatBasedOnIMEINo, imeiNo));
		acceptAlert();
	}

	public void clickOnRemoveBulkUpdate(int indexes) {
		// WebDriverWait wait = new WebDriverWait(getWebDriver(),
		// Duration.ofSeconds(10));[v4.26]

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

		for (int index = indexes; index <= indexes; index--) {
			if (index == 0) {
				return;
			}
			clickOnButton(getWebDriver(), String.format(removeStringFormat, index));
			System.out.println("Clicked on status successfully!");
			acceptAlert();

		}
	}

	public void clickOnStatus() throws InterruptedException {
		// WebDriverWait wait = new WebDriverWait(getWebDriver(),
		// Duration.ofSeconds(10));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		String xpath = String.format(statusStringFormat, lastIndex());
		System.out.println("Status: " + xpath);

		try {
			WebElement status = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

			// Scroll into view before clicking
			((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", status);
			Thread.sleep(500); // Small wait after scrolling

			status.click();
			System.out.println("Clicked on status successfully!");
		} catch (Exception e) {
			System.out.println("Not clicked on the status: " + xpath);
			e.printStackTrace();
		}
	}

	public void clickOnStatus(String imeiNo) throws InterruptedException {
		// WebDriverWait wait = new WebDriverWait(getWebDriver(),
		// Duration.ofSeconds(10));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
			String xpath = String.format(statusBasedOnIMEINo, imeiNo);
			System.out.println("Status: " + xpath);

			try {
				WebElement status = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

				// Scroll into view before clicking
				((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", status);
				
				status.click();
				System.out.println("Clicked on status successfully!");
			} catch (Exception e) {
				System.out.println("Not clicked on the status: " + xpath);
				e.printStackTrace();
			}
		
	}
	
	public void clickOnStatus(int indexes) throws InterruptedException {
		// WebDriverWait wait = new WebDriverWait(getWebDriver(),
		// Duration.ofSeconds(10));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		for (int index = 1; index <= indexes; index++) {
			String xpath = String.format(statusStringFormat, index);
			System.out.println("Status: " + xpath);

			try {
				WebElement status = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

				// Scroll into view before clicking
				((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", status);
				Thread.sleep(500); // Small wait after scrolling

				status.click();
				System.out.println("Clicked on status successfully!");
			} catch (Exception e) {
				System.out.println("Not clicked on the status: " + xpath);
				e.printStackTrace();
			}
		}
	}
	
	public static String toCamelCase(String input) {
		input = input.toLowerCase().trim();
		StringBuilder camelCaseString = new StringBuilder();
		boolean capitalizeNext = false;

		for (char c : input.toCharArray()) {
			if (Character.isWhitespace(c) || c == '_' || c == '-') {
				capitalizeNext = true;
			} else if (capitalizeNext) {
				camelCaseString.append(Character.toUpperCase(c));
				capitalizeNext = false;
			} else {
				camelCaseString.append(c);
			}
		}
		return camelCaseString.toString();
	}

	public static Map<String, Object> convertKeysToCamelCase(Map<String, Object> inputMap) {
		Map<String, Object> modifiedMap = new LinkedHashMap<>(); // Preserve order

		for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
			String camelCaseKey = toCamelCase(entry.getKey()); // Convert to camel case
			modifiedMap.put(camelCaseKey, entry.getValue()); // Store with modified key
		}

		return modifiedMap; // Return updated map
	}
	
	List<String> imeiNoList = new ArrayList<String>();

	@Description("To add the vehicle")
	@Test(enabled = true, priority = 1, dataProvider = "multiobjectRunningDataProvider")
	public void add(Map<String, Object> filterData)
			throws Exception {
		//TESTCASE_DONE: testToAddVehicleAndRunIt
		try {
			filterData = convertKeysToCamelCase(filterData);
			imeiNoList.add((String) filterData.get("imeiNo"));
			
			clickOnAddConfiguration();
			setIMEINo((String) filterData.get("imeiNo"));
			setServerIP((String) filterData.get("serverIp"));
			setTimeInterval((String) filterData.get("timeInterval"));
			
			String filePath= "";

			if(!pathFileFromTestXMLFile.isEmpty()) {
				filePath = pathFileFromTestXMLFile;
			}else {
				String fileName = "Path";
				filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
						+ File.separator + "java" + File.separator + "projects" + File.separator +"trakzee" + File.separator + "testCases"+ File.separator + "web" + File.separator +  "multiobjectRunning"
						+ File.separator + fileName + ".csv";
				System.out.println("XLS File path: " + filePath);
			}
			
			setCSVFile(filePath);
			clickOnSubmit();
			getWebDriver().navigate().refresh();
			Thread.sleep(200);
			clickOnStatus(); //active
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Description("Refresh the page")
	@Test(enabled = true,priority = 2)
	public void refresh() throws InterruptedException {
		try {
			getWebDriver().navigate().refresh();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Description("To change the status active-inactive")
	@Test(enabled = true,priority = 3,dataProvider = "multiobjectRunningDataProvider")
	public void status(Map<String, Object> filterData) throws InterruptedException {
		try {
			filterData = convertKeysToCamelCase(filterData);
			clickOnStatus((String) filterData.get("imeiNo"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	@Description("To remove the added vehicle from the entry")
	@Test(enabled = true,priority = 5, dataProvider = "multiobjectRunningDataProvider")
	public void remove(Map<String, Object> filterData) throws InterruptedException {
		try {
			filterData = convertKeysToCamelCase(filterData);
			clickOnRemove((String) filterData.get("imeiNo"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Description("To change the status active-inactive")
	@Test(enabled = false,priority = 4)
	public void changeStatusBulkUpdate2() throws InterruptedException {
		try {
			clickOnStatus(Integer.parseInt(lastIndex()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
	@Description("To remove the added vehicle from the entry")
	@Test(enabled = false,priority = 5)
	public void remove() throws InterruptedException {
		try {
			Thread.sleep(1000);
			clickOnRemoveBulkUpdate(Integer.parseInt(lastIndex()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@DataProvider(name = "multiobjectRunningDataProvider")
	public Object[][] multiobjectRunningDataProvider(Method method) throws IOException {
		// Fetch data from Excel file using the DataProviders class
		System.setProperty("sheetName", "Object");
		Test testAnnotation = method.getAnnotation(Test.class);
		String sheetName = testAnnotation.description();

		//this line is run use-able only if the page name and sheet name is not present in the test case description and testname
		sheetName = sheetName.isEmpty() || sheetName.equals("") ? System.getProperty("sheetName") : sheetName;

		System.out.println("Found description as sheet name: " + sheetName);

		String filePath="";
		
		if(!(objectFileFromTestXMLFile != null && objectFileFromTestXMLFile.isEmpty())) {
			filePath=objectFileFromTestXMLFile;
		}else {
			String fileName = "Object";
			filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
					+ File.separator + "java" + File.separator + "projects" + File.separator +"trakzee" + File.separator + "testCases"+ File.separator + "web" +File.separator + "multiobjectRunning"
					+ File.separator + fileName + ".xlsx";
			System.out.println("XLS File path: " + filePath);
		}
		
		System.out.println("File Path: "+filePath);
		List<Map<String, Object>> rawData = readExcelFileAndCatchDataUsingColumnNameWithSkipDataSet(filePath, sheetName);

		// Convert List<Map<String, Object>> to Object[][]
		Object[][] data = new Object[rawData.size()][1];
		for (int i = 0; i < rawData.size(); i++) {
			data[i][0] = rawData.get(i); // Each test method receives one Map<String, Object>
		}
		return data;
	}
	
	public List<Map<String, Object>> readExcelFileAndCatchDataUsingColumnNameWithSkipDataSet(String filePath,
			String sheetName) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(new File(filePath));
				Workbook workbook = WorkbookFactory.create(fis)) {

			Sheet sheet = getSheetNameIgnoreCase(workbook, sheetName);
			if (sheet == null) {
				System.out.println("❌ Error Sheet '" + sheetName + "' not found in the file: " + filePath);
				softAssert.fail("❌ Given Sheet '" + sheetName + "' not found in the file: " + filePath);
				throw new SkipException("❌ Error: Sheet '" + sheetName + "' not found in the file: " + filePath);
			}

			// Read header row (column names)
			Iterator<Row> rowIterator = sheet.iterator();
			if (!rowIterator.hasNext()) {
				System.out.println("⚠️ Error: The sheet is empty!");
				return dataList;
			}

			Row headerRow = rowIterator.next();
			List<String> headers = new ArrayList<>();
			int colCount = headerRow.getPhysicalNumberOfCells();
			System.out.println("✅ Found " + colCount + " columns in the header row.");

			for (Cell cell : headerRow) {
				headers.add(cell.getStringCellValue().trim());
			}
			System.out.println("Sheet column names: " + headers);

			// Check if "wantToSkip" column exists (ignoring case)
			int skipColumnIndex = -1;
			for (int i = 0; i < headers.size(); i++) {
				if (headers.get(i).trim().replaceAll(" ", "").equalsIgnoreCase("wantToSkip")) {
					skipColumnIndex = i;
					break;
				}
			}

			boolean hasSkipColumn = skipColumnIndex != -1;
			if (hasSkipColumn) {
				System.out.println("✅ 'wantToSkip' column found at index: " + skipColumnIndex);
			} else {
				System.out.println("⚠️ 'wantToSkip' column not found. No rows will be skipped.");
			}

			// Read data rows
			List<Integer> totalSkipRowsIndexCounter = new ArrayList<>();
			int rowCounter = 0;
			int validDataRows = 0, skippedRows = 0;
			while (rowIterator.hasNext()) {
				rowCounter++;
				Row row = rowIterator.next();
				if (row.getPhysicalNumberOfCells() == 0) {
					continue; // Skip empty rows
				}

				// Check if this row should be skipped
				if (hasSkipColumn) {
					Cell skipCell = row.getCell(skipColumnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					boolean skipRow = skipCell.toString().trim().equalsIgnoreCase("true");
					if (skipRow) {
						skippedRows++;
						totalSkipRowsIndexCounter.add(rowCounter);
						continue; // Skip this row
					}
				}

				// Store row data
				Map<String, Object> rowData = new LinkedHashMap<>(); //it is maintain the order

				//Map<String, Object> rowData = new HashMap<>();
				for (int i = 0; i < headers.size(); i++) {
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					rowData.put(headers.get(i), getCellValue(cell, true));
				}

				dataList.add(rowData);
				validDataRows++;
			}

			System.out.println(
					"✅ Successfully read " + validDataRows + " data rows (Skipped: " + skippedRows
							+ ") and skipped row indexes are" + totalSkipRowsIndexCounter + ".");
			System.setProperty("skipRowIndexes", totalSkipRowsIndexCounter.toString());
		} catch (Exception e) {
			System.out.println("❌ Error: Failed to read the Excel sheet. " + e.getMessage());
			e.printStackTrace();
		}

		return dataList;
	}
	
	public static Sheet getSheetNameIgnoreCase(Workbook workbook, String sheetName) {
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if (workbook.getSheetName(i).equalsIgnoreCase(sheetName)) {
				return workbook.getSheetAt(i);
			}
		}
		return null; // Return null if no matching sheet is found
	}


	private static Object getCellValue(Cell cell,Boolean simpleText) {
		if (simpleText) {
			DataFormatter formatter = new DataFormatter();
			String data = formatter.formatCellValue(cell);
			return data;
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			return cell.getNumericCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		default:
			return "";
		}
	}

	private static ThreadLocal<WebDriver> tldriver = new ThreadLocal<>();

	public static synchronized WebDriver getDriver() {
		return tldriver.get();
	}

	public static void setDriver(WebDriver driverInstance) {
		tldriver.set(driverInstance);
	}

	@AfterClass()
	public static void quitDriver() {
		if (tldriver.get() != null) {
			tldriver.get().quit();
			tldriver.remove();
		}
	}

	public static WebDriver getWebDriver() {
		return getDriver();
	}

	public void setWebDriver(WebDriver driverInstance) {
		setDriver(driverInstance);
	}

	@BeforeClass()
	public void setUpWebDriverInDebuggerMode(ITestContext iTestContext) {
		// Ensure the correct version of ChromeDriver is used
		WebDriverManager.chromedriver().driverVersion("latest").setup();

//		if (!Boolean.parseBoolean(System.getProperty("headless", "false"))) {
//			System.out.println("Setting up WebDriver in debugger mode...");
//			startChromeIfNotRunning(9222, "/home/uffizio/ChromeDebuggerDataGMail",
//					"Profile 1");
//		}
		try {
			// Initialize WebDriver with the given Chrome options
			driver = new ChromeDriver(customizedChromeOptions(true,true, true, true, false, 9222));
			setWebDriver(driver);
		} catch (Exception e) {
			// Capture any errors during initialization and print the stack trace
			System.err.println("Failed to initialize WebDriver: " + e.getMessage());
		}
		
		StoreValues store = StoreValues.load();
		store.printAll();

		// --- Server Handling ---
		serverFromXMLFile = iTestContext.getCurrentXmlTest().getParameter("server");
		if (serverFromXMLFile != null && !serverFromXMLFile.isEmpty()) {
		    store.set("server", serverFromXMLFile);
		    System.out.println("🔐 Saved new server value: " + serverFromXMLFile);
		} else {
		    serverFromXMLFile = store.get("server");
		}

		// Navigate to server URL
		if (serverFromXMLFile != null && serverFromXMLFile.equalsIgnoreCase("live")) {
		    getWebDriver().get("http://13.234.126.218:5757"); // Live
		} else if (serverFromXMLFile != null && serverFromXMLFile.equalsIgnoreCase("local")) {
		    getWebDriver().get("http://192.168.3.177:5000"); // Local
		}

		// --- Object File Handling ---
		objectFileFromTestXMLFile = iTestContext.getCurrentXmlTest().getParameter("objectfile");
		if (objectFileFromTestXMLFile != null && !objectFileFromTestXMLFile.isEmpty()) {
		    store.set("objectfile", objectFileFromTestXMLFile);
		    System.out.println("🔐 Saved new object file path value: " + objectFileFromTestXMLFile);
		} else {
		    objectFileFromTestXMLFile = store.get("objectfile");
		}

		// --- Path File Handling ---
		pathFileFromTestXMLFile = iTestContext.getCurrentXmlTest().getParameter("pathfile");
		if (pathFileFromTestXMLFile != null && !pathFileFromTestXMLFile.isEmpty()) {
		    store.set("pathfile", pathFileFromTestXMLFile);
		    System.out.println("🔐 Saved new path file value: " + pathFileFromTestXMLFile);
		} else {
		    pathFileFromTestXMLFile = store.get("pathfile");
		}

		// --- Summary Logs ---
		System.out.println("✅ Selected server: " + serverFromXMLFile);
		System.out.println("✅ Selected object file: " + objectFileFromTestXMLFile);
		System.out.println("✅ Selected path file: " + pathFileFromTestXMLFile);

	}
	
	public void startChromeIfNotRunning(int port, String userBrowserStoreDataDirectory, String userProfile) {
		if (isPortInUse(port)) {
			System.out.println("Chrome is already running on port " + port);
		} else {
			startChromeWithRemoteDebugging(port, userBrowserStoreDataDirectory, userProfile);
		}
	}
	
	private void startChromeWithRemoteDebugging(int port, String userBrowserStoreDataDirectory, String userProfile) {
		try {
			String chromePath = "";
			String os = System.getProperty("os.name").toLowerCase();
			System.out.println("Detected OS: " + System.getProperty("os.name"));

			if (os.contains("win")) {
			    chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
			} else if (os.contains("nux")) {
			    chromePath = "/usr/bin/google-chrome";
			} else if (os.contains("mac")) {
			    chromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
			}


			String portArg = "--remote-debugging-port=" + port;
			String userDataDirArg = "--user-data-dir=" + userBrowserStoreDataDirectory;
			String profileDirArg = "--profile-directory=" + userProfile; // Add profile-directory

			// Include both the user-data-dir and profile-directory arguments
			ProcessBuilder processBuilder = new ProcessBuilder(chromePath, portArg, userDataDirArg, profileDirArg);
			Process process = processBuilder.start();

			System.out.println("Chrome started on remote debugging on port " + port);
		} catch (IOException e) {
			System.out.println("Error occurred while starting Chrome: " + e.getMessage());
		}
	}

	private boolean isPortInUse(int port) {
		try (Socket socket = new Socket("localhost", port)) {
			return true; // Port is in use
		} catch (IOException e) {
			return false; // Port is not in use
		}
	}

	private ChromeOptions customizedChromeOptions(boolean acceptInsecure,boolean blockAdsAndNotifications, boolean headlessBrowsing,
			boolean incognitoMode, boolean debuggerMode, int debuggerPort) {

		// TO INITIALIZE CHROME OPTIONS
		ChromeOptions options = new ChromeOptions(); // Use the correct class name and variable

		if(acceptInsecure) {
			options.setAcceptInsecureCerts(true); // This is the key
			options.addArguments("--ignore-certificate-errors");
	        options.addArguments("--allow-insecure-localhost");
	        options.addArguments("--disable-web-security");
	        options.addArguments("--disable-gpu");
	        options.addArguments("--no-sandbox");
	        options.addArguments("--disable-dev-shm-usage");
	     // Optional: to suppress HTTPS warning for localhost-style IPs
	        options.addArguments("--user-data-dir=/tmp/chromeprofile");
	        options.addArguments("--test-type");
	        options.addArguments("--unsafely-treat-insecure-origin-as-secure=http://13.234.126.218:5757");

		}
		
		if (blockAdsAndNotifications) {
			// Disable pop-ups and intrusive ads
			options.addArguments("--disable-popup-blocking");
			options.addArguments("--disable-notifications");
			options.addArguments("--disable-ads");
			System.out.println("Disabled Ads and Notifications");
		}
		if (headlessBrowsing) {
			// FOR HEADER LESS BROWSING
			options.addArguments("--headless");
			System.out.println("Entered into headless browsing");
		}

		if (incognitoMode) {
			// TO OPEN CHROME DRIVER INTO INCOGNITO MODE
			options.addArguments("--incognito");
			System.out.println("Entered into incognito mode");

		}

		if (debuggerMode) {
			// TO USE CHROME DRIVER IN DEBUGGER MODE
			options.setExperimentalOption("debuggerAddress", "localhost:" + debuggerPort);
			System.out.println("Debugging mode with port:" + debuggerPort);
		}

		// Set the path to Chrome binary if it’s not in the default location
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"); // Path for Windows (Update as needed)
		} else if (System.getProperty("os.name").toLowerCase().contains("nix")
				|| System.getProperty("os.name").toLowerCase().contains("nux")) {
			options.setBinary("/usr/bin/google-chrome"); // Path for Linux

		}

		return options;
	}

}
