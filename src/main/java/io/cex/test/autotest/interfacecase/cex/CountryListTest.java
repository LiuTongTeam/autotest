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
@Feature("CountryList接口")
public class CountryListTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例")
    public void countryList() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip + countryListUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        AssertTool.isContainsExpect("{\"code\":\"US\",\"mobileArea\":\"1\",\"name\":\"United States\",\"countryId\":5}", rspjson.get("data").toString());
    }
    @Test(description = "异常用例：body为空,也返回成功，该接口依靠header的语言来的")
    public void countryListError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        Response response = OkHttpClientManager.post(ip + countryListUrl,jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

}
