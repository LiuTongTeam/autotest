package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.dbutil.DataBaseManager;
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

@Feature("DealDetail接口")

public class DealDetailTest extends BaseCase {
    private String orderNo = null;

    //正常用例，输入专用账户的订单号查成交，返回成功
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "DealDetail正常用例")
    public void dealDetail() throws IOException {
        //从数据库中随机获取一笔专用账户的订单编号
        String sql = String.format("select order_no from order_info where user_no= (select user_no from member_user where email='%s') LIMIT 1;", presetUser);
        DataBaseManager dataBaseManager = new DataBaseManager();
        orderNo = JSON.parseObject(dataBaseManager.executeSingleQuery(sql, cexmysql).getString(0)).getString("order_no");
        JSONObject object = new JSONObject();
        object.put("orderNo", orderNo);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data", object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + dealDetailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }

    //订单号为空，返回100006
    @Test(description = "DealDetail异常用例1")
    public void dealDetailError1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("orderNo", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data", object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + dealDetailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    //没有token，返回100006
    @Test(description = "DealDetail异常用例2")
    public void dealDetailError2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("orderNo", "123");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        jsonbody.put("data", object);
        Response response = OkHttpClientManager.post(ip + dealDetailUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
}

