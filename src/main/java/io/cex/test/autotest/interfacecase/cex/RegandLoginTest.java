package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.regandloginUrl;

@Feature("regandlogin接口")
@Slf4j
public class RegandLoginTest extends BaseCase {
    //cex登陆token
    private String token = null;
    //随机手机号，用于注册
    private String randomPhone = null;

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "regandlogin的4A邀请注册", retryAnalyzer = Retry.class)
    public void testRegandLogin() throws IOException {
        randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = new JSONObject();
        object.put("phone", randomPhone);
        object.put("email", "");
        object.put("loginPwd", pwd);
        object.put("verifyCode", "111111");
        object.put("type", "1");
        object.put("inviteCode","0ktejS");
        HashMap header = dataInit();
        header.put("CEXPASSPORT", cexpassport);
        Response response = OkHttpClientManager.post(ip_gateway + regandloginUrl, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        log.info("-------------regandlogin response is:" + rspjson.toJSONString());
        Allure.addAttachment("入参：", object.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }


    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideregandloginErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/RegandLogin/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/
    @Test(dataProvider = "provideregandloginErrorData",description = "regandlogin异常用例")
    public void testRegandLoginError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        HashMap header = dataInit();
        header.put("CEXPASSPORT", cexpassport);
        Allure.addAttachment(param.get("comment").toString()+"入参",object.toJSONString());
        Response response = OkHttpClientManager.post(ip_gateway + regandloginUrl, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }
}