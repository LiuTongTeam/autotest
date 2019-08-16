package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.common.StringUtil;
import io.cex.test.framework.testng.Retry;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenqingyan
 * @create 2019/8/8 11:25
 * @desc 主流程测试类
 **/
@Slf4j
@Feature("主流程")
public class CoreProcessTest extends BaseCase{
    private String token = null;
    private String certificate_no = null;
    private String randomPhone = null;
    @BeforeClass(description = "初始化header数据")
    public void beforeClazz(){
        //初始化header数据
        dataInit();
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "注册", retryAnalyzer = Retry.class)
    public void testRegister() throws IOException {
        randomPhone = RandomUtil.getRandomPhoneNum();
        JSONObject object = new JSONObject();
        object.put("identifier",randomPhone);
        object.put("loginPwd",pwd);
        object.put("mobileArea",area);
        object.put("verifyCode","111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------register response is:"+rspjson.toJSONString());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegister",description = "登陆")
    public void testLogin(){
        token = BaseCase.userCexLogin(randomPhone,pwd,area);
        AssertTool.assertNotEquals(null,token);
        log.info("------------cex token:"+token);
        Allure.addAttachment("登陆token：",token);
    }
    @Story("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testLogin",description = "身份认证")
    public void testIdentity() throws IOException{
        //认证姓名随机字符串
        String userName = RandomUtil.generateString(10);
        JSONObject object = new JSONObject();
        object.put("countryId",countryId);
        //图片ID由上传文件接口返回
        object.put("backId",BaseCase.uploadFile(fileUrl,token,"1.jpeg"));
        object.put("frontId",BaseCase.uploadFile(fileUrl,token,"2.jpg"));
        object.put("userName", userName);
        object.put("certificateNo",RandomUtil.generateLong(18));
        object.put("personId",BaseCase.uploadFile(fileUrl,token,"3.png"));
        object.put("certificateType",certificateType);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        Response response = OkHttpClientManager.post(ip+identityUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------Identity response is:"+rspjson);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        //从数据库中获取身份认证ID
        String sql = String.format("SELECT certificate_no FROM member_certification_record WHERE first_name = '%s';",userName);
        DataBaseManager dataBaseManager = new DataBaseManager();
        certificate_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql,mysql).getString(0)).getString("certificate_no");
        log.info("------certificate_no is:"+certificate_no+"\n");
    }
    @Story("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testIdentity", description = "身份认证初审通过")
    public void testFirstTrial() throws IOException{
        //boss登陆token放入header
        HashMap header = dataInit();
        header.put("Boss-Token",BaseCase.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装初审接口入参
        JSONObject object = new JSONObject();
        object.put("auditStatus","1");
        object.put("auditType","USER_CERT_AUTH");
        object.put("bid",certificate_no);
        //调用初审接口
        Response response = OkHttpClientManager.post(boss_ip+firstTrial, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------Identity first trial response is:"+rspjson);
        Allure.addAttachment("入参：",object.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }
    @Story("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testFirstTrial", description = "身份认证复审通过")
    public void testReviewing() throws IOException{
        HashMap header = dataInit();
        header.put("Boss-Token",BaseCase.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装复审接口入参
        JSONObject object = new JSONObject();
        object.put("auditType","USER_CERT_AUTH");
        object.put("auditStatus","1");
        object.put("bid",certificate_no);
        //调用复审接口
        Response response = OkHttpClientManager.post(boss_ip+reviewing, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",object.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        log.info("-------------Identity reviewing response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testReviewing", description = "设置资金密码")
    public void testSecurityPwd() throws IOException{
        JSONObject object = new JSONObject();
        object.put("securityPwd",securityPwd);
        object.put("verifyCode","111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        Response response = OkHttpClientManager.post(ip+securityPwdUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        log.info("-------------SecurityPwd response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSecurityPwd", description = "充币")
    public void testDeposit1() throws InterruptedException{
        String address = BaseCase.getAddress(randomPhone,pwd,depositCurrency);
        String rspCode = withDraw(presetUsersecurityPwd,address,BaseCase.userCexLogin(presetUser,presetUserPwd,area),depositAmount,depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,depositCurrency);
        log.info("--------Deposit sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",mysql,sql);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit1", description = "充币")
    public void testDeposit2() throws InterruptedException{
        String address = BaseCase.getAddress(randomPhone,pwd,productCoin);
        String rspCode = withDraw(presetUsersecurityPwd,address,BaseCase.userCexLogin(presetUser,presetUserPwd,area),depositAmount,productCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,productCoin);
        log.info("--------Deposit KOFO sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",mysql,sql);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit2", description = "充币")
    public void testDeposit3() throws InterruptedException{
        String address = BaseCase.getAddress(randomPhone,pwd,currencyCoin);
        String rspCode = withDraw(presetUsersecurityPwd,address,BaseCase.userCexLogin(presetUser,presetUserPwd,area),depositAmount,currencyCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,currencyCoin);
        log.info("--------Deposit USDT sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",mysql,sql);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit3", description = "提币数量超过账户剩余数量")
    public void testWithdrawFail(){
        String address = BaseCase.getAddress(presetUser,presetUserPwd,depositCurrency);
        token = BaseCase.userCexLogin(randomPhone,pwd,area);
        String rspCode = withDraw(securityPwd,address,token,"25",depositCurrency);
        AssertTool.isContainsExpect("100113",rspCode);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testWithdrawFail", description = "提币成功")
    public void testWithdraw() throws InterruptedException{
        String address = BaseCase.getAddress(presetUser,presetUserPwd,depositCurrency);
        String rspCode = withDraw(securityPwd,address,token,"15",depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,depositCurrency);
        log.info("--------Deposit USDT sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"5.000000000000000000000000000000\"}",mysql,sql);
    }

    @Story("交易")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testWithdraw",description = "下单")
    public void testOrder(){
        String symbol = String.format("%s/%s",productCoin,currencyCoin);
        //下单前先撤销未成交的市价单
        batchCancelOrder(symbol);
        Map result = order(symbol,"BUY","LMT","5","2","2",token);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
    }





    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testOrder", description = "归还剩余depositCurrency币种")
    public void testWithdrawReturnDepositCurrency() throws InterruptedException{
        String address = BaseCase.getAddress(presetUser,presetUserPwd,depositCurrency);
        String amount = BaseCase.queryAsset(token,depositCurrency).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        Thread.sleep(60000);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testOrder", description = "归还剩余productCoin币种")
    public void testWithdrawReturnProductCoin() throws InterruptedException{
        String address = BaseCase.getAddress(presetUser,presetUserPwd,productCoin);
        String amount = BaseCase.queryAsset(token,productCoin).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),productCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        Thread.sleep(60000);
    }
    @Story("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testOrder", description = "归还剩余currencyCoin币种")
    public void testWithdrawReturnCurrencyCoin() throws InterruptedException{
        String address = BaseCase.getAddress(presetUser,presetUserPwd,currencyCoin);
        String amount = BaseCase.queryAsset(token,currencyCoin).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),currencyCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        Thread.sleep(60000);
    }
}
