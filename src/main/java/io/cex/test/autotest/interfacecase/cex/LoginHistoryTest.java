package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
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
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.loginHistoryUrl;
@Feature("LoginHistory登录历史查询接口")

public class LoginHistoryTest extends BaseCase {
    @Test(description = "异常用例没有token，返回100006")
    public void loginHistoryTestError() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currPage", "1");
        object.put("pageRows", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip_gateway+loginHistoryUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，返回000000")
    public void loginHistoryTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currPage", "1");
        object.put("pageRows", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+loginHistoryUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        String totalRows = JsonPath.read(rspjson,"$.data.pagination.totalRows").toString();
        //断言totalRows即总条数大于0
        AssertTool.assertEquals(Integer.parseInt(totalRows)>0,true);
        //断言当前页面list不为空
        AssertTool.assertEquals(JSON.parseArray(JsonPath.read(rspjson,"$.data.list").toString()).size()>0,true);
    }


}
