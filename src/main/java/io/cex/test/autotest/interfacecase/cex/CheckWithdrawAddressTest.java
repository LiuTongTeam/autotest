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


@Feature("CheckWithdrawAddress接口")

public class CheckWithdrawAddressTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideCheckWithdrawAddressErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/CheckWithdrawAddress/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideCheckWithdrawAddressErrorData",description = "CheckWithdrawAddress异常用例")
    public void testCheckWithdrawAddressError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+checkwithdrawaddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }

    //检查提币地址不传token，返回100006
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "不输token，返回参数异常")
    public void testCheckWithdrawAddressList() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency","IDA");
        object.put( "withdrawAddr","0xd9a99030385cbd069f7afd9d9eda8c0c87950a57");
        object.put( "label", " ");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        Response response = OkHttpClientManager.post(ip+checkwithdrawaddressUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] provideCheckWithdrawAddressData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/CheckWithdrawAddress/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    //查询提币地址是内部地址还是外部地址
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "provideCheckWithdrawAddressData",description = "检查返回是否成功")
    public void testCheckWithdrawAddressData(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN",presetToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+checkwithdrawaddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
}

