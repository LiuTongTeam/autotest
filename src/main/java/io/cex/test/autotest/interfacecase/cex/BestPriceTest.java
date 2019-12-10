package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.StringUtil;
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

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.BestPriceUrl;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.lang;

@Feature("BestPrice接口")

public class BestPriceTest extends BaseCase {
    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideBestPriceErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/BestPrice/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideBestPriceErrorData",description = "BestPrice异常用例")
    public void testBestPriceError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+BestPriceUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
    /**
     * 给一个正常的买卖方向和交易对，检查返回是否成功
     */
    @DataProvider(parallel=true)
    public Object[][] provideBestPriceData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/BestPrice/right";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "provideBestPriceData",description = "正常的买卖方向和交易对，检查返回是否成功")
    public void testBestPrice(Map<?,?> param) throws IOException {
        dataInit();
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+BestPriceUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
        BigDecimal price = new BigDecimal(StringUtil.stripTrailingZeros(JSONObject.parseObject(rspjson.get("data").toString()).getString("bestPrice")));
        //断言出参最优价格大于0
        AssertTool.assertEquals(1,price.compareTo(new BigDecimal(0)));
    }
}
