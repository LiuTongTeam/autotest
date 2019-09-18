package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.boss.tool.BossConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.*;

/**
 * @author shenqingyan
 * @create 2019/8/8 11:25
 * @desc 主流程测试类
 **/
@Slf4j
@Epic("CEX主流程")

public class CoreProcessTest extends BaseCase {
    //cex登陆token
    private String token = null;
    //BOSS身份认证ID
    private String certificate_no = null;
    //随机手机号，用于注册
    private String randomPhone = null;
    //限价买入不成交的订单号
    private String cancelAllBuyNo = null;
    //限价卖出不成交的订单号
    private String cancelAllSellNo = null;
    //部分买入不成交的订单号
    private String cancelPartBuyNo = null;
    //部分卖出不成交的订单号
    private String cancelPartSellNo = null;
    //下单交易数量
    private String limitPrice = "2";
    //成交前可用计价币种数量
    private BigDecimal preAvailableAmount = null;
    //成交前可用交易币种数量
    private BigDecimal preAvailableCoinAmount = null;
    //可用于交易数量
    private String availableAmount = null;
    //分笔成交第一次下单价格
    private String price1 = null;
    //分笔成交第二次下单价格
    private String price2 = null;
    //交易对
    String symbol = String.format("%s/%s",productCoin,currencyCoin);
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
        token = CexCommonOption.userCexLogin(randomPhone,pwd,area);
        AssertTool.assertNotEquals(null,token);
        log.info("------------cex token:"+token);
        Allure.addAttachment("登陆token：",token);
    }
    @Feature("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testLogin",description = "身份认证")
    public void testIdentity() throws IOException{
        //认证姓名随机字符串
        String userName = RandomUtil.generateString(10);
        JSONObject object = new JSONObject();
        object.put("countryId",countryId);
        //图片ID由上传文件接口返回
        object.put("backId", CexCommonOption.uploadFile(fileUrl,token,"1.jpeg"));
        object.put("frontId", CexCommonOption.uploadFile(fileUrl,token,"2.jpg"));
        object.put("userName", userName);
        object.put("certificateNo",RandomUtil.generateLong(18));
        object.put("personId", CexCommonOption.uploadFile(fileUrl,token,"3.png"));
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
        certificate_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql,cexmysql).getString(0)).getString("certificate_no");
        log.info("------certificate_no is:"+certificate_no+"\n");
    }
    @Feature("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testIdentity", description = "身份认证初审通过")
    public void testFirstTrial() throws IOException{
        //boss登陆token放入header
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
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
    @Feature("实名认证")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testFirstTrial", description = "身份认证复审通过")
    public void testReviewing() throws IOException{
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
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
    @Feature("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSecurityPwd", description = "充币"+depositCurrency)
    public void testDeposit1() throws InterruptedException{
        String address = CexCommonOption.getAddress(randomPhone,pwd,depositCurrency);
        String rspCode = withDraw(presetUsersecurityPwd,address,presetToken,depositAmount,depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,depositCurrency);
        log.info("--------Deposit sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",cexmysql,sql);
    }
    @Feature("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit1", description = "充币"+productCoin)
    public void testDeposit2() throws InterruptedException{
        String address = CexCommonOption.getAddress(randomPhone,pwd,productCoin);
        String rspCode = withDraw(presetUsersecurityPwd,address,presetToken,depositAmount,productCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,productCoin);
        log.info("--------Deposit KOFO sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",cexmysql,sql);
    }
    @Feature("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit2", description = "充币"+currencyCoin)
    public void testDeposit3() throws InterruptedException{
        String address = CexCommonOption.getAddress(randomPhone,pwd,currencyCoin);
        String rspCode = withDraw(presetUsersecurityPwd,address,presetToken,depositAmount,currencyCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,currencyCoin);
        log.info("--------Deposit USDT sql is:"+sql);
        Thread.sleep(60000);
        AssertTool.isContainsExpect("{\"amount\":\"20.000000000000000000000000000000\"}",cexmysql,sql);
    }
    @Feature("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testDeposit3", description = "提币数量超过账户剩余数量")
    public void testWithdrawFail(){
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,depositCurrency);
        token = CexCommonOption.userCexLogin(randomPhone,pwd,area);
        String rspCode = withDraw(securityPwd,address,token,"25",depositCurrency);
        AssertTool.isContainsExpect("100113",rspCode);
    }
    @Feature("充提币")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testWithdrawFail", description = "提币成功")
    public void testWithdraw() throws InterruptedException{
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,depositCurrency);
        String rspCode = withDraw(securityPwd,address,token,"15",depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        String sql = String.format("SELECT amount FROM account_info WHERE user_no = (SELECT user_no from member_user WHERE mobile_num = '%s') and currency = '%s';\n",randomPhone,depositCurrency);
        log.info("--------Deposit USDT sql is:"+sql);
        Thread.sleep(30000);
        AssertTool.isContainsExpect("{\"amount\":\"5.000000000000000000000000000000\"}",cexmysql,sql);
    }

    @Feature("交易")
    @Story("限价买入不成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testWithdraw",description = "测试账户下买单")
    public void testBuyOrder(){
        //下单前先批量撤销未成交的订单,只能撤销自动化测试账号下的单
        try {
            batchCancelOrder(symbol);
        }catch (Exception e){
            e.printStackTrace();
        }
        token = CexCommonOption.userCexLogin(randomPhone,pwd,area);
        Map result = order(symbol,"BUY","LMT",limitPrice,"5","5",token);
        cancelAllBuyNo = result.get("orderNo").toString();
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("下单后冻结"+currencyCoin+"金额:",frozeAmount);
        AssertTool.isContainsExpect("10",frozeAmount);
    }

    @Feature("交易")
    @Story("限价买入不成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testBuyOrder",description = "买单全部撤单")
    public void testCancelBuyOrder() throws InterruptedException{
        HashMap result = cancelOrder(token,cancelAllBuyNo);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        Thread.sleep(60000);
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("撤单后冻结"+currencyCoin+"金额:",frozeAmount);
        AssertTool.assertEquals(StringUtil.numStringRound(frozeAmount),"0");
    }

    @Feature("交易")
    @Story("限价卖出不成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testCancelBuyOrder",description = "测试账户下卖单")
    public void testSellOrder(){
        Map result = order(symbol,"SELL","LMT",limitPrice,"5","5",token);
        cancelAllSellNo = result.get("orderNo").toString();
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("下单后冻结币"+productCoin+"个数为：",frozeAmount);
        AssertTool.isContainsExpect(StringUtil.numStringRound(frozeAmount),"5");
    }

    @Feature("交易")
    @Story("限价卖出不成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testSellOrder",description = "卖单全部撤单")
    public void testCancelSellOrder() throws InterruptedException{
        HashMap result = cancelOrder(token,cancelAllSellNo);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        Thread.sleep(30000);
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("撤单后冻结币"+productCoin+"个数：",frozeAmount);
        AssertTool.assertEquals(StringUtil.numStringRound(frozeAmount),"0");
    }

    @Feature("交易")
    @Story("限价买入部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testCancelSellOrder",description = "测试账户下买单")
    public void testPartBuyOrder() throws InterruptedException{
        Map result = order(symbol,"BUY","LMT",limitPrice,"5","5",token);
        cancelPartBuyNo = result.get("orderNo").toString();
        Thread.sleep(30000);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("下单后冻结"+currencyCoin+"金额",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("10",StringUtil.stripTrailingZeros(frozeAmount));
    }


    @Feature("交易")
    @Story("限价买入部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartBuyOrder",description = "预置账户下卖单")
    public void testPartBuyOrder1() throws InterruptedException{
        //获取成交前测试账户币个数
        preAvailableCoinAmount = new BigDecimal(queryCexAsset(token,productCoin).get("availableAmount").toString());
        //预置账户下卖单
        Map result = order(symbol,"SELL","LMT",limitPrice,"3","3",presetToken);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        Thread.sleep(30000);
        //成交后查询测试账户的支付余额冻结金额
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("成交后冻结"+currencyCoin+"金额为：",StringUtil.stripTrailingZeros(frozeAmount));
        //断言冻结计价币种是否减少
        AssertTool.isContainsExpect("4",StringUtil.stripTrailingZeros(frozeAmount));
        //成交后查询测试账户的可用币种个数
        String availableAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后可用币"+productCoin+"个数为：",availableAmount);
        log.info("可用币个数："+availableAmount);
        //断言成交后可用币种是否与计算的可用个数一致
        AssertTool.isContainsExpect(countAvailableAmount(symbol,preAvailableCoinAmount.toString(),"3"),availableAmount);
    }
    @Feature("交易")
    @Story("限价买入部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartBuyOrder1",description = "测试账户撤单")
    public void testPartBuyCancelOrder() throws InterruptedException{
        HashMap result = cancelOrder(token,cancelPartBuyNo);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        Thread.sleep(30000);
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("测试账户撤单后冻结"+currencyCoin+"金额：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.assertEquals(StringUtil.stripTrailingZeros(frozeAmount),"0");
    }

    @Feature("交易")
    @Story("限价卖出部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartBuyCancelOrder",description = "测试账户下卖单")
    public void testPartSellOrder() throws InterruptedException{
        Map result = order(symbol,"SELL","LMT",limitPrice,"2","2",token);
        cancelPartSellNo = result.get("orderNo").toString();
        Thread.sleep(30000);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("下单后冻结币"+productCoin+"个数：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("2",StringUtil.stripTrailingZeros(frozeAmount));
    }


    @Feature("交易")
    @Story("限价卖出部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartSellOrder",description = "预置账户下买单")
    public void testPartSellOrder1() throws InterruptedException{
        //获取成交前测试账户计价币个数
        preAvailableAmount = new BigDecimal(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交前可用"+currencyCoin+"金额为：",StringUtil.stripTrailingZeros(preAvailableAmount.toString()));
        log.info("成交前可用金额："+preAvailableAmount.toString());
        //预置账户下买单
        Map result = order(symbol,"BUY","LMT",limitPrice,"1","1",presetToken);
        Thread.sleep(30000);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        //成交后查询测试账户的卖出币种冻结数量
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("成交后冻结"+productCoin+"个数为：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("1",StringUtil.stripTrailingZeros(frozeAmount));
        log.info("冻结数量："+StringUtil.stripTrailingZeros(frozeAmount));
        //查询计价币种可用数量
        String availableAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后可用"+currencyCoin+"金额为：",availableAmount);
        log.info("成交后可用金额："+availableAmount);
        //断言成交后可用币种是否与计算的可用个数一致
        AssertTool.isContainsExpect(countAvailableAmount(symbol,preAvailableAmount.toString(),limitPrice),availableAmount);
    }

    @Feature("交易")
    @Story("限价卖出部分成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartSellOrder1",description = "测试账户撤单")
    public void testPartSellCancelOrder() throws InterruptedException{
        HashMap result = cancelOrder(token,cancelPartSellNo);
        AssertTool.isContainsExpect("000000",result.get("code").toString());
        Thread.sleep(30000);
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("测试账户撤单之后冻结"+productCoin+"个数：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.assertEquals(StringUtil.stripTrailingZeros(frozeAmount),"0");
    }

    @Feature("交易")
    @Story("限价买入全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testPartSellCancelOrder",description = "查询成交前余额")
    public void testAllBuyOrderQuery() throws InterruptedException{
        //获取成交前计价币种数量
        preAvailableAmount = new BigDecimal(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交前可用计价币"+currencyCoin+"为：",StringUtil.stripTrailingZeros(preAvailableAmount.toString()));
        log.info("成交前可用计价币为："+preAvailableAmount.toString());
        //获取成交前交易币种数量
        preAvailableCoinAmount = new BigDecimal(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交前可用交易币"+productCoin+"为：",StringUtil.stripTrailingZeros(preAvailableCoinAmount.toString()));
        log.info("成交前可用交易币为："+preAvailableCoinAmount.toString());


    }


    @Feature("交易")
    @Story("限价买入全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllBuyOrderQuery",description = "预置账户下卖单")
    public void testAllBuyOrderPreSell() throws InterruptedException {
        //计算可购买个数
        availableAmount = StringUtil.stripTrailingZeros(preAvailableAmount.divide(new BigDecimal(limitPrice)).toString());
        //预置账户下卖单，卖单数量为测试账户最多可购买个数
        Map sellResult = order(symbol,"SELL","LMT",limitPrice,availableAmount,availableAmount,presetToken);
        AssertTool.isContainsExpect("000000",sellResult.get("code").toString());
        Thread.sleep(5000);
    }


    @Feature("交易")
    @Story("限价买入全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllBuyOrderPreSell",description = "测试账户下买单")
    public void testAllBuyOrderBuy() throws InterruptedException {
        //测试账户下买单
        Map buyResult = order(symbol,"BUY","LMT",limitPrice,availableAmount,availableAmount,token);
        AssertTool.isContainsExpect("000000",buyResult.get("code").toString());
        Thread.sleep(30000);
        //成交后查询测试账户的计价币种冻结数量
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("成交后冻结"+currencyCoin+"个数为：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(frozeAmount));
        log.info("冻结数量："+StringUtil.stripTrailingZeros(frozeAmount));
        //查询计价币种可用数量
        String currencyCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后计价币种"+currencyCoin+"可用金额为：",currencyCoinAmount);
        log.info("成交后计价币种可用金额："+currencyCoinAmount);
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(currencyCoinAmount));
        //成交后查询测试账户的可用交易币种个数
        String productCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后可用币"+productCoin+"个数为：",productCoinAmount);
        log.info("可用币个数："+productCoinAmount);
        //断言成交后可用交易币种是否与计算的可用个数一致
        AssertTool.isContainsExpect(countAvailableAmount(symbol,preAvailableCoinAmount.toString(),availableAmount),productCoinAmount);
    }


    @Feature("交易")
    @Story("限价卖出全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllBuyOrderBuy",description = "查询成交前余额")
    public void testAllSellOrderQuery() throws InterruptedException{
        //获取交易前计价币种数量
        preAvailableAmount = new BigDecimal(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("交易前可用计价币"+currencyCoin+"为：",StringUtil.stripTrailingZeros(preAvailableAmount.toString()));
        log.info("成交前可用计价币为："+preAvailableAmount.toString());
        //获取交易前交易币种数量
        preAvailableCoinAmount = new BigDecimal(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("交易前可用交易币"+productCoin+"为：",StringUtil.stripTrailingZeros(preAvailableCoinAmount.toString()));
        log.info("交易前可用交易币为："+preAvailableCoinAmount.toString());

        }

    @Feature("交易")
    @Story("限价卖出全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllSellOrderQuery",description = "预置账户下买单")
    public void testAllSellOrderPreBuy() throws InterruptedException {
        //计算可购买个数
        availableAmount = StringUtil.stripTrailingZeros(preAvailableCoinAmount.toString());
        //预置账户下买单，买单数量为测试账户最多可卖个数
        Map buyResult = order(symbol,"BUY","LMT",limitPrice,availableAmount,availableAmount,presetToken);
        AssertTool.isContainsExpect("000000",buyResult.get("code").toString());
        Thread.sleep(5000);
    }


    @Feature("交易")
    @Story("限价卖出全部成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllSellOrderPreBuy",description = "测试账户下卖单")
    public void testAllSellOrderSell() throws InterruptedException {
        //测试账户下卖单
        Map sellResult = order(symbol,"SELL","LMT",limitPrice,availableAmount,availableAmount,token);
        AssertTool.isContainsExpect("000000",sellResult.get("code").toString());
        Thread.sleep(30000);
        //成交后查询测试账户的交易币种冻结数量
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("交易后冻结交易币种"+productCoin+"个数为：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(frozeAmount));
        log.info("交易后冻结交易币种个数为："+StringUtil.stripTrailingZeros(frozeAmount));
        //查询交易币种可用数量
        String productCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后交易币种"+productCoin+"可用金额为：",productCoinAmount);
        log.info("成交后交易币种可用金额："+productCoinAmount);
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(productCoinAmount));
        //成交后查询测试账户的可用计价币种个数
        String currencyCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后计价币"+currencyCoin+"个数为：",currencyCoinAmount);
        log.info("计价币个数："+currencyCoinAmount);
        //断言成交后可用计价币种是否与计算的可用个数一致
        String getAmount = new BigDecimal(availableAmount).multiply(new BigDecimal(limitPrice)).toString();
        AssertTool.isContainsExpect(countAvailableAmount(symbol,preAvailableAmount.toString(),getAmount),currencyCoinAmount);

    }

    @Feature("交易")
    @Story("限价买入分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testAllSellOrderSell",description = "查询成交前余额")
    public void testTwiceBuyOrderQuery() throws InterruptedException{
        //获取交易前计价币种数量
        preAvailableAmount = new BigDecimal(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("交易前可用计价币"+currencyCoin+"为：",StringUtil.stripTrailingZeros(preAvailableAmount.toString()));
        log.info("成交前可用计价币为："+preAvailableAmount.toString());
    }


    @Feature("交易")
    @Story("限价买入分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceBuyOrderQuery",description = "预置账户下两笔卖单")
    public void testTwiceBuyOrderPreSell() throws InterruptedException {
        //设置卖单金额
        price1 = StringUtil.stripTrailingZeros(new BigDecimal(limitPrice).subtract(new BigDecimal("0.03")).toString());
        price2 = StringUtil.stripTrailingZeros(new BigDecimal(limitPrice).subtract(new BigDecimal("0.02")).toString());
        Map sellResult1 = order(symbol, "SELL", "LMT", price1, "1", "1", presetToken);
        AssertTool.isContainsExpect("000000", sellResult1.get("code").toString());
        //预置账户下卖单，卖单数量为1
        Map sellResult2 = order(symbol, "SELL", "LMT", price2, "1", "1", presetToken);
        AssertTool.isContainsExpect("000000", sellResult2.get("code").toString());
        Thread.sleep(5000);
    }



    @Feature("交易")
    @Story("限价买入分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceBuyOrderPreSell",description = "测试账户下一笔买单")
    public void testTwiceBuyOrderBuy() throws InterruptedException {
        //测试账户下买单
        Map buyResult = order(symbol,"BUY","LMT",limitPrice,"2","2",token);
        AssertTool.isContainsExpect("000000",buyResult.get("code").toString());
        Thread.sleep(30000);
        //成交后查询测试账户计价币种冻结数量
        String frozeAmount = queryCexAsset(token,currencyCoin).get("frozenAmount").toString();
        Allure.addAttachment("成交后冻结计价币种"+currencyCoin+"个数为：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(frozeAmount));
        log.info("冻结计价币种个数："+StringUtil.stripTrailingZeros(frozeAmount));
        //查询计价币种可用数量
        String currencyCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后计价币种"+"可用金额：",currencyCoinAmount);
        log.info("成交后计价币种可用金额为："+currencyCoinAmount);
        //预期结果为：交易前计价币种数量-(salePrice1*1+salePrice2*1)
        String expect = StringUtil.stripTrailingZeros(preAvailableAmount.subtract(new BigDecimal(price1).add(new BigDecimal(price2))).toString());
        AssertTool.isContainsExpect(expect,StringUtil.stripTrailingZeros(currencyCoinAmount));
        //成交后查询测试账户的可用交易币种个数
        String productCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后可用币"+productCoin+"个数为：",productCoinAmount);
        log.info("可用币个数："+productCoinAmount);
        //断言成交后可用交易币种是否与计算的可用个数一致
        AssertTool.isContainsExpect(countAvailableAmount(symbol,"0","2"),productCoinAmount);
    }


    @Feature("交易")
    @Story("限价卖出分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceBuyOrderBuy",description = "成交前查询")
    public void testTwiceSellOrderQuery() throws InterruptedException{
        //充值productCoin
        String address = CexCommonOption.getAddress(randomPhone,pwd,productCoin);
        String rspCode = withDraw(presetUsersecurityPwd,address,presetToken,depositAmount,productCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        Thread.sleep(30000);
        token = userCexLogin(randomPhone,pwd,area);
        //获取成交前计价币种数量
        preAvailableAmount = new BigDecimal(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交前可用计价币"+currencyCoin+"为：",StringUtil.stripTrailingZeros(preAvailableAmount.toString()));
        log.info("成交前可用计价币为："+preAvailableAmount.toString());
        //获取交易前交易币种数量
        preAvailableCoinAmount = new BigDecimal(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("交易前可用交易币"+productCoin+"为：",StringUtil.stripTrailingZeros(preAvailableCoinAmount.toString()));
        log.info("交易前可用交易币为："+preAvailableCoinAmount.toString());
    }

    @Feature("交易")
    @Story("限价卖出分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceSellOrderQuery",description = "预置账户下两笔买单")
    public void testTwiceSellOrderPreBuy() throws InterruptedException{
        //设置买单金额
        price1 = StringUtil.stripTrailingZeros(new BigDecimal(limitPrice).add(new BigDecimal("0.02")).toString());
        price2 = StringUtil.stripTrailingZeros(new BigDecimal(limitPrice).add(new BigDecimal("0.01")).toString());
        Map buyResult1 = order(symbol,"BUY","LMT",price1,"1","1",presetToken);
        AssertTool.isContainsExpect("000000",buyResult1.get("code").toString());
        //预置账户下买单，卖单数量为1
        Map buyResult2 = order(symbol,"BUY","LMT",price2,"1","1",presetToken);
        AssertTool.isContainsExpect("000000",buyResult2.get("code").toString());
        Thread.sleep(5000);
    }

    @Feature("交易")
    @Story("限价卖出分笔成交")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceSellOrderPreBuy",description = "测试账户下一笔卖单")
    public void testTwiceSellOrderSell() throws InterruptedException{
        //测试账户下卖单
        Map sellResult = order(symbol,"SELL","LMT",limitPrice,"2","2",token);
        AssertTool.isContainsExpect("000000",sellResult.get("code").toString());
        Thread.sleep(30000);
        //成交后查询测试账户交易币种冻结数量
        String frozeAmount = queryCexAsset(token,productCoin).get("frozenAmount").toString();
        Allure.addAttachment("成交后冻结交易币种"+productCoin+"个数为 ：",StringUtil.stripTrailingZeros(frozeAmount));
        AssertTool.isContainsExpect("0",StringUtil.stripTrailingZeros(frozeAmount));
        log.info("冻结交易币种个数："+StringUtil.stripTrailingZeros(frozeAmount));
        //查询交易币种可用数量
        String productCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,productCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后交易币种"+productCoin+"可用金额：",productCoinAmount);
        log.info("成交后交易币种可用金额为："+productCoinAmount);
        //预期结果为：交易前交易币种数量-2
        String expect = StringUtil.stripTrailingZeros(preAvailableCoinAmount.subtract(new BigDecimal("2")).toString());
        AssertTool.isContainsExpect(expect,StringUtil.stripTrailingZeros(productCoinAmount));
        //成交后查询测试账户的可用计价币种个数
        String currencyCoinAmount = StringUtil.stripTrailingZeros(queryCexAsset(token,currencyCoin).get("availableAmount").toString());
        Allure.addAttachment("成交后可用计价币"+currencyCoin+"个数为：",currencyCoinAmount);
        log.info("可用计价币个数："+currencyCoinAmount);
        //成交计价金额
        String dealPrice = StringUtil.stripTrailingZeros(new BigDecimal(price1).add(new BigDecimal(price2)).toString());
        //断言成交后可用计价币种是否与计算的可用个数一致
        AssertTool.isContainsExpect(countAvailableAmount(symbol,preAvailableAmount.toString(),dealPrice),currencyCoinAmount);
    }


    @Feature("交易")
    @Story("市价买入")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testTwiceSellOrderSell",description = "预置账户下一笔卖单")
    public void testMktBuyPreSell() throws InterruptedException{
        //预置账户下买单，卖单数量为1
        Map buyResult1 = order(symbol,"SELL","LMT",limitPrice,"1","1",presetToken);
        AssertTool.isContainsExpect("000000",buyResult1.get("code").toString());
        Thread.sleep(5000);
    }

    @Feature("交易")
    @Story("市价买入")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktBuyPreSell",description = "测试账户下一笔市价买单")
    public void testMktBuy() throws InterruptedException{
        //测试账户下卖单
        Map sellResult = order(symbol,"BUY","MKT","0","0","2",token);
        AssertTool.isContainsExpect("000000",sellResult.get("code").toString());
        Thread.sleep(5000);
    }

    @Feature("交易")
    @Story("市价卖出")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktBuy",description = "预置账户下一笔买单")
    public void testMktSellPreBuy() throws InterruptedException{
        //预置账户下买单，卖单数量为1
        Map buyResult1 = order(symbol,"BUY","LMT",limitPrice,"2","0",presetToken);
        AssertTool.isContainsExpect("000000",buyResult1.get("code").toString());
        Thread.sleep(5000);
    }

    @Feature("交易")
    @Story("市价卖出")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktSellPreBuy",description = "测试账户下一笔市价卖单")
    public void testMktSell() throws InterruptedException{
        //测试账户下卖单
        Map sellResult = order(symbol,"SELL","MKT","0","2","2",token);
        AssertTool.isContainsExpect("000000",sellResult.get("code").toString());
        Thread.sleep(5000);
    }


    @Feature("充提币")
    @Story("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktSell", description = "归还剩余depositCurrency币种")
    public void testWithdrawReturnDepositCurrency() throws InterruptedException{
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,depositCurrency);
        String amount = CexCommonOption.queryCexAsset(token,depositCurrency).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),depositCurrency);
        AssertTool.isContainsExpect("000000",rspCode);
        Allure.addAttachment("归还剩余币种"+depositCurrency+":",StringUtil.numStringRound(amount));
        Thread.sleep(30000);
    }


    @Feature("充提币")
    @Story("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktSell", description = "归还剩余productCoin币种")
    public void testWithdrawReturnProductCoin() throws InterruptedException{
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,productCoin);
        String amount = CexCommonOption.queryCexAsset(token,productCoin).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),productCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        Allure.addAttachment("归还剩余的币种"+productCoin+":",StringUtil.numStringRound(amount));
        Thread.sleep(30000);
    }

    @Feature("充提币")
    @Story("归还")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "testMktSell", description = "归还剩余currencyCoin币种")
    public void testWithdrawReturnCurrencyCoin() throws InterruptedException{
        String address = CexCommonOption.getAddress(presetUser,presetUserPwd,currencyCoin);
        String amount = CexCommonOption.queryCexAsset(token,currencyCoin).get("availableAmount").toString();
        String rspCode = withDraw(securityPwd,address,token, StringUtil.numStringRound(amount),currencyCoin);
        AssertTool.isContainsExpect("000000",rspCode);
        Allure.addAttachment("归还剩余的计价币种"+currencyCoin+":",StringUtil.numStringRound(amount));
        Thread.sleep(30000);
    }
}
