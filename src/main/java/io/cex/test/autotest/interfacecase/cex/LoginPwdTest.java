package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.registerUrl;
@Feature("LoginPwd登录后重置登录密码")

public class LoginPwdTest extends BaseCase {
    //用随机手机号注册,然后修改手机号的登录密码
    //随机手机号，用于注册
    private String randomPhone = null;
    private String token = null;



    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "注册", retryAnalyzer = Retry.class)
    public void testRegister() throws IOException {
        randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = new JSONObject();
        object.put("identifier", randomPhone);
        object.put("loginPwd", pwd);
        object.put("mobileArea", area);
        object.put("verifyCode", "912121");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip_gateway + registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());

    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegister", description = "先注册，然后登录")
    public void testLogin(){
        token = CexCommonOption.userCexLogin(randomPhone,pwd,area);
        AssertTool.assertNotEquals(null,token);
        Allure.addAttachment("登陆token：",token);
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testLogin", description = "登录后，重置登录密码")
    public void loginPwdTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("newPwd", "afdd0b4ad2ec172c586e2160880fbf9e");
        object.put("verifyCode", "912121");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Response response = OkHttpClientManager.post(ip_gateway + loginPwdUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());

    }
    @Test(description = "异常用例，没有token,重置登录密码")
    public void loginPwdTestError() throws IOException {
        JSONObject object = new JSONObject();
        object.put("newPwd", "afdd0b4ad2ec172c586e2160880fbf9e");
        object.put("verifyCode", "912121");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip_gateway + loginPwdUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());

    }

}
