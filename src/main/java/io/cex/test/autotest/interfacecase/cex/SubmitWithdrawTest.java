package io.cex.test.autotest.interfacecase.cex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption;
import io.cex.test.autotest.interfacecase.boss.tool.BossConfig;
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
import static io.cex.test.autotest.interfacecase.boss.tool.BossConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.setSecurityPwd;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;



@Feature("SubmitWithdraw提交提币请求接口")
@Slf4j

public class SubmitWithdrawTest extends BaseCase{
    //注册新账户用于提币
    private String usertoken = null;
    private String randomPhoneUser = null;
    //BOSS提币单号
    private String withdraw_seq_no = null;
    //新账户的币种地址
    private String address = null;
    //新账户的EOS标签
    private String label = null;
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
    public void testIdentity()throws InterruptedException{
        //提交认证
        String certifercationUser = CexCommonOption.cexIdentity(usertoken);
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


    //测试UPT外部提币，先从专用账户提到新账户，再从新账户提到外部
    //从专用账户提币13UPT，扣除手续费3（做市商组），新账户到账10UPT
   @Severity(SeverityLevel.CRITICAL)
    @Test( dependsOnMethods = "testSetSecurityPwd",description = "SubmitWithdraw从专用账户提UPT到新账户")
    public void testSubmitWithdrawupt() throws IOException,InterruptedException {
        String address = CexCommonOption.getAddress(randomPhoneUser, pwd,"UPT");
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        JSONObject object = new JSONObject();
        object.put("amount", "13");
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
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
    @Test(dependsOnMethods = "testSubmitWithdrawupt", description = "SubmitWithdraw新账户往外部提币")
    public void testSubmitWithdraw1() throws IOException, InterruptedException {
        JSONObject object = new JSONObject();
        object.put("amount", "10");
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
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
        String sql = String.format("SELECT freeze_amount,amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = 'UPT';\n",randomPhoneUser);
        AssertTool.isContainsExpect("{\"freeze_amount\":\"10.000000000000000000000000000000\"}",cexmysql,sql);
        Thread.sleep(700000);
        //发起外部提币成功并等待一段时间，查询冻结金额和可用金额是否都为零
        AssertTool.isContainsExpectJsonNode("{\"freeze_amount\":\"0.000000000000000000000000000000\"}",cexmysql,sql);
        AssertTool.isContainsExpectJsonNode("{\"amount\":\"0.000000000000000000000000000000\"}",cexmysql,sql);
    }

    //测试boss审核提币（做市商审核UPT阈值20），从专用账户往新账户提，初审然后复审通过
    @Severity(SeverityLevel.CRITICAL)
    @Test( dependsOnMethods = "testSubmitWithdraw1",description = "SubmitWithdraw从专用账户提UPT到新账户走boss审核")
    public void testSubmitWithdraw2() throws IOException,InterruptedException {
        String address = CexCommonOption.getAddress(randomPhoneUser, pwd,"UPT");
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        JSONObject object = new JSONObject();
        object.put("amount", "23");
        object.put("currency", "UPT");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
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
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSubmitWithdraw2", description = "提币初审通过")
    public void testauthfirstTrial() throws IOException{
        //从数据库中获取提币单号
        String sql = String.format("SELECT withdraw_seq_no FROM asset_withdraw_coin_bill WHERE user_no = (SELECT user_no from member_user WHERE email = '%s') AND currency_code = 'UPT' AND status = '0';",presetUser);
        DataBaseManager dataBaseManager = new DataBaseManager();
        withdraw_seq_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql,cexmysql).getString(0)).getString("withdraw_seq_no");
        log.info("------withdraw_no is:"+withdraw_seq_no+"\n");
        //boss登陆token放入header
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装初审接口入参
        JSONObject object = new JSONObject();
        object.put("auditStatus","1");
        object.put("auditType","WITHDRAW_COIN");
        object.put("bid",withdraw_seq_no);
        //调用初审接口
        Response response = OkHttpClientManager.post(boss_ip+ BossConfig.authfirstTrial, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        log.info("-------------Identity first trial response is:"+rspjson);
        Allure.addAttachment("入参：",object.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testauthfirstTrial", description = "提币复审通过")
    public void testauthreviewing() throws IOException,InterruptedException{
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装复审接口入参
        JSONObject object = new JSONObject();
        object.put("auditType","WITHDRAW_COIN");
        object.put("auditStatus","1");
        object.put("bid",withdraw_seq_no);
        //调用复审接口
        Response response = OkHttpClientManager.post(boss_ip+BossConfig.authreviewing, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",object.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        log.info("-------------Identity reviewing response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
       Thread.sleep(30000);
        String sql = String.format("SELECT freeze_amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE email = '%s') and currency = 'UPT';\n",presetUser);
        AssertTool.isContainsExpect("{\"freeze_amount\":\"0.000000000000000000000000000000\"}",cexmysql,sql);
    }


    //测试NEO内部提币，从专用账户提到新账户，再从新账户还给专用账户
    @Severity(SeverityLevel.CRITICAL)
    @Test( dependsOnMethods = "testauthreviewing",description = "SubmitWithdraw从专用账户提NEO到新账户")
    public void testSubmitWithdraw3() throws IOException,InterruptedException {
        String address = CexCommonOption.getAddress(randomPhoneUser, pwd,"NEO");
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        JSONObject object = new JSONObject();
        object.put("amount", "1");
        object.put("currency", "NEO");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
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
    @Test( dependsOnMethods = "testSubmitWithdraw3",description = "SubmitWithdraw从新账户将NEO还给专用账户")
    public void testSubmitWithdrawneo() throws IOException,InterruptedException {
        JSONObject object = new JSONObject();
        object.put("amount", "1");
        object.put("currency", "NEO");
        object.put("isLabelCoin", false);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
        object.put("walletAddress", "AWgnvpuC9Rc9Su635r94bhZu5ty8dbqdbA");
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

    //测试EOS内部提币，从专用账户提到新账户，再从新账户还给专用账户
    @Severity(SeverityLevel.CRITICAL)
    @Test( dependsOnMethods = "testSubmitWithdrawneo",description = "SubmitWithdraw从专用账户提EOS到新账户")
    public void testSubmitWithdraw4() throws IOException,InterruptedException {
        String address = CexCommonOption.getAddress(randomPhoneUser, pwd,"EOS");
        String label = CexCommonOption.getLabel(randomPhoneUser, pwd,"EOS");
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        JSONObject object = new JSONObject();
        object.put("amount", "0.1");
        object.put("currency", "EOS");
        object.put("isLabelCoin", true);
        object.put("labelContent", label);
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
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
    @Test( dependsOnMethods = "testSubmitWithdraw4",description = "SubmitWithdraw从新账户将EOS还给专用账户")
    public void testSubmitWithdraweos() throws IOException,InterruptedException {
        JSONObject object = new JSONObject();
        object.put("amount", "0.1");
        object.put("currency", "EOS");
        object.put("isLabelCoin", true);
        object.put("labelContent", "100085097");
        object.put("securityPwd", "f3d3d3667220886d7a1a3f1eb9335d91");
        object.put("verifyCode1", "912121");
        object.put("walletAddress", "yjt123454321");
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
        object.put("verifyCode1", "912121");
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
