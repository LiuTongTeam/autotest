package io.cex.test.autotest.uicase.webcase;

import io.cex.test.autotest.uicase.pageobj.web.LoginPage;
import io.cex.test.framework.ui.WebElementAction;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @Classname LoginTest
 * @Description TODO
 * @Date 2019/12/17  16:13
 * @Created by shenqingyan
 */
public class LoginTest extends BaseCase{
    @Test
    public void testOpen() throws IOException {
        webDriver.get("http://139.9.55.125");
        WebElementAction webElementAction = new WebElementAction(webDriver);
        webElementAction.refresh();
        LoginPage loginPage = new LoginPage();
        webElementAction.windowMaxSize();
        webElementAction.click(loginPage.cex_login());
    }
}
