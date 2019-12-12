package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
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
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.exchangeRateUrl;
@Feature("ExchangeRateTest法币汇率接口测试")
public class ExchangeRateTest extends BaseCase {

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void exchangeRateTest() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+exchangeRateUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        //断言汇率列表list不为空
        AssertTool.assertEquals(JSON.parseArray(JsonPath.read(rspjson,"$.data.exchangeRateList").toString()).size()>0,true);

    }

}
