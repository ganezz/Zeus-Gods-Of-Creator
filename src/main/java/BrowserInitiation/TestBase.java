package BrowserInitiation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;


public class TestBase {
    protected static Properties props = new Properties();
    public static ThreadLocal<WebDriver> webdriver = new ThreadLocal<WebDriver>();
    public static DesiredCapabilities capabilities;
    private static Logger LOGGER = LogManager.getLogger(TestBase.class);


    /**To load the project configuration files **/
    public TestBase() {

        FileInputStream ip;
        try {
            ip = new FileInputStream(new File("src/main/resources/project.properties"));
            props.load(ip);
            LOGGER.info("properties files loaded in TestBase Constructor");

        } catch (Exception e) {
            LOGGER.error("Error in creating properties loader in TestBase Constructor");
        }
    }

    /**
     *      * Run Test in Selenium GRID when runMode is "hub" and run test in local browser when runMOde is standalone
     *      * Run tests in all different browser such as Chrome, IE, Firefox
     */

    public static void initialization() throws Exception {
        WebDriver driver = null;
        LOGGER.info("Instantiate browser details");
        String browserType = props.getProperty("browser.type");
        LOGGER.info("browserName = "+browserType);
        LOGGER.info("Browser name is set");
        String runMode = props.getProperty("grid.runmode");
        String browser = browserType.trim();
        LOGGER.info("Browser name trim if space");
        if ("hub".equalsIgnoreCase(runMode)) {
            capabilities = new DesiredCapabilities();
            if ("chrome".equalsIgnoreCase(browser)) {
                capabilities.setBrowserName(BrowserType.CHROME);
            } else if ("firefox".equalsIgnoreCase(browser)) {
                capabilities.setBrowserName(BrowserType.FIREFOX);
            } else if ("ie".equalsIgnoreCase(browser)) {
                capabilities.setBrowserName(BrowserType.IE);
            }
            String host = props.getProperty("grid.seleniumHubHost");
            driver = new RemoteWebDriver(new URL(host + "wd/hub"),capabilities);
            webdriver.set(driver);
        } else if ("chrome".equalsIgnoreCase(browser)) {
            LOGGER.info("setting chromedriver path details");
            System.setProperty("webdriver.chrome.driver", props.getProperty("browser.chrome.driverPath"));
            ChromeOptions options = new ChromeOptions();
            LOGGER.info("setting insecure certificate options");
            options.setAcceptInsecureCerts(true);
            if ("true".equalsIgnoreCase(props.getProperty("browser.web.headlessmode", "false"))) {
                LOGGER.info("setting headless moe and jenkins config enabled");
                options.addArguments("--no-sandbox");
                options.addArguments("--headless");
                options.setHeadless(true);
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920x1080");

            }
            driver = new ChromeDriver(options);
            webdriver.set(driver);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            System.setProperty("webdriver.gecko.driver", props.getProperty("browser.firefox.driverPath"));
            FirefoxOptions options = new FirefoxOptions();
            options.setAcceptInsecureCerts(true);
            if ("true".equalsIgnoreCase(props.getProperty("browser.web.headlessmode", "false"))) {
                options.setHeadless(true);
            }
            driver = new FirefoxDriver(options);
            webdriver.set(driver);
        } else if ("ie".equalsIgnoreCase(browser)) {
            System.setProperty("webdriver.ie.driver", props.getProperty("browser.ie.driverPath"));
            InternetExplorerOptions options = new InternetExplorerOptions();
            options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
            driver = new InternetExplorerDriver(options);
            webdriver.set(driver);
        } else {
            throw new Exception("Browser is invalid");
        }
    }


    public static WebDriver getDriver()
    {
        return webdriver.get();
    }

    public static void setDriver(WebDriver driver) {
        webdriver.set(driver);
    }
}
