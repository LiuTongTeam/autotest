package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@Feature("注册接口")
public class RegisterTest extends BaseCase{
    /**
    * @desc 前置条件：16602829192已注册
    * @param
    **/
    @BeforeClass
    public void beforClass(){
        try {
            register("16602829192","123qweQWE","86","111111");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideRegisterErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/register/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
    * @desc 异常用例1
    * @param
    **/
    @Test(dataProvider = "provideRegisterErrorData",description = "Register异常用例")
    public void testRegisterError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("msg").toString());
    }

    /**
     * @desc 数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideRegisterData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/register/register.json";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
    * @desc 注册成功
    * @param
    **/
    @Test(dataProvider = "provideRegisterData",description = "注册成功", retryAnalyzer = Retry.class)
    public void testRegister(Map<?,?> param) throws IOException {
        String randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        object.put("identifier",randomPhone);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        System.out.printf("json:"+jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("msg").toString());
    }
}
