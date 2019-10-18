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
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Slf4j
@Feature("ChecksecurityPwd接口")

public class CheckpwdTest extends BaseCase {

    //检查资金密码正确，返回000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "检查资金密码正确")
    public void checkpwdtest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",presetToken);
        Response response = OkHttpClientManager.post(ip + checkpwdTestUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------checkpwdtest response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    //检查资金密码错误，返回100108
    @Test(description = "检查资金密码错误")
    public void checkpwderror1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("securityPwd", "qwertyui");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",presetToken);
        Response response = OkHttpClientManager.post(ip + checkpwdTestUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------checkpwderror1 response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100108", rspjson.get("code").toString());
    }

    //检查资金密码为空，返回100006
    @Test(description = "检查资金密码为空")
    public void checkpwderror2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("securityPwd", " ");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",presetToken);
        Response response = OkHttpClientManager.post(ip + checkpwdTestUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------checkpwderror2 response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

    //检查资金密码不传token，返回100006
    @Test(description = "检查资金密码正确，不传token")
    public void checkpwderror3() throws IOException {
        JSONObject object = new JSONObject();
        object.put("securityPwd", " ");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip + checkpwdTestUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------checkpwderror3 response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}
