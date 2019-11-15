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
@Feature("FullArticle新闻详情、滚动通知详情接口")

public class FullArticleTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，有token返回000000")
    public void fullArticleTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("bizId", "1630489093291929602");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+fullArticleUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，没有token返回000000")
    public void fullArticleTest1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("bizId", "1630489093291929602");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip_gateway+fullArticleUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    @Test(description = "异常用例，bizId为空")
    public void fullArticleTestError() throws IOException {
        JSONObject object = new JSONObject();
        object.put("bizId", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip_gateway+fullArticleUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
}
