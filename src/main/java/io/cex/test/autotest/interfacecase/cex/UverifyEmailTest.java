package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.uverifyEmailUrl;
@Feature("verifySmsTest登录后发送邮箱验证码")
@Slf4j

public class UverifyEmailTest extends BaseCase {
    @Test(description = "没有token的异常用例")
    public void uverifyEmailTestError1() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        object.put("authType", "WITHDRAW_COIN");
        object.put("email", presetUser);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + uverifyEmailUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    @Test(description = "邮箱为空")
    public void uverifyEmailTestError2() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        object.put("authType", "WITHDRAW_COIN");
        object.put("email", "");
        jsonbody.put("data", object);
        HashMap header = dataInit();
        //String token = userCexLogin("18780050294",pwd,area);
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + uverifyEmailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        //System.out.println("-------------liuxm"+rspjson.toJSONString());
        AssertTool.isContainsExpect("infrastructure-100030", rspjson.get("code").toString());
    }
    @Test(description = "类型传登录LOGIN，返回100006")
    public void uverifyEmailTestError3() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        object.put("authType", "LOGIN");
        object.put("email", presetUser);
        jsonbody.put("data", object);
        HashMap header = dataInit();
        //String token = userCexLogin("18780050294",pwd,area);
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + uverifyEmailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        //System.out.println("-------------liuxm"+rspjson.toJSONString());
        log.info("----------------打印出失败原因"+rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "检查返回是否成功")
    public void uverifyEmailTest() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        object.put("authType", "WITHDRAW_COIN");
        object.put("email", presetUser);
        jsonbody.put("data", object);
        HashMap header = dataInit();
        //String token = userCexLogin("18780050294",pwd,area);
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + uverifyEmailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        //System.out.println("-------------liuxm"+rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }
}
