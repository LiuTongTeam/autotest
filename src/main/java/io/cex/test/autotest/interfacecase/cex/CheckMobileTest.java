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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.CheckMobileUrl;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;

@Feature("CheckMobile接口")
public class CheckMobileTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideCheckMobileErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/CheckMobile/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideCheckMobileErrorData",description = "CheckMobile异常用例")
    public void testCheckMobileError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway+CheckMobileUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    /**
     * 给一个系统没有注册的账号，检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] provideCheckMobileData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/CheckMobile/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "provideCheckMobileData",description = "给一个系统没有注册的账号，检查返回是否成功")
    public void testCheckMobile(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway+CheckMobileUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
}
