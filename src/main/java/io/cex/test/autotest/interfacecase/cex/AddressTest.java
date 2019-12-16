package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currencyAliasName", depositCurrency);
        jsonbody.put("chain", depositCurrencyChain);
        jsonbody.put("currency", depositCurrency);
        jsonbody.put("currencyAddress", address);
        jsonbody.put("isLabelCoin", "0");
        jsonbody.put("labelContent", "");
        Response response = OkHttpClientManager.post(ip_gateway+addressUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }

    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideAddressTestErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/ValidateAddress/error/";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    //异常测试案例
    @Test(dataProvider = "provideAddressTestErrorData", description = "异常测试案例")
    public void addressErrorTest(Map<?,?> param) throws IOException {
        JSONObject jsonbody = JSON.parseObject(param.get("body").toString());
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+addressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        System.out.println("chu:"+rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());


    }

    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideAddressTestRightData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/ValidateAddress/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    //正常测试案例
    @Test(dataProvider = "provideAddressTestRightData", description = "正常测试案例")
    public void addressRightTest(Map<?,?> param) throws IOException {
        JSONObject jsonbody = JSON.parseObject(param.get("body").toString());
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+addressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
        AssertTool.isContainsExpect(param.get("assertString").toString(),rspjson.get("data").toString());

    }

}
