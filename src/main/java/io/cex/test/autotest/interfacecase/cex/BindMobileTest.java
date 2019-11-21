package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;


@Feature("BindMobileTest绑定手机接口")
@Slf4j

public class BindMobileTest extends BaseCase {
    //cex登陆token
    private String token = null;
    //随机手机号，用于绑定
    private String randomPhone = null;
    //随机邮箱，用于注册
    private String randomEmail = null;

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "邮箱注册", retryAnalyzer = Retry.class)
    public void testRegister() throws IOException {
        randomEmail = RandomUtil.getRandomMail();
        JSONObject object = new JSONObject();
        object.put("identifier", randomEmail);
        object.put("loginPwd", pwd);
        object.put("verifyCode", "111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip_gateway + registerUrl, jsonbody.toJSONString(),
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
        token = CexCommonOption.userCexLogin(randomEmail, pwd, area);
        AssertTool.assertNotEquals(null, token);
        log.info("------------cex token:" + token);
        Allure.addAttachment("登陆token：", token);
    }

    /**
     * @param
     * @desc 异常用例的数据驱动
     **/
    @DataProvider(parallel = true)
    public Object[][] BindMobileErrorData(Method method) {
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/BindMobile/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }

    /**
     * @param
     * @desc 异常用例
     **/
    @Test(dependsOnMethods = "testLogin",dataProvider = "BindMobileErrorData", description = "BindMobile异常用例")
    public void testBindMobileError(Map<?, ?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data", object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Allure.addAttachment(param.get("comment").toString() + "入参", jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway + bindmobileUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(), rspjson.get("code").toString());
    }

    //绑定手机，返回000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testBindMobileError",description = "BindMobile正常用例")
    public void testBindMobile() throws IOException {
        JSONObject object = new JSONObject();
        randomPhone = RandomUtil.getRandomPhoneNum();
        object.put("mobileArea", area);
        object.put("mobile", randomPhone);
        object.put("smsCode", "123456");
        object.put("verifyCode", "654321");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        System.out.println("rucan"+jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway + bindmobileUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        System.out.println("chucan" +rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    //不传token，返回100006
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testBindMobileError",description = "BindMobile异常用例8")
    public void testBindMobileError8() throws IOException {
        JSONObject object = new JSONObject();
        object.put("mobileArea", area);
        object.put("mobile", "17812345678");
        object.put("smsCode", "123456");
        object.put("verifyCode", "654321");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        System.out.println("rucan"+jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway + bindmobileUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        System.out.println("chucan" +rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}
