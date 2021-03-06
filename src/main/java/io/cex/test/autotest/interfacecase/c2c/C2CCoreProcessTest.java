package io.cex.test.autotest.interfacecase.c2c;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.common.StringUtil;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption.cexReviewing;
import static io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption.firstTrial;
import static io.cex.test.autotest.interfacecase.c2c.tool.C2CConfig.*;
import static io.cex.test.autotest.interfacecase.c2c.tool.C2CCommonOption.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.*;

/**
 * @author shenqingyan
 * @create 2019/8/28 14:18
 * @desc c2c主流程测试
 **/
@Epic("AA【C2C----------主流程】")
@Slf4j
public class C2CCoreProcessTest extends BaseCase {
    private String merchanttoken = null;
    private String usertoken = null;
    //随机手机号，用于注册
    private String randomPhoneMerchant = null;
    private String randomPhoneUser = null;
    private String amount = "100";
    private String tradeMerchantBuyId = null;
    private String tradeMerchantSellId = null;
    private String orderId = null;
    private String merchantReturnAmount = null;
    private String userReturnAmount = null;
    private String merchantBankAccount = RandomUtil.generateLong(10).toString();
    private String userBankAccount = RandomUtil.generateLong(10).toString();
    private String userImageUrl = null;
    private String merchantImageUrl = null;
    private String userWechatPayId = null;
    private String merchantWechatPayId = null;
    private String userBankCardPayId = null;
    private String merchantBankCarPayId = null;

    @BeforeClass(description = "取消已有自动化测试产生的广告")
    public void testDataClean(){
        tradeCancelAll();
    }


    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(retryAnalyzer = Retry.class,description = "两个账户注册并登陆")
    public void testRegistAndLogin(){
        //生成两个随机手机号
        randomPhoneMerchant = RandomUtil.getRandomPhoneNum();
        randomPhoneUser = RandomUtil.getRandomPhoneNum();
        //注册
        CexCommonOption.register(randomPhoneMerchant,pwd,area,"000000");
        CexCommonOption.register(randomPhoneUser, pwd, area,"000000");
        //登陆
        merchanttoken = userCexLogin(randomPhoneMerchant, pwd, area);
        usertoken = userCexLogin(randomPhoneUser, pwd, area);
        log.info("mechant用户token:"+merchanttoken);
        log.info("user用户token:"+usertoken);
        AssertTool.assertNotEquals(merchanttoken,null);
        AssertTool.assertNotEquals(usertoken,null);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testRegistAndLogin",description = "身份认证")
    public void testC2CIdentity() throws InterruptedException{
        //提交认证
        String certifercationMerchant = cexIdentity(merchanttoken);
        String certifercationUser = cexIdentity(usertoken);
        //初审
        firstTrial(certifercationMerchant);
        firstTrial(certifercationUser);
        //复审
        cexReviewing(certifercationMerchant);
        cexReviewing(certifercationUser);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testC2CIdentity",description = "设置两个账户的资金密码")
    public void testC2CSetSecurityPwd(){
        setSecurityPwd(merchanttoken);
        setSecurityPwd(usertoken);
    }


    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "账户设置昵称",dependsOnMethods = "testC2CSetSecurityPwd")
    public void testAddNickName() throws IOException {
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("nickname","merchant"+RandomUtil.generateString(6));
        Response response = OkHttpClientManager.post(c2cip+nikeNameAddUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("设置merchant昵称入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置merchant昵称出参：",rspjson.toJSONString());
        log.info("-------------Set merchant nickname response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        header.put("CEXTOKEN",usertoken);
        jsonbody.put("nickname","测试user"+RandomUtil.generateString(6));
        Response response1 = OkHttpClientManager.post(c2cip+nikeNameAddUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson1 = resultDeal(response1);
        Allure.addAttachment("设置测试user昵称入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置测试user昵称出参：",rspjson1.toJSONString());
        log.info("-------------Set user nickname response is:"+rspjson1);
        AssertTool.isContainsExpect("000000",rspjson1.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAddNickName", description = "添加商户")
    public void testMerchantAdd() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        String sql1 = String.format("SELECT user_no from member_user WHERE mobile_num = '%s';\n",randomPhoneMerchant);
        String userNo = new DataBaseManager().executeSingleQuery(sql1,cexmysql).getString(0);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("userId",JSON.parseObject(userNo).get("user_no").toString());
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("buyLimit","100");
        jsonbody.put("sellLimit","100");
        Response response = OkHttpClientManager.post(c2cip+merchantAddUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户添加入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户添加出参：",rspjson.toJSONString());
        log.info("-------------Merchant add response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "上传图片",dependsOnMethods = "testMerchantAdd")
    public void testUploadImage() throws InterruptedException{
        userImageUrl = userUploadFile(fileUrl,usertoken,"2.jpg",userUploadImageUrl);
        Thread.sleep(1000);
        merchantImageUrl = userUploadFile(fileUrl,merchanttoken,"1.jpeg",merchantUploadImageUrl);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "设置merchant微信支付方式",dependsOnMethods = "testUploadImage")
    public void testMerchantPmCreateWe() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        System.out.println(header.get("CEXTOKEN"));
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("realName","mer1"+randomPhoneUser);
        jsonbody.put("type","WECHAT");
        jsonbody.put("account","we"+randomPhoneUser+"11");
        jsonbody.put("qrcodeUrl",merchantImageUrl);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+merchantPmCreateUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("设置merchant微信支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置merchant微信支付方式出参：",rspjson.toJSONString());
        log.info("-------------Set merchant wechat pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }


    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "设置merchant银行卡支付方式",dependsOnMethods = "testMerchantPmCreateWe")
    public void testMerchantPmCreateBankCard() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("type","BANKCARD");
        jsonbody.put("realName","me1"+randomPhoneMerchant);
        jsonbody.put("account",merchantBankAccount+"1");
        jsonbody.put("bankName","bank1");
        jsonbody.put("bankSubName","test1");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("qrcodeUrl","http://172.29.16.161/2.jpeg");
        Response response = OkHttpClientManager.post(c2cip+merchantCreateBankCardUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("设置merchant银行卡支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置merchant银行卡支付方式出参：",rspjson.toJSONString());
        log.info("-------------Set merchant bank card pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "设置user微信支付方式",dependsOnMethods = "testMerchantPmCreateBankCard")
    public void testUserPmCreateWe() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("type","WECHAT");
        jsonbody.put("realName","us1"+randomPhoneUser);
        jsonbody.put("account","we1"+randomPhoneUser);
        jsonbody.put("qrcodeUrl",userImageUrl);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+pmCreateUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("设置user微信支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置user微信支付方式出参：",rspjson.toJSONString());
        log.info("-------------Set merchant wechat pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "设置user银行卡支付方式",dependsOnMethods = "testUserPmCreateWe")
    public void testUserPmCreateBankCard() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("type","BANKCARD");
        jsonbody.put("realName","us1"+randomPhoneUser);
        jsonbody.put("qrcodeUrl","http://172.29.16.161/2.jpg");
        jsonbody.put("account",userBankAccount+"1");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("bankName","bank");
        jsonbody.put("bankSubName","test");
        System.out.println(jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(c2cip+pmCreateUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("设置user银行卡支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("设置user银行卡支付方式出参：",rspjson.toJSONString());
        log.info("-------------Set merchant bank card pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "查询支付方式",dependsOnMethods = "testUserPmCreateBankCard")
    public void testGetPayMethod() throws IOException{
        userWechatPayId = getPayMethodId(usertoken,"WECHAT",userListUrl);
        merchantWechatPayId = getPayMethodId(merchanttoken,"WECHAT",merchantListUrl);
        userBankCardPayId = getPayMethodId(usertoken,"BANKCARD",userListUrl);
        merchantBankCarPayId = getPayMethodId(merchanttoken,"BANKCARD",merchantListUrl);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "修改merchant微信支付方式",dependsOnMethods = "testGetPayMethod")
    public void testMerchantPmUpdateWe() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        System.out.println(header.get("CEXTOKEN"));
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",merchantWechatPayId);
        jsonbody.put("realName","merchant"+randomPhoneUser);
        jsonbody.put("type","WECHAT");
        jsonbody.put("account","we"+randomPhoneUser+"me");
        jsonbody.put("qrcodeUrl",merchantImageUrl);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+merchantUpdatePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改merchant微信支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改merchant微信支付方式出参：",rspjson.toJSONString());
        log.info("-------------Update merchant wechat pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }


    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "修改merchant银行卡支付方式",dependsOnMethods = "testMerchantPmUpdateWe")
    public void testMerchantPmUpdateBankCard() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",merchantBankCarPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("realName","merchant"+randomPhoneMerchant);
        jsonbody.put("account",merchantBankAccount);
        jsonbody.put("bankName","bank111");
        jsonbody.put("bankSubName","test支行");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("qrcodeUrl","http://172.29.16.161/2.jpeg");
        Response response = OkHttpClientManager.post(c2cip+merchantUpdateBankPayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改merchant银行卡支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改merchant银行卡支付方式出参：",rspjson.toJSONString());
        log.info("-------------Update merchant bank card pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "修改user微信支付方式",dependsOnMethods = "testMerchantPmUpdateBankCard")
    public void testUserPmUpdateWe() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",userWechatPayId);
        jsonbody.put("type","WECHAT");
        jsonbody.put("realName","user"+randomPhoneUser);
        jsonbody.put("account","we"+randomPhoneUser);
        jsonbody.put("qrcodeUrl",userImageUrl);
        jsonbody.put("securityPwd",securityPwd);
        System.out.println(jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(c2cip+userUpdatePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改user微信支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改user微信支付方式出参：",rspjson.toJSONString());
        log.info("-------------Update merchant wechat pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "修改user银行卡支付方式",dependsOnMethods = "testUserPmUpdateWe")
    public void testUserPmUpdateBankCard() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",userBankCardPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("realName","user"+randomPhoneUser);
        jsonbody.put("qrcodeUrl","http://172.29.16.161/2.jpg");
        jsonbody.put("account",userBankAccount);
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("bankName","bank111");
        jsonbody.put("bankSubName","test支行");
        System.out.println(jsonbody.toJSONString());
        Response response = OkHttpClientManager.post(c2cip+userUpdateBankPayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改user银行卡支付方式入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改user银行卡支付方式出参：",rspjson.toJSONString());
        log.info("-------------Update merchant bank card pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserPmUpdateBankCard", description = "商户禁用支付方式")
    public void testMerchantDisablePayMethod() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",merchantBankCarPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("status","0");
        Response response = OkHttpClientManager.post(c2cip+merchantEnablePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户禁用支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户禁用支付出参：",rspjson.toJSONString());
        log.info("-------------Merchant disable pay method response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }


    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantDisablePayMethod", description = "充值")
    public void testDeposit() throws InterruptedException{
        String address1 = CexCommonOption.getAddress(randomPhoneMerchant,pwd,"USDT-ERC20");
        String rspCode1 = withDraw(presetUsersecurityPwd,address1,presetToken,amount,currencyCoin,"USDT-ERC20");
        AssertTool.isContainsExpect("000000",rspCode1);
        String sql1 = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhoneMerchant,"USDT");
        log.info("--------Deposit sql is:"+sql1);
        Thread.sleep(30000);
        AssertTool.isContainsExpect("{\"amount\":\"100.000000000000000000000000000000\"}",cexmysql,sql1);
        String address2 = CexCommonOption.getAddress(randomPhoneUser,pwd,"USDT-ERC20");
        String rspCode2 = withDraw(presetUsersecurityPwd,address2,presetToken,amount,currencyCoin,"USDT-ERC20");
        AssertTool.isContainsExpect("000000",rspCode2);
        String sql2 = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhoneUser,"USDT");
        log.info("--------Deposit sql is:"+sql2);
        Thread.sleep(30000);
        AssertTool.isContainsExpect("{\"amount\":\"100.000000000000000000000000000000\"}",cexmysql,sql2);
    }

    @Feature("划转")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit", description = "用户划入")
    public void testUserTransferIn() throws IOException{
        usertoken = CexCommonOption.userCexLogin(randomPhoneUser, pwd, area);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("amount",amount);
        Response response = OkHttpClientManager.post(c2cip+transferInUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("用户转入入参：",jsonbody.toJSONString());
        Allure.addAttachment("用户转入出参：",rspjson.toJSONString());
        log.info("-------------User transfer in response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }
    @Feature("划转")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserTransferIn", description = "商户划入")
    public void testMerchantTransferIn() throws IOException{
        merchanttoken = CexCommonOption.userCexLogin(randomPhoneMerchant, pwd, area);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("amount",amount);
        Response response = OkHttpClientManager.post(c2cip+transferInUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户转入入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户转入出参：",rspjson.toJSONString());
        log.info("-------------User transfer in response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }



    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantTransferIn", description = "发布买入广告")
    public void testAddBuyTrade() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeType","BUY");
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("amount","50");
        jsonbody.put("minPriceLimit","0.01");
        jsonbody.put("maxPriceLimit","500");
        jsonbody.put("unitPrice","0.5");
        jsonbody.put("remark","autotest");
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+tradeAddUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("发布买入广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("发布买入广告出参：",rspjson.toJSONString());
        log.info("-------------Add buy trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        tradeMerchantBuyId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("tradeId").toString();
        //查询冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals("0",freezeAmount);
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAddBuyTrade", description = "修改买入广告")
    public void testUpdateBuyTrade() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeMerchantBuyId);
        jsonbody.put("tradeType","BUY");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("amount","100");
        jsonbody.put("remark","autotest");
        jsonbody.put("minPriceLimit","0.1");
        jsonbody.put("maxPriceLimit","1000");
        jsonbody.put("unitPrice","1");
        Response response = OkHttpClientManager.post(c2cip+tradeUpdateUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改买入广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改买入广告出参：",rspjson.toJSONString());
        log.info("-------------Update buy trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        tradeMerchantBuyId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("tradeId").toString();
        System.out.println("tradeId:"+tradeMerchantBuyId);
        //查询冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals("0",freezeAmount);
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUpdateBuyTrade", description = "发布卖出广告")
    public void testAddSellTrade() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeType","SELL");
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("amount","50");
        jsonbody.put("unitPrice","1");
        jsonbody.put("remark","autotest");
        jsonbody.put("minPriceLimit","0.01");
        jsonbody.put("maxPriceLimit","500");
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+tradeAddUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("发布卖出广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("发布卖出广告出参：",rspjson.toJSONString());
        log.info("-------------Add sell trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        tradeMerchantSellId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("tradeId").toString();
        //查询冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals("50",freezeAmount);
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAddSellTrade", description = "修改卖出广告")
    public void testUpdateSellTrade() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeMerchantSellId);
        jsonbody.put("tradeType","SELL");
        jsonbody.put("amount","100");
        jsonbody.put("remark","autotest");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("currency",currencyCoin);
        jsonbody.put("minPriceLimit","0.1");
        jsonbody.put("maxPriceLimit","1000");
        jsonbody.put("unitPrice","2");
        Response response = OkHttpClientManager.post(c2cip+tradeUpdateUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("修改卖出广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("修改卖出广告出参：",rspjson.toJSONString());
        log.info("-------------Update sell" +
                " trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        tradeMerchantSellId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("tradeId").toString();
        //查询冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals(freezeAmount,"100");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUpdateSellTrade", description = "提交买入订单")
    public void testSubmitBuyOrder() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeMerchantSellId);
        jsonbody.put("totalPrice","20");
        Response response = OkHttpClientManager.post(c2cip+submitBuyOrderUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("提交买入订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("提交买入订单出参：",rspjson.toJSONString());
        log.info("-------------Submit buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        orderId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("orderId").toString();
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantSellId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"90");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSubmitBuyOrder", description = "用户确认支付")
    public void testUserReConfirmPay() throws IOException,InterruptedException{
        Thread.sleep(1000);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("paymentMethod","BANKCARD");
        jsonbody.put("receiptRealName","merchant"+randomPhoneMerchant);
        jsonbody.put("receiptAccount",merchantBankAccount);
        Response response = OkHttpClientManager.post(c2cip+userConfirmPayUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("用户再次确认支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("用户再次确认支付出参：",rspjson.toJSONString());
        log.info("-------------User reconfirm pay response is:"+rspjson);
        AssertTool.isContainsExpect("888023",rspjson.get("respCode").toString());
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserReConfirmPay", description = "用户取消买入订单")
    public void testUserCancelOrder() throws IOException {
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId", orderId);
        Response response = OkHttpClientManager.post(c2cip+userCancelUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("用户取消买入订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("用户取消买入订单出参：",rspjson.toJSONString());
        log.info("-------------Cancel buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantSellId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"100");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserCancelOrder", description = "商户启用支付方式")
    public void testMerchantEnablePayMethod() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",merchantBankCarPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("status","1");
        Response response = OkHttpClientManager.post(c2cip+merchantEnablePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户启用支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户启用支付出参：",rspjson.toJSONString());
        log.info("-------------Merchant enable pay method response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantEnablePayMethod", description = "再次提交买入订单")
    public void testReSubmitBuyOrder() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("totalPrice","100");
        jsonbody.put("tradeId",tradeMerchantSellId);
        Response response = OkHttpClientManager.post(c2cip+submitBuyOrderUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("再次提交买入订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("再次提交买入订单出参：",rspjson.toJSONString());
        log.info("-------------Submit buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        orderId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("orderId").toString();
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantSellId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"50");
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testReSubmitBuyOrder", description = "用户再次确认支付")
    public void testUserConfirmPay() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("paymentMethod","BANKCARD");
        jsonbody.put("receiptRealName","merchant"+randomPhoneMerchant);
        jsonbody.put("receiptAccount",merchantBankAccount);
        Response response = OkHttpClientManager.post(c2cip+userConfirmPayUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("用户确认支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("用户确认支付出参：",rspjson.toJSONString());
        log.info("-------------User confirm pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }





    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserConfirmPay", description = "商户确认收款")
    public void testMerchantConfirm() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+merchantConfirmUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户确认收款入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户确认收款出参：",rspjson.toJSONString());
        log.info("-------------User confirm pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        //查询商户冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals(freezeAmount,"50");
        //查询商户总资产
        String merchantAmount = StringUtil.stripTrailingZeros(queryC2CAsset("amount",merchanttoken));
        AssertTool.assertEquals(freezeAmount,"50");
        //查询用户总资产
        String userAmount = StringUtil.stripTrailingZeros(queryC2CAsset("amount",usertoken));
        AssertTool.assertEquals(userAmount,"150");
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantSellId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"50");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantConfirm", description = "普通用户禁用支付方式")
    public void testUserDisablePayMethod() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",userBankCardPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("status","0");
        Response response = OkHttpClientManager.post(c2cip+userEnablePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("普通用户禁用支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("普通用户禁用支付出参：",rspjson.toJSONString());
        log.info("-------------User disable pay method response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserDisablePayMethod", description = "用户提交卖出订单")
    public void testSubmitSellOrder() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("totalPrice","10");
        jsonbody.put("tradeId",tradeMerchantBuyId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+submitBuyOrderUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("提交卖出订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("提交卖出订单出参：",rspjson.toJSONString());
        log.info("-------------Submit buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        orderId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("orderId").toString();
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantBuyId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"90");
        //查询用户总资产
        String userAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",usertoken));
        AssertTool.assertEquals(userAmount,"10");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSubmitSellOrder", description = "商家确认支付")
    public void testMerchantConfirmPay() throws IOException,InterruptedException{
        Thread.sleep(1000);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("paymentMethod","BANKCARD");
        jsonbody.put("receiptRealName","user"+randomPhoneUser);
        jsonbody.put("receiptAccount",userBankAccount);
        Response response = OkHttpClientManager.post(c2cip+merchantConfirmPayUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商家确认支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("商家确认支付出参：",rspjson.toJSONString());
        log.info("-------------Merchant confirm pay response is:"+rspjson);
        AssertTool.isContainsExpect("888023",rspjson.get("respCode").toString());
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantConfirmPay", description = "商户取消买入订单")
    public void testUserCancelSellOrder() throws IOException {
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId", orderId);
        Response response = OkHttpClientManager.post(c2cip+merchantCancelUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户取消买入订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户取消买入订单出参：",rspjson.toJSONString());
        log.info("-------------Cancel buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        //查询冻结广告
        String freezeAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantBuyId,"freezeAmount"));
        AssertTool.assertEquals(freezeAmount,"0");
        //查询可交易广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantBuyId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"100");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserCancelSellOrder", description = "普通用户启用支付方式")
    public void testUserEnablePayMethod() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("id",userBankCardPayId);
        jsonbody.put("type","BANKCARD");
        jsonbody.put("securityPwd",securityPwd);
        jsonbody.put("status","1");
        Response response = OkHttpClientManager.post(c2cip+userEnablePayMethod,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("普通用户启用支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("普通用户启用支付出参：",rspjson.toJSONString());
        log.info("-------------User enable pay method response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }



    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserEnablePayMethod", description = "用户再次提交卖出订单")
    public void testSubmitReSellOrder() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("totalPrice","50");
        jsonbody.put("tradeId",tradeMerchantBuyId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+submitBuyOrderUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("再次提交卖出订单入参：",jsonbody.toJSONString());
        Allure.addAttachment("再次提交卖出订单出参：",rspjson.toJSONString());
        log.info("-------------Resubmit buy order response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        orderId = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get("orderId").toString();
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantBuyId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"50");
        //查询用户冻结资产
        String userAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",usertoken));
        AssertTool.assertEquals(userAmount,"50");
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSubmitReSellOrder", description = "商家确认支付")
    public void testMerchantReConfirmPay() throws IOException,InterruptedException{
        Thread.sleep(1000);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("paymentMethod","BANKCARD");
        jsonbody.put("receiptRealName","user"+randomPhoneUser);
        jsonbody.put("receiptAccount",userBankAccount);
        Response response = OkHttpClientManager.post(c2cip+merchantConfirmPayUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商家确认支付入参：",jsonbody.toJSONString());
        Allure.addAttachment("商家确认支付出参：",rspjson.toJSONString());
        log.info("-------------Merchant confirm pay response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantReConfirmPay", description = "用户确认收款")
    public void testUserConfirm() throws IOException{
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("orderId",orderId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+userConfirmUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("用户确认收款入参：",jsonbody.toJSONString());
        Allure.addAttachment("用户确认收款出参：",rspjson.toJSONString());
        log.info("-------------User confirm receive response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
        //查询商户冻结资产
        String freezeAmount = StringUtil.stripTrailingZeros(queryC2CAsset("freezeAmount",merchanttoken));
        AssertTool.assertEquals(freezeAmount,"50");
        //查询商户总资产
        String merchantAmount = StringUtil.stripTrailingZeros(queryC2CAsset("amount",merchanttoken));
        AssertTool.assertEquals(merchantAmount,"100");
        //查询用户总资产
        String userAmount = StringUtil.stripTrailingZeros(queryC2CAsset("amount",usertoken));
        AssertTool.assertEquals(userAmount,"100");
        //查询冻结广告
        String remainAmount = StringUtil.stripTrailingZeros(selectOne(merchanttoken,tradeMerchantSellId,"remainAmount"));
        AssertTool.assertEquals(remainAmount,"50");
    }


    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserConfirm", description = "商户取消买入广告")
    public void testTradeBuyCancel() throws IOException {
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeMerchantBuyId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+tradeCancelUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户取消买入广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户取消买入广告出参：",rspjson.toJSONString());
        log.info("-------------Cancel buy trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("买卖")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTradeBuyCancel", description = "商户取消卖出广告")
    public void testTradeSellCancel() throws IOException {
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", merchanttoken);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeMerchantSellId);
        jsonbody.put("securityPwd",securityPwd);
        Response response = OkHttpClientManager.post(c2cip+tradeCancelUrl,jsonbody.toJSONString(),"application/json",header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("商户取消卖出广告入参：",jsonbody.toJSONString());
        Allure.addAttachment("商户取消卖出广告出参：",rspjson.toJSONString());
        log.info("-------------Cancel sell trade response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Feature("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTradeSellCancel", description = "商户划出")
    public void testMerchantTransferOut() throws IOException,InterruptedException{
        Thread.sleep(1000);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",merchanttoken);
        merchantReturnAmount = queryC2CAsset("amount",merchanttoken);
        JSONObject jsonbodyTransferOut = new JSONObject();
        jsonbodyTransferOut.put("currency",currencyCoin);
        jsonbodyTransferOut.put("amount",merchantReturnAmount);
        Response response1 = OkHttpClientManager.post(c2cip+transferOutUrl,jsonbodyTransferOut.toJSONString(),"application/json",header);
        JSONObject rspjson1 = resultDeal(response1);
        Allure.addAttachment("商户划出入参：",jsonbodyTransferOut.toJSONString());
        Allure.addAttachment("商户划出出参：",rspjson1.toJSONString());
        log.info("-------------Cancel buy order response is:"+rspjson1);
        AssertTool.isContainsExpect("000000",rspjson1.get("respCode").toString());
    }

    @Feature("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMerchantTransferOut", description = "用户划出")
    public void testUserTransferOut() throws IOException,InterruptedException{
        Thread.sleep(1000);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",usertoken);
        userReturnAmount = queryC2CAsset("amount",usertoken);
        JSONObject jsonbodyTransferOut = new JSONObject();
        jsonbodyTransferOut.put("currency",currencyCoin);
        jsonbodyTransferOut.put("amount",userReturnAmount);
        Response response1 = OkHttpClientManager.post(c2cip+transferOutUrl,jsonbodyTransferOut.toJSONString(),"application/json",header);
        JSONObject rspjson1 = resultDeal(response1);
        Allure.addAttachment("用户划出入参：",jsonbodyTransferOut.toJSONString());
        Allure.addAttachment("用户划出出参：",rspjson1.toJSONString());
        log.info("-------------Cancel buy order response is:"+rspjson1);
        AssertTool.isContainsExpect("000000",rspjson1.get("respCode").toString());
    }

    @Feature("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testUserTransferOut", description = "提币")
    public void testReturnDeposit() throws InterruptedException{
        //获取预置用户地址
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,"USDT-ERC20");
        //用户归还
        String rspCode2 = withDraw(presetUsersecurityPwd,address,usertoken,userReturnAmount,currencyCoin,"USDT-ERC20");
        AssertTool.isContainsExpect("000000",rspCode2);
        //商户归还
        String rspCode1 = withDraw(presetUsersecurityPwd,address,merchanttoken,merchantReturnAmount,currencyCoin,"USDT-ERC20");
        AssertTool.isContainsExpect("000000",rspCode1);
    }

}
