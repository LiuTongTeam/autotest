package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("Recharge接口查询冲币记录")

public class RechargeTest extends BaseCase {
    //正常用例
    @DataProvider(parallel=true)
    public Object[][] provideRechargeData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/recharge/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "provideRechargeData",description = "正常输入各种参数，返回成功")
    public void testrecharge(Map<?,?> param) throws IOException {
        JSONObject jsonbody = JSON.parseObject(param.get("body").toString());
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway+rechargeUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
        String totalRows = JsonPath.read(rspjson,"$.data.pagination.totalRows").toString();
        //断言totalRows大于0
        AssertTool.assertEquals( new BigDecimal(totalRows).compareTo(new BigDecimal(0)),1);
        //断言数据list不为空
        AssertTool.assertEquals(JSON.parseArray(JsonPath.read(rspjson,"$.data.list").toString()).size()>0,true);
    }
    //没有token，返回100006
    @Test(description = "RechargeTest异常用例1")
    public void testrechargeError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("timeType", "3");
        jsonbody.put("pageRows", "10");
        jsonbody.put("currPage", "1");
        jsonbody.put("currency", "");
        Response response = OkHttpClientManager.post(ip_gateway + rechargeUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}
