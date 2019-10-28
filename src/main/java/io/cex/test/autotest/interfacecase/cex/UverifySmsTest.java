package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
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

@Feature("UerifySmsTest登录后发送手机验证码")
public class UverifySmsTest extends BaseCase {
    private String token = CexCommonOption.userCexLogin("18980198729",pwd,"86");

    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideUVerifySmsTestErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/UverifySms/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideUVerifySmsTestErrorData",description = "VerifySmsTest异常用例")
    public void uverifySmsTestError(Map<?,?> param) throws IOException,InterruptedException{
        Thread.sleep( Long.parseLong(param.get("time").toString()));
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", this.token);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+uverifySmsUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }

    @Test(description = "没有token的异常用例")
    public void uverifySmsTestError1() throws IOException,InterruptedException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        object.put("authType", "WITHDRAW_COIN");
        object.put("mobileArea", "86");
        object.put("mobile", "18980198729");
        jsonbody.put("data",object);
        Thread.sleep(61000);
        Response response = OkHttpClientManager.post(ip + uverifySmsUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }

    @Test(description = "类型传登录LOGIN，返回100006")
    public void uverifySmsTestError2() throws IOException,InterruptedException {
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        Thread.sleep(61000);
        jsonbody.put("lang",lang);
        object.put("authType", "LOGIN");
        object.put("mobileArea", "86");
        object.put("mobile", "18980198729");
        jsonbody.put("data",object);
        HashMap header = dataInit();
        //String token = CexCommonOption.userCexLogin("18780050294",pwd,area);
        header.put("CEXTOKEN", this.token);
        Response response = OkHttpClientManager.post(ip + uverifySmsUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "检查返回是否成功")
    public void uverifySmsTest() throws IOException,InterruptedException {
        Thread.sleep(61000);
        JSONObject object = new JSONObject();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        object.put("authType", "WITHDRAW_COIN");
        object.put("mobileArea", "86");
        object.put("mobile", "18980198729");
        jsonbody.put("data",object);
        HashMap header = dataInit();
        //String token = CexCommonOption.userCexLogin("18780050294",pwd,area);
        header.put("CEXTOKEN", this.token);
        Response response = OkHttpClientManager.post(ip + uverifySmsUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        //System.out.println("-------------liuxm"+rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());



    }

}
