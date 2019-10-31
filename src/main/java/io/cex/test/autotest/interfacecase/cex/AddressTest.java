package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("AddressTest地址是否合法，内部地址校验")
public class AddressTest extends BaseCase {

    private String address = null;
    @BeforeClass
    public void getAddress(){
        address = CexCommonOption.getAddress(presetUser,presetUserPwd,depositCurrency);
    }

    //异常测试案例1 没有token
    @Test(description = "异常用例没有token，返回100006")
    public void addressTestError1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        object.put("currencyAddress", address);
        object.put("isLabelCoin", "0");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip+addressUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    //异常测试案例2 币种为空
    @Test(description = "币种为空，返回100006")
    public void addressTestError2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", "");
        object.put("currencyAddress", address);
        object.put("isLabelCoin", "0");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+addressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    //异常测试案例3 地址为空
    @Test(description = "地址为空，返回100006")
    public void addressTestError3() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        object.put("currencyAddress", "");
        object.put("isLabelCoin", "0");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+addressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    //正常案例，正常输入，返回000000并且返回的data中isValid=true
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "正常案例，正常输入，返回000000并且返回的data中isValid=true")
    public void addressTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        object.put("currencyAddress", address);
        object.put("isLabelCoin", "0");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+addressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        JSONObject a = JSON.parseObject(JSON.toJSONString(rspjson.get("data")));
        AssertTool.isContainsExpect("true",a.get("isValid").toString());
    }
    //地址和币种不符合，返回000000并且返回的data中isValid=false
      @Severity(SeverityLevel.CRITICAL)
      @Test(description = "地址和币种不符合，返回000000并且返回的data中isValid=false")
      public void addressTestError4() throws IOException {
          JSONObject object = new JSONObject();
          object.put("currency", currencyCoin);
          object.put("currencyAddress", address);
          object.put("isLabelCoin", "0");
          object.put("labelContent", "");
          JSONObject jsonbody = new JSONObject();
          jsonbody.put("data", object);
          jsonbody.put("lang", lang);
          HashMap header = dataInit();
          header.put("CEXTOKEN", presetToken);
          Response response = OkHttpClientManager.post(ip+addressUrl, jsonbody.toJSONString(),
                  "application/json", header);
          JSONObject rspjson = resultDeal(response);
          Allure.addAttachment("入参：",jsonbody.toJSONString());
          Allure.addAttachment("出参：",rspjson.toJSONString());
          AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
          //JSONObject a = JSON.parseObject(JSON.toJSONString(rspjson.get("data")));
          String isValid = JsonPath.read(rspjson,"$.data.isValid").toString();
          AssertTool.isContainsExpect("false",isValid);
      }
}
