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
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.batchChannelTokenUrl;
@Feature("BatchChannelTokenTest获取渠道子token")

public class BatchChannelTokenTest extends BaseCase {
    @Test(description = "异常用例没有token，返回100006")
    public void batchChannelTokenTestError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip+batchChannelTokenUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void batchChannelTokenTest() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+batchChannelTokenUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.isContainsExpect("\"channelNo\":0",rspjson.get("data").toString());
    }
}
