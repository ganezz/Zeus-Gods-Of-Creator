package MobileUtility;

import CommonUtilities.RandomDataGenerator;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static MobileUtility.PcloudyConnection.appiumDriver;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class IOSUtility {

    IOSDriver driver;
    TouchAction touch;

    RandomDataGenerator rg = new RandomDataGenerator();


    public IOSUtility(){
        driver = (IOSDriver) appiumDriver;
        touch = new TouchAction(driver);
    }


    public void screenshot(String name) throws IOException {
        File srcFile=driver.getScreenshotAs(OutputType.FILE);
        String filename = rg.getTimeStamp() + name;
        File targetFile=new File(System.getProperty("user.dir")+"/screenshot/" + filename +".jpg");
        FileUtils.copyFile(srcFile,targetFile);
    }


    public void hideKeyboard() throws InterruptedException {
        try {
            driver.hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE);
        }catch (Exception e){
            System.out.println(e);
            try {
                driver.getKeyboard().pressKey(Keys.RETURN);
            }catch (Exception e1){
                System.out.println(e1);
                driver.hideKeyboard();
            }
        }
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
        hideKeyboard();
    }

    public void enterClick(WebElement element) throws Exception {
        element.sendKeys(Keys.TAB);
    }

    public void click(WebElement element) throws Exception {
        try {
            element.click();
        }catch (Exception e){
            System.out.println("Tapping...");
            tapByElement(element);
        }
    }

    public void tapByElement(WebElement element) throws IOException {
        int startX = element.getLocation().getX();
        int addition = (int) (element.getSize().width * 0.25);
        int endX = startX + addition;
        int startY = element.getLocation().getY();
        int endY = startY + (int) (element.getSize().height * 0.25);
        System.out.println(startX+" + "+addition +"" +endX +" "+startY+" "+endY);
        new TouchAction(driver).tap(point(endX, endY)).perform();
    }

    public String getLastValueInDropDown(List<WebElement> elementList) throws Exception {
        String lastValue = "";
        waitForFirstElement(elementList);
        System.out.println("List Size :"+elementList.size());
        int i = 50;
        while (i!=0){
            if (elementList.size() == 0 )
                break;
            i++;
        }
        for(WebElement elm : elementList){
            lastValue = elm.getText();
        }
        return lastValue;
    }


    public void scrollToElement(By elm) {
        RemoteWebElement element = (RemoteWebElement) driver.findElement(elm);
        String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", elementID);
        scrollObject.put("toVisible", "not an empty string");
        driver.executeScript("mobile:scroll", scrollObject);
    }

    public  void scroll(String name) throws InterruptedException {
        hideKeyboard();
        RemoteWebElement element = (RemoteWebElement) driver.findElement(By.xpath("//*[@name='" + name + "']"));
        String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", elementID);
        scrollObject.put("toVisible", "not an empty string");
        driver.executeScript("mobile:scroll", scrollObject);
        Thread.sleep(3000);
    }


    public void scrollToElement(WebElement elm) throws InterruptedException {
        int scroll = 30;
        while (0 != scroll--) {
            try {
                elm.isDisplayed();
                break;
            } catch (Exception e) {
                touch.press(point(50, 400)).waitAction(waitOptions(ofMillis(10))).moveTo(point(50, 50)).perform();
            }
        }
    }

    public void singleScroll(){
        touch.press(point(50, 600)).waitAction(waitOptions(ofMillis(10))).moveTo(point(50, 50)).perform();
    }

    public void listSelection(WebElement element, List<WebElement> webElementList, String value) throws Exception {
        waitForElementClickable(element);
        click(element);
        selectFromDropDown(webElementList, value);
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
        System.out.println("List Size : "+listELm.size());
        while (!(listELm.size() >= 1)){
            waitForSeconds(1);
            if (i == 60) {
                Assert.fail("Drop down not found");
                break;
            }
            System.out.println("Iteration : "+i);
            i++;
        }
    }

    public void assertMessage(WebElement element, String message) {
        waitForElement(element);
        System.out.println(element.getText() +" = " +message);
        Assert.assertEquals(element.getText().trim(),message);
    }

    public boolean checkSameTextEnteredPresent(WebElement element, String text) {
        System.out.println(element.getText()+" = "+text);
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


    public String getText(WebElement element) {
        System.out.println(element.getText());
        return element.getText();
    }

    public void webViewConverter() throws Exception {
        int count =0 ;
        boolean flag = true;
       waitForSeconds(5);
        while (count++ != 10 && flag) {
                Set<String> contextNames = driver.getContextHandles();
                System.out.println("Context Checking");
                for (String contextName : contextNames) {
                    System.out.println(contextName);
                    if (contextName.contains("WEBVIEW")) {
                        driver.context(contextName);
                        System.out.println("Switched to " + contextName);
                        flag=false;
                        break;
                    }
                }
                System.out.println(" Retrying .. "+count);
               waitForSeconds(2);
        }
    }
}
