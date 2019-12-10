package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;


import static io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption.firstTrial;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.cexIdentity;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.authAuditReasonUrl;
@Feature("AuthAuditReason查询最后异常认证失败接口")

public class AuthAuditReasonTest extends BaseCase {
    String token = null;
    @BeforeClass
    public void dataPrepare() throws InterruptedException{
        //生成随机手机号
        String randomPhone = RandomUtil.getRandomPhoneNum();
        //注册
        CexCommonOption.register(randomPhone,pwd,area,"000000");
        //登录获取token
        token = CexCommonOption.userCexLogin(randomPhone,pwd,area);
        //提交身份认证
        String certifercation = cexIdentity(token);
        //身份认证初审打回
        firstTrial(certifercation,"打回");
    }

    @Test(description = "异常用例没有token，返回100006")
    public void authAuditReasonTestError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip_gateway+authAuditReasonUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void authAuditReasonTest() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", token);
        Response response = OkHttpClientManager.post(ip_gateway+authAuditReasonUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.isContainsExpect("打回",rspjson.get("data").toString());
    }

}
