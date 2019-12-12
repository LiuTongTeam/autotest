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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.depthDataUrl;
@Feature("DepthData查询深度图")

public class DepthDataTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideDepthDataErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/DepthData/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideDepthDataErrorData",description = "DepthDataTest异常用例")
    public void testDepthDataError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+depthDataUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] providedepthDataTestData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/DepthData/dd.json";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "providedepthDataTestData",description = "检查返回是否成功")
    public void testdepthDataTest(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+depthDataUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
        String xMaxPrice = JsonPath.read(rspjson,"$.data.xMaxPrice").toString();
        //断言xMaxPrice大于0
        AssertTool.assertEquals( new BigDecimal(xMaxPrice).compareTo(new BigDecimal(0)),1);
        //断言深度数据list不为空
        AssertTool.assertEquals(JSON.parseArray(JsonPath.read(rspjson,"$.data.asks").toString()).size()>0,true);

    }

}
