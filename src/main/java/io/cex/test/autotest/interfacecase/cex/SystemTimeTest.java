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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;


@Feature("SystemTime获取系统时间")
public class SystemTimeTest extends BaseCase {
    //正常获取系统时间，返回000000
    @Severity(SeverityLevel.CRITICAL)
    @Test( description = "SystemTime正常用例")
    public void testSystemTime() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip + systemTimeUrl , jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    //不传lang，返回100006
    @Severity(SeverityLevel.CRITICAL)
    @Test( description = "SystemTime异常用例")
    public void testSystemTimeError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang"," ");
        Response response = OkHttpClientManager.post(ip + systemTimeUrl , jsonbody.toJSONString(),
                "application/json");
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}