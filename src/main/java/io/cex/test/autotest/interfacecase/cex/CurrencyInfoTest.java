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
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.currencyInfoUrl;
@Feature("CurrencyInfo币种资料信息")

public class CurrencyInfoTest extends BaseCase {
    @Test(description = "输入错误的币种，返回100006")
    public void currencyInfoTestError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", "IDAR");
        Response response = OkHttpClientManager.post(ip_gateway+currencyInfoUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }

    @Test(description = "输入空的币种，返回100006")
    public void currencyInfoTestError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", "");
        Response response = OkHttpClientManager.post(ip_gateway+currencyInfoUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void currencyInfoTest() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", "IDA");
        Response response = OkHttpClientManager.post(ip_gateway+currencyInfoUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.isContainsExpect("\"officialWebsite\":\"www.ida.im\"",rspjson.get("data").toString());
    }

}
