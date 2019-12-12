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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.symbolquotationgroupUrl;

@Feature("symbolQuotationGroupByInTradeArea交易对信息根据计价币种进行归类接口")

public class SymbolQuotationGroupTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "输入正确的参数，成功返回交易对信息")
    public void testsymbolQuotation() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data","");
        Response response = OkHttpClientManager.post(ip+symbolquotationgroupUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.assertEquals(JSON.parseArray(rspjson.getString("data")).size()>0,true);
    }

}
