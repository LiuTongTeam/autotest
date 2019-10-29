package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;



@Feature("QuotationHistoryTest获取行情历史数据")
public class QuotationHistoryTest extends BaseCase {
    //正常获取行情历史数据，返回000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "QuotationHistory正常用例")
    public void testQuotationHistory() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol", "IDA/USDT");
        object.put("startTime", "1562554734808");
        object.put("endTime", "1562590734808");
        object.put("range", "300000");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", "zh-CN");
        System.out.println("rucan"+jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip + quotationHistoryUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        System.out.println("chucan" +rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    /**
     * @param
     * @desc 异常用例的数据驱动
     **/
    @DataProvider(parallel = true)
    public Object[][] QuotationHistoryErrorData(Method method) {
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/QuotationHistory/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }

    /**
     * @param
     * @desc 异常用例
     **/
    @Test(dataProvider = "QuotationHistoryErrorData", description = "QuotationHistory异常用例")
    public void testQuotationHistoryError(Map<?, ?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data", object);
        Allure.addAttachment(param.get("comment").toString() + "入参", jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip + quotationHistoryUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(), rspjson.get("code").toString());
    }
}