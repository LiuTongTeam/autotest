package io.cex.test.autotest.uicase.webcase;

import io.cex.test.autotest.uicase.pageobj.web.LoginPage;
import io.cex.test.autotest.uicase.pageobj.web.WithdrawPage;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.listener.TestngListener;
import io.cex.test.framework.ui.WebBaseCase;
import io.cex.test.framework.ui.WebElementAction;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import org.aspectj.lang.annotation.Before;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.cex.test.autotest.uicase.UIConfig.*;

/**
 * @Classname LoginTest
 * @Description TODO
 * @Date 2019/12/17  16:13
 * @Created by shenqingyan
 */
@Feature("LoginTestUI测试")
public class LoginTest extends WebBaseCase {
/*
    WebElementAction webElementAction = new WebElementAction();
    @BeforeClass
    public void before(){
        webDriver.get(webUrl);
    }
*/

    @Test
    public void testLogin() throws IOException,InterruptedException {
        webDriver.get(webUrl);
        WebElementAction webElementAction = new WebElementAction();

        Allure.addAttachment("1","初始化登陆页面对象");
        LoginPage loginPage = new LoginPage();
        Allure.addAttachment("2","最大化浏览器窗口");
        webElementAction.windowMaxSize();
        Allure.addAttachment("3","点击右上角登陆");
        webElementAction.click(loginPage.cex_login());
        Allure.addAttachment("4","点击邮箱登陆");
        webElementAction.click(loginPage.cex_mail_login());
        Allure.addAttachment("5","输入邮箱");
        webElementAction.sendKeys(loginPage.mail_input(),loginEmail);
        Allure.addAttachment("6","输入密码");
        webElementAction.sendKeys(loginPage.pwd_input(),loginPwd);
        Allure.addAttachment("7","点击登陆按钮");
        webElementAction.click(loginPage.login_button());
        Thread.sleep(2000);
        Allure.addAttachment("8","输入邮箱验证码");
        webElementAction.sendKeys(loginPage.email_code_input(),"123456");
        Allure.addAttachment("9","点击确定按钮");
        webElementAction.click(loginPage.email_code_confirm_button());
    }

    @Test(dependsOnMethods = "testLogin")
    public void testWithdraw() throws IOException,InterruptedException{
        webDriver.get(webUrl);
        WebElementAction webElementAction = new WebElementAction();
        Allure.addAttachment("1","初始化提币页面对象");
        WithdrawPage withdrawPage = new WithdrawPage();
        Allure.addAttachment("1","点击资产管理");
        webElementAction.click(withdrawPage.asset_manage());
        Allure.addAttachment("1","点击提币");
        /*webElementAction.click(withdrawPage.withdraw());
        Allure.addAttachment("1","选择币种");*/
        webElementAction.selectByIndex(withdrawPage.currency_select(),0);
        Thread.sleep(2000);
        Allure.addAttachment("2","点击选择地址按钮");
        webElementAction.click(withdrawPage.chose_addr());
        Allure.addAttachment("3","选择地址");
        webElementAction.click(withdrawPage.click_addr());
        Allure.addAttachment("4","输入数量");
        webElementAction.sendKeys(withdrawPage.amount_input(),"1");
        Allure.addAttachment("5","确认提交");
        webElementAction.click(withdrawPage.submit_click());
    }
}
