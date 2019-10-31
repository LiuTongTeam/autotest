package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("orderdetail接口")
@Slf4j

public class OrderDetailTest extends BaseCase{
    private String orderNo = null;

    @Severity(SeverityLevel.CRITICAL)
    @Test( description = "OrderDetail正常用例")
    public void testorderDetail() throws IOException {
        //从数据库中获取一笔委托订单号，state != 3表示未全部成交
        String sql = String.format("SELECT order_no FROM order_info WHERE user_no = (select user_no from member_user where email='%s') and state != 3 LIMIT 1;",presetUser);
        DataBaseManager dataBaseManager = new DataBaseManager();
        orderNo = JSON.parseObject(dataBaseManager.executeSingleQuery(sql, cexmysql).getString(0)).getString("order_no");
        List data = new ArrayList();
        data.add(0,orderNo);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",data);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip + orderDetailUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }


    //不传token，返回100006
    @Test(description = "orderDetail异常用例2")
    public void orderDetailError2() throws IOException {
        //从数据库中获取一笔委托订单号，state != 3表示未全部成交
        String sql = String.format("SELECT order_no FROM order_info WHERE user_no = (select user_no from member_user where email='%s') and state != 3 LIMIT 1;",presetUser);
        DataBaseManager dataBaseManager = new DataBaseManager();
        orderNo = JSON.parseObject(dataBaseManager.executeSingleQuery(sql, cexmysql).getString(0)).getString("order_no");
        List data = new ArrayList();
        data.add(0,orderNo);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",data);
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip + orderDetailUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

}
