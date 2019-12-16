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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("WithdrawInfo接口测试")

public class WithdrawInfoTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例")
    public void withdrawInfo() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currencyCode",depositCurrency);
        jsonbody.put("currencyAliasName",depositCurrency);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway + withdrawInfoUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        String availableTotalAmount = JSON.parseObject(rspjson.get("data").toString()).getString("availableTotalAmount");
        //断言totalAmount大于0
        AssertTool.assertEquals(new BigDecimal(availableTotalAmount).compareTo(new BigDecimal(0)),1);
    }
    @Test(description = "异常用例：没有输入token")
    public void withdrawInfoError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currencyCode",depositCurrency);
        jsonbody.put("currencyAliasName",depositCurrency);
        Response response = OkHttpClientManager.post(ip_gateway + withdrawInfoUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    @Test(description = "异常用例：currencyCode为空")
    public void withdrawInfoError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currencyCode","");
        jsonbody.put("currencyAliasName",depositCurrency);
        Response response = OkHttpClientManager.post(ip_gateway + withdrawInfoUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    @Test(description = "异常用例：currencyAliasName为空")
    public void withdrawInfoError2() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currencyCode",depositCurrency);
        jsonbody.put("currencyAliasName","");
        Response response = OkHttpClientManager.post(ip_gateway + withdrawInfoUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}
