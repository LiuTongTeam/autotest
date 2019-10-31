package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.currencyCoin;
@Feature("交易下单的异常校验测试")

public class OrderErrorTest extends BaseCase {
    String symbol = String.format("%s/%s",productCoin,currencyCoin);
    //订单异常下单情况控制
    //没有token,返回100006
    @Test(description = "没有token，下单,报错返回100006")
    public void orderErrorTest1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "2");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    //订单类型非法，不是限价也不是市价
    @Test(description = "订单类型非法，不是限价也不是市价,返回100006")
    public void orderErrorTest2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "11111");
        object.put("action", "BUY");
        object.put("limitPrice", "2");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    //订单买卖方向为空或者乱填
    @Test(description = "订单买卖方向为空或者乱填")
    public void orderErrorTest3() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "22222");
        object.put("limitPrice", "2");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    //订单价格为空:后端返回系统错误，后面再优化，先注释
    /*
    @Test(description = "订单价格为空")
    public void orderErrorTest4() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }*/
    //订单价格为0:

    @Test(description = "订单价格为空")
    public void orderErrorTest5() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "0");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100048", rspjson.get("code").toString());
    }
    /*
    @Test(description = "订单价格为负数，报空指针后面再优化")
    public void orderErrorTest6() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "-1");
        object.put("amount", "10");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100048", rspjson.get("code").toString());
    }*/
    /*
    @Test(description = "订单价格精度/数量精度/金额精度后端都没有控制超过配置的4位--需要后端修改")
    public void orderErrorTest7() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "0.5000001");
        object.put("amount", "10.00001");
        object.put("quantity", "20");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100048", rspjson.get("code").toString());
    }*/
    @Test(description = "最小挂单数量设置成1，检查是否校验到")
    public void orderErrorTest8() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "1");
        object.put("amount", "0.9");
        object.put("quantity", "0.9");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100049", rspjson.get("code").toString());
    }
    @Test(description = "最大挂单数量设置成1000，检查是否校验到")
    public void orderErrorTest9() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "1");
        object.put("amount", "10001");
        object.put("quantity", "10001");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100050", rspjson.get("code").toString());
    }
    /*
    @Test(description = "最小挂单金额设置成0.5，最大金额都没有控制，后端都没有控制--需要改造")
    public void orderErrorTest10() throws IOException {
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("orderType", "LMT");
        object.put("action", "BUY");
        object.put("limitPrice", "0.4");
        object.put("amount", "0.4");
        object.put("quantity", "1");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100050", rspjson.get("code").toString());
    }*/

}
