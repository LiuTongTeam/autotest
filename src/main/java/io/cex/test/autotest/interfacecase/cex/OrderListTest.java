package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.DateUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Slf4j
@Feature("orderList接口")

public class OrderListTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideorderListErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/orderList/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideorderListErrorData",description = "orderList异常用例")
    public void testorderListError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+orderListUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "不输token，返回参数异常")
    public void testorderList() throws IOException {
        JSONObject object = new JSONObject();
        object.put("gmtStart","0000000000000");
        object.put("currPage","1");
        object.put("orderStatus","OPEN");
        object.put("orderStatus","999");
        object.put("gmtEnd","1652473552000");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip+orderListUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] provideorderListData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/orderList/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "provideorderListData",description = "检查返回是否成功")
    public void testorderListData(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        object.put("gmtStart", System.currentTimeMillis()- + 30 * 60 * 1000);
        object.put("gmtEnd",System.currentTimeMillis());
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN",presetToken);
        System.out.println("body:"+object.toJSONString());
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+orderListUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
        if(object.get("orderStatus").toString()=="CLOSED")
        {
            String totalRows = JsonPath.read(rspjson,"$.data.pagination.totalRows").toString();
            //AssertTool.isContainsExpect("false",isValid);
            try {
                int actualReal =Integer.parseInt(totalRows);
                if (actualReal>0){
                    log.info("------Assert true：acutal is greater than except");
                }else {
                    fail("----Assert failed：Actual number not greater than expect number");
                }
            }catch (NumberFormatException e){
                fail("----Assert failed：NumberFormatException，please input  String than can transform into number----");
            }
        }
    }
}
