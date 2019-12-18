package io.cex.test.autotest.uicase.webcase;

import io.cex.test.framework.ui.WebBaseCase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;

/**
 * @Classname BaseCase
 * @Description TODO
 * @Date 2019/12/17  15:39
 * @Created by shenqingyan
 */
public class BaseCase extends WebBaseCase {
    public static WebDriver webDriver = null;
    @BeforeTest
    public void initData(){
/*        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        webDriver = initDriver("chrome");*/
        System.setProperty("webdriver.gecko.driver","C:\\tools\\geckodriver-v0.26.0-win64\\geckodriver.exe");
        webDriver = initDriver("firefox");
    }

    @AfterClass
    public void clearData(){
        webDriver.quit();
    }
}
