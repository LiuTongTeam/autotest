package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class dealList extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] providedealListErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/dealList/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "providedealListErrorData")
    public void testdealListError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip+dealListUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    /**
     * 检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] providedealListData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/dealList/dl1.json";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }

    @Test(dataProvider = "providedealListData")
    public void testdealList(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip+dealListUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
}
