package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.math.BigDecimal;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.recommendSymbolQuotationUrl;

@Feature("RecommendSymbolQuotation热门交易对测试接口")

public class RecommendSymbolQuotationTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "输入正确的参数，成功返回交易对信息")
    public void recommendSymbolQuotation() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip+recommendSymbolQuotationUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        JSONArray recommendSymbolList = JSON.parseArray(JsonPath.read(rspjson,"$.data.recommendSymbolList").toString());
        //断言recommendSymbolList不为空
        AssertTool.assertEquals(recommendSymbolList.size()>0,true);
        //断言klineList不为空
        AssertTool.assertEquals(JSON.parseArray(JSON.parseObject(recommendSymbolList.getString(0)).get("klineList").toString()).size()>0,true);

    }
}
