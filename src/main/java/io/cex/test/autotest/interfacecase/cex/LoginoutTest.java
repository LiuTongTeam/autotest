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
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("logout接口")
@Slf4j

public class LoginoutTest extends BaseCase {
    //cex登陆token
    private String token = null;
    //随机手机号，用于注册
    private String randomPhone = null;

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "注册", retryAnalyzer = Retry.class)
    public void testRegister() throws IOException {
        randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = new JSONObject();
        object.put("identifier", randomPhone);
        object.put("loginPwd", pwd);
        object.put("mobileArea", area);
        object.put("verifyCode", "111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip + registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        log.info("-------------register response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegister", description = "登陆")
    public void testLogin() {
        token = CexCommonOption.userCexLogin(randomPhone, pwd, area);
        AssertTool.assertNotEquals(null, token);
        log.info("------------cex token:" + token);
        Allure.addAttachment("登陆token：", token);
    }

    //异常测试lang传空值
    @Test(dependsOnMethods = "testLogin",description = "Loginout异常用例1:lang传空值")
    public void testloginoutError1()  throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", "");
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Response response = OkHttpClientManager.post(ip + loginoutUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

    //异常测试不传token
    @Test(description = "Loginout异常用例2:不传token")
    public void testloginoutError2() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip + loginoutUrl, jsonbody.toJSONString(),
                "application/json");
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

    //正常测试
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testloginoutError1", description = "Loginout正常登录后退出")
    public void testloginout() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Response response = OkHttpClientManager.post(ip + loginoutUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    //异常测试token失效
    @Test(dependsOnMethods = "testloginout",description = "Loginout异常用例3：token失效后退出")
    public void testloginoutError3()  throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Response response = OkHttpClientManager.post(ip + loginoutUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100000", rspjson.get("code").toString());
    }
}