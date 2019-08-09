package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
    * @desc 异常用例
    * @param
    **/
    @Test(dataProvider = "provideRegisterErrorData")
    public void testRegisterError(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
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
    @Test(dataProvider = "provideRegisterData")
    public void testRegister(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        object.put("identifier",randomPhone);
        jsonbody.put("data",object);
        System.out.printf("json:"+jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("msg").toString());
    }
}
