package io.cex.test.autotest.interfacecase.cex;
import bsh.InterpreterError;
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
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption.cexReviewing;
import static io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption.firstTrial;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.setSecurityPwd;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;



@Feature("SubmitWithdraw提交提币请求接口")
@Slf4j

public class SubmitWithdrawTest extends BaseCase{
    //注册新账户用于提币
    private String usertoken = null;
    private String randomPhoneUser = null;
    //新账户的UPT地址
    private String address = null;
    public static final String Coin = "UPT";
    public BigDecimal amount = null;
    public BigDecimal amountnew = null;
    BigDecimal num = new BigDecimal("1");

    //SubmitWithdraw判断外部提币，从专用账户提到新注册账户，再从新账户往外部提币
    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(retryAnalyzer = Retry.class,description = "账户注册并登陆")
    public void testRegistAndLogin(){
        //生成一个随机手机号
        randomPhoneUser = RandomUtil.getRandomPhoneNum();
        //注册
        CexCommonOption.register(randomPhoneUser, pwd, area,"000000");
        //登陆
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        AssertTool.assertNotEquals(usertoken,null);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegistAndLogin",description = "身份认证")
    public void testIdentity(){
        //提交认证
        String certifercationUser = cexIdentity(usertoken);
        //初审
        firstTrial(certifercationUser);
        //复审
        cexReviewing(certifercationUser);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testIdentity",description = "设置账户的资金密码")
    public void testSetSecurityPwd(){
        setSecurityPwd(usertoken);
    }


    @Severity(SeverityLevel.CRITICAL)
    @Test( dependsOnMethods = "testSetSecurityPwd",description = "SubmitWithdraw从专用账户提UPT到新账户")
    public void testSubmitWithdraw1() throws IOException,InterruptedException {
        String address = CexCommonOption.getAddress(randomPhoneUser, pwd,"UPT");
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        JSONObject object = new JSONObject();
        object.put("amount", "13");
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "123456 ");
        object.put("walletAddress", address);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", "zh-CN");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        System.out.println("rucan" + jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip + submitwithdrawUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        System.out.println("chucan" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        Thread.sleep(30000);

    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSubmitWithdraw1", description = "SubmitWithdraw新账户往外部提币")
    public void testSubmitWithdraw() throws IOException, InterruptedException {
        JSONObject object = new JSONObject();
        object.put("amount", "10");
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "123456 ");
        object.put("walletAddress", "0x274cC789096337D12eB5A17159845CBf3ebB8610");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", "zh-CN");
        HashMap header = dataInit();
        header.put("CEXTOKEN", usertoken);
        System.out.println("rucan" + jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip + submitwithdrawUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        System.out.println("chucan" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        Thread.sleep(2000);
        String sql = String.format("SELECT freeze_amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = 'UPT';\n",randomPhoneUser);
        AssertTool.isContainsExpect("{\"freeze_amount\":\"10.000000000000000000000000000000\"}",cexmysql,sql);
        Thread.sleep(300000);
        String sql1 = String.format("SELECT freeze_amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = 'UPT';\n",randomPhoneUser);
        AssertTool.isContainsExpect("{\"freeze_amount\":\"0.000000000000000000000000000000\"}",cexmysql,sql1);
    }

    /**
     * @desc 异常用例的数据驱动
     * @param
     **/

    @DataProvider(parallel=true)
    public Object[][] SubmitWithdrawErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/SubmitWithdraw/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
     * @desc 异常用例
     * @param
     **/

    @Test(dataProvider = "SubmitWithdrawErrorData",description = "SubmitWithdraw异常用例")
    public void testDepthStepError(Map<?,?> param) throws IOException {
        JSONObject object = JSON.parseObject(param.get("body").toString());
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip+submitwithdrawUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }


    @Test( description = "SubmitWithdraw提币金额大于可用金额")
    public void testSubmitWithdrawError1() throws IOException {
        amount = new BigDecimal(queryCexAsset(presetToken,Coin).get("availableAmount").toString());
        amountnew = amount.add(num);
        JSONObject object = new JSONObject();
        object.put("amount", amountnew);
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "123456 ");
        object.put("walletAddress", "0x274cC789096337D12eB5A17159845CBf3ebB8610");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", "zh-CN");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        System.out.println("rucan" + jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(ip + submitwithdrawUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        System.out.println("chucan" + rspjson.toJSONString());
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("100113", rspjson.get("code").toString());
    }

}
