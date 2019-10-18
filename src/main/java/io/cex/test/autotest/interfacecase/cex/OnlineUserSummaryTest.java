package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

public class OnlineUserSummaryTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test( description = "OnlineUserSummary正常用例")
    public void testonlineUserSummary() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + onlineUserSummaryUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }
}
