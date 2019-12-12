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

@Feature("userInfo接口测试")

public class UserInfoTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "userInfo正常用例")
    public void userInfo() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway + userInfoUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        AssertTool.isContainsExpect("\"userNo\":\"1629161555866214401\",",rspjson.getString("data"));

    }
    @Test(description = "异常用例：没有输入token")
    public void userInfoError1() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip_gateway + userInfoUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        System.out.println(jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        System.out.println(rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }


}
