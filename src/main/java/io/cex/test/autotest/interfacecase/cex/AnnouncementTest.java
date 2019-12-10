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
@Feature("Announcement获取公告信息接口")

public class AnnouncementTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，有token返回000000")
    public void announcementTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("pageIndex", "1");
        object.put("pageSize", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+announcementUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.isContainsExpect("\"summary\"",rspjson.get("data").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，没有token返回000000")
    public void announcementTest1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("pageIndex", "1");
        object.put("pageSize", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip_gateway+announcementUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.isContainsExpect("\"summary\"",rspjson.get("data").toString());
    }
}
