package MobileUtility;


import CommonUtilities.RandomDataGenerator;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static MobileUtility.PcloudyConnection.appiumDriver;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static java.time.Duration.ofMillis;


public class AndroidUtility {
    AndroidDriver driver;
    TouchAction touch;

    RandomDataGenerator rg = new RandomDataGenerator();


    public AndroidUtility(){
        driver = (AndroidDriver) appiumDriver;
        PageFactory.initElements(driver, this);
        touch = new TouchAction(driver);
    }

    public void waitForElement(WebElement element){
        WebDriverWait wait = new WebDriverWait(appiumDriver,100);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementClickable(WebElement element){
        WebDriverWait wait = new WebDriverWait(appiumDriver,100);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void sendKeys(WebElement element, String value) throws Exception {
        element.clear();
        element.sendKeys(value);
    }

    public void enterTab(WebElement element) throws Exception {
        element.sendKeys(Keys.ENTER);
    }

    public void click(WebElement element) throws Exception {
            scrollToElement(element);
            element.click();
    }


    public String getLastValueInDropDown(List<WebElement> elementList) throws Exception {
        waitForFirstElement(elementList);
        return elementList.get(elementList.size()-1).getText();
    }

    public void screenshot(String name) throws IOException {
        File srcFile=driver.getScreenshotAs(OutputType.FILE);
        String filename = rg.getTimeStamp() + name;
        File targetFile=new File(System.getProperty("user.dir")+"/screenshot/" + filename +".jpg");
        FileUtils.copyFile(srcFile,targetFile);
    }


    public  void scroll(String element) throws InterruptedException {
        driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""+element+"\").instance(0))");
        Thread.sleep(2000);
    }


    public void scrollToElement(WebElement element) throws InterruptedException {
        int scroll = 30;
        while (0 != scroll--) {
            try {
                element.isDisplayed();
                System.out.println("Element Visible");
                break;
            } catch (Exception e) {
                touch.press(PointOption.point(50, 400)).waitAction(waitOptions(ofMillis(10))).moveTo(PointOption.point(50, 50)).perform();
            }
        }
    }

    public void singleScroll(){
        touch.press(PointOption.point(50, 600)).waitAction(waitOptions(ofMillis(10))).moveTo(PointOption.point(50, 50)).perform();
    }

    public void listSelection(WebElement element, List<WebElement> webElementList, String value) throws Exception {
        scrollToElement(element);
        waitForElementClickable(element);
        click(element);
        selectFromDropDown(webElementList,value);
    }

    public void selectFromDropDown(List<WebElement> elementList, String value) throws Exception {
        waitForFirstElement(elementList);
        for(WebElement elm : elementList){
            System.out.println(elm.getText()+" = "+value);
            if (elm.getText().equals(value)){
                click(elm);
                break;
            }
        }
    }

    public void waitForFirstElement(List<WebElement> listELm) throws Exception {
        int i = 0;
        while (!(listELm.size() >= 1)){
           waitForSeconds(1);
            if (i == 60) {
                Assert.fail("Drop down not found");
                break;
            }
            i++;
        }
    }

    public void assertMessage(WebElement element, String message) {
        waitForElement(element);
        System.out.println(element.getText() +" = " +message);
        Assert.assertEquals(element.getText().trim(),message);
    }

    public boolean checkSameTextEnteredPresent(WebElement element, String text) {
        return element.getText().trim().equals(text);
    }

    public void waitForTextVisible(WebElement element) throws Exception {
        int i = 50;
        while (element.getText().isEmpty() && i!=0) {
            waitForSeconds(1);
            i--;
        }
    }

    public void waitForSeconds(int timeoutInSeconds) throws Exception {
        Thread.sleep(timeoutInSeconds * 1000);
    }

    public void waitForElementTextVisible(WebElement element, String text) throws Exception {
        int i = 50;
        while (!(element.getText().equals(text))) {
            waitForSeconds(1);
            i--;
        }
    }

}
