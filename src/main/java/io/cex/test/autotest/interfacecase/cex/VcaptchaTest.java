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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.vcaptchaUrl;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;
@Feature("Vcaptcha验证码二次验证接口")

public class VcaptchaTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void vcaptchaTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("geetestChallenge", "e3fe5751099b8b3dc1a39a7fbfb191d9fv");
        object.put("geetestValidate", "9812801d64b60f98f7652dc0ae2e7d6e");
        object.put("geetestSeccode", "9812801d64b60f98f7652dc0ae2e7d6e");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip+vcaptchaUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100087",rspjson.get("code").toString());
    }
}
