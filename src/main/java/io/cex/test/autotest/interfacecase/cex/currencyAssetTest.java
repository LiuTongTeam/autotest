package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.StringUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
@Feature("currencyAsset根据币种查询余额")

public class currencyAssetTest extends BaseCase {
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常用例")
    public void currencyAsset() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        object.put("currency", "IDA");
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + currencyAssetUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        BigDecimal price = new BigDecimal(StringUtil.stripTrailingZeros(JSONObject.parseObject(rspjson.get("data").toString()).getString("availableAmount")));
        //断言出参余额大于0
        AssertTool.assertEquals(price.compareTo(new BigDecimal(0)),1);
    }
    @Test(description = "异常用例：没有输入token")
    public void currencyAssetError1() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        object.put("currency", "IDA");
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip + currencyAssetUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    @Test(description = "异常用例：币种为空")
    public void currencyAssetError2() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        object.put("currency", "");
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + currencyAssetUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    @Test(description = "异常用例：币种乱传")
    public void currencyAssetError3() throws IOException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        object.put("currency", "12");
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + currencyAssetUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100112", rspjson.get("code").toString());
    }

}
