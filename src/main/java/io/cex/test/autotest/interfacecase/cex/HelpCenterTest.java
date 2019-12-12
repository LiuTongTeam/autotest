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
@Feature("HelpCenter获取帮助中心接口")


public class HelpCenterTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，有token返回000000")
    public void helpCenterTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("pageIndex", "1");
        object.put("pageSize", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+helpCenterUrl, jsonbody.toJSONString(),
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
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例，没有token返回000000")
    public void helpCenterTest1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("pageIndex", "1");
        object.put("pageSize", "10");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip_gateway+helpCenterUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
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
