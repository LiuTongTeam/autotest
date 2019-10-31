package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.registerUrl;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.loginPwdWithoutLoginUrl;
@Feature("LoginPwdWithoutLogin重置登录密码接口")

public class LoginPwdWithoutLoginTest extends BaseCase {
    //用随机手机号注册,然后修改手机号的登录密码
    //随机手机号，用于注册
    private String randomPhone = null;
    //用随机邮箱注册,然后修改邮箱的登录密码
    //随机邮箱，用于注册
    private  String randonMail = null;


    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "注册", retryAnalyzer = Retry.class)
    public void testRegister() throws IOException {
        randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = new JSONObject();
        object.put("identifier", randomPhone);
        object.put("loginPwd", pwd);
        object.put("mobileArea", area);
        object.put("verifyCode", "111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip + registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        /*try {
            userNo = JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("userNo").toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        //从数据库根据用户号取到手机号
        String sql = String.format("select mobile_num from member_user where user_no='%s';",userNo);
        DataBaseManager dataBaseManager = new DataBaseManager();
        identifier = JSON.parseObject(dataBaseManager.executeSingleQuery(sql, cexmysql).getString(0)).getString("mobile_num");
        */
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegister", description = "手机重置登陆密码，返回000000")
    public void loginPwdWithoutLoginTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("mobileArea", "86");
        object.put("identifier", randomPhone);
        object.put("newPwd", "afdd0b4ad2ec172c586e2150770fb54g");
        object.put("verifyCode", "123456");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + loginPwdWithoutLoginUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "注册", retryAnalyzer = Retry.class)
    public void testRegisterMail() throws IOException {
        randonMail = RandomUtil.getRandomMail();
        JSONObject object = new JSONObject();
        object.put("identifier", randonMail);
        object.put("loginPwd", pwd);
        object.put("mobileArea", "");
        object.put("verifyCode", "111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip + registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegisterMail", description = "邮箱重置登陆密码，返回000000")
    public void loginPwdWithoutLoginMailTest() throws IOException {
        JSONObject object = new JSONObject();
        object.put("mobileArea", "");
        object.put("identifier", randonMail);
        object.put("newPwd", "afdd0b4ad2ec172c586e2150770fb54g");
        object.put("verifyCode", "123456");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + loginPwdWithoutLoginUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    //用没有注册的手机号，去修改登录密码，报错返回
    @Test(description = "没有注册过的手机号去修改登录密码，报错返回")
    public void loginPwdWithoutLoginTestError1() throws IOException {
        JSONObject object = new JSONObject();
        object.put("mobileArea", "86");
        object.put("identifier", "11111111111");
        object.put("newPwd", "afdd0b4ad2ec172c586e2150770fb54g");
        object.put("verifyCode", "123456");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + loginPwdWithoutLoginUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("user-100008",rspjson.get("code").toString());
    }
    //用没有注册的邮箱号，去修改登录密码，报错返回
    @Test(description = "没有注册过的邮箱号去修改登录密码，报错返回")
    public void loginPwdWithoutLoginTestError2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("mobileArea", "");
        object.put("identifier", "11111111111@11.com");
        object.put("newPwd", "afdd0b4ad2ec172c586e2150770fb54g");
        object.put("verifyCode", "123456");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip + loginPwdWithoutLoginUrl, jsonbody.toJSONString(),
                "application/json", dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("user-100008",rspjson.get("code").toString());
    }
}
