package CommonUtilities;

import BrowserInitiation.TestBase;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class QuickUtility extends TestBase {

    //Quick Utility are Short Hand Utility for Reduce Line of Code

    WebDriver localDriver = TestBase.getDriver();
    
    JavascriptExecutor javaScriptControls;


    public QuickUtility(){
        PageFactory.initElements(localDriver,this);
        javaScriptControls = (JavascriptExecutor)localDriver;
    }

    public void sendKeys(WebElement element, String value) throws Exception {
        element.clear();
        element.sendKeys(value);
    }

    public void waitForElement(WebElement element){
        WebDriverWait wait = new WebDriverWait(localDriver,100);
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public void waitForElementClickable(WebElement element){
        WebDriverWait wait = new WebDriverWait(localDriver,100);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void click(WebElement element) throws Exception {
        try {
            element.click();
        }catch (Exception e){
            javaScriptControls.executeScript("arguments[0].scrollIntoView();", element);
            javaScriptControls.executeScript("arguments[0].click();", element);
        }
    }

    public void listSelection(WebElement element, List<WebElement> webElementList, String value) throws Exception {
        javaScriptControls.executeScript("arguments[0].scrollIntoView();", element);
        waitForElementClickable(element);
        click(element);
        selectFromDropDown(webElementList,value);
    }

    public void selectFromDropDown(List<WebElement> elementList, String value) throws Exception {
        waitForFirstElement(elementList);
        for(WebElement elm : elementList){
            if (elm.getText().equals(value)){
                click(elm);
                break;
            }
        }
    }

    public void listSelectionByData(WebElement element, List<WebElement> webElementList, String value) throws Exception {
        javaScriptControls.executeScript("arguments[0].scrollIntoView();", element);
        waitForElementClickable(element);
        click(element);
        selectFromDropDownByData(webElementList,value);
    }

    public void selectFromDropDownByData(List<WebElement> elementList, String value) throws Exception {
        waitForFirstElement(elementList);
        for(WebElement elm : elementList){
            if (elm.getAttribute("data-value").equals(value)){
                click(elm);
                break;
            }
        }
    }


    public void checkListContains(List<WebElement> elementList, String value) {
        boolean flag = true;
        for(WebElement elm : elementList){
            if (elm.getText().equals(value)){
                flag = false;
                break;
            }
        }
        if (flag)
            Assert.fail("List value is not found");
    }

    public boolean verifyListContains(List<WebElement> elementList, String value) {
        boolean flag = false;
        for(WebElement elm : elementList){
            if (elm.getText().equals(value)){
                flag = true;
                break;
            }
        }
       return flag;
    }

    public void assertMessage(WebElement element, String message) {
        waitForElement(element);
        Assert.assertEquals(element.getText().trim(),message);
    }

    public void assertClassName(WebElement element, String message) {
        waitForElement(element);
        Assert.assertTrue(element.getAttribute("class").contains(message));
    }

    public void selectTagSelection(WebElement element, String value) throws Exception {
        new Select(element).selectByValue(value);
    }

    public void waitForSeconds(int timeoutInSeconds) throws Exception {
        Thread.sleep(timeoutInSeconds * 1000);
    }

    public void multiSelection(WebElement element, List<WebElement> elementList, String value) throws Exception {
        javaScriptControls.executeScript("arguments[0].scrollIntoView();", element);
        click(element);
        String[] valueArray = value.split(",");
        for (String val : valueArray) {
            selectFromDropDown(elementList, val.trim());
        }
    }

    public void waitForFirstElement(List<WebElement> listELm) throws Exception {
        int i = 0;
        while (!(listELm.size() >= 1)){
            waitForSeconds(1);
            if (i == 60) {
                Assert.fail("Drop down / List not found");
                break;
            }
            i++;
        }
    }


    public void waitForFirstElementPro(List<WebElement> listELm) throws Exception {
        int i = 0;
        while (!(listELm.get(0).getText().length() >= 1)){
           waitForSeconds(1);
            if (i == 60) {
                Assert.fail("Drop down not found");
                break;
            }
            i++;
        }
    }
    
    public void verifyListContainsAllValues(WebElement element, List<WebElement> elementList, List<Map<String, String>> allListValues) throws Exception {
        javaScriptControls.executeScript("arguments[0].scrollIntoView();", element);
        waitForElementClickable(element);
        click(element);
        System.out.println("Access List "+elementList.size());
        for (Map<String, String> dataList : allListValues) {
            checkListContains(elementList,dataList.get("ApplicationName").trim());
        }
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

}
