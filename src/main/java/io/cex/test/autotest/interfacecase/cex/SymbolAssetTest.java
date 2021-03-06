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

@Feature("SymbolAsset接口")
public class SymbolAssetTest extends BaseCase {
    //正常用例
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "SymbolAssetTest正常用例")
    public void symbolAsset() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol","KOFO/USDT");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + querySymbolAsset, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        AssertTool.assertEquals(JSON.parseArray(rspjson.get("data").toString()).size()>0,true);
    }
    //异常用例1：没有输入正常的token
    @Test(description = "SymbolAssetTest异常用例：没有输入token")
    public void symbolAssetError1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol","KOFO/USDT");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + querySymbolAsset, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    //异常用例2：交易对送空
    @Test(description = "SymbolAssetTest异常用例：交易对送空")
    public void symbolAssetError2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol","");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + querySymbolAsset, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

}
