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

@Feature("dealOrderList历史成交订单")

public class dealOrderListTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] providedealOrderListErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/dealOrderList/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "providedealOrderListErrorData",description = "dealList异常用例")
    public void dealOrderListError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        object.put("gmtStart", System.currentTimeMillis()- + 30 * 60 * 1000);
        object.put("gmtEnd",System.currentTimeMillis());
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", orderToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+dealOrderListUrl, jsonbody.toJSONString(),
                "application/json",header );
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] providedealOrderListData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/dealList/dl1.json";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "providedealOrderListData",description = "检查返回是否成功")
    public void dealOrderList(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        object.put("gmtStart", System.currentTimeMillis()- + 30 * 60 * 1000);
        object.put("gmtEnd",System.currentTimeMillis());
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", orderToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+dealOrderListUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
}
