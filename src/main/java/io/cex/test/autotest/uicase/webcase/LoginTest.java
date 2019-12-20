package io.cex.test.autotest.uicase.webcase;

import io.cex.test.autotest.uicase.pageobj.web.LoginPage;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.listener.TestngListener;
import io.cex.test.framework.ui.WebBaseCase;
import io.cex.test.framework.ui.WebElementAction;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @Classname LoginTest
 * @Description TODO
 * @Date 2019/12/17  16:13
 * @Created by shenqingyan
 */
@Feature("LoginTestUI测试")
public class LoginTest extends WebBaseCase {
    @Test
    public void testOpen() throws IOException {
        webDriver.get("http://139.9.55.125");
        WebElementAction webElementAction = new WebElementAction();
        webElementAction.refresh();
        LoginPage loginPage = new LoginPage();
        webElementAction.windowMaxSize();
        webElementAction.click(loginPage.cex_login());
        webElementAction.click(loginPage.cex_register());
    }
}
