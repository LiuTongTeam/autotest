package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;

@Feature("WithdrawAddress提币地址的增删查主流程")

public class WithdrawAddressTest extends BaseCase {
    private String bizId = null;
    private String address = null;
    @BeforeClass
    public void getAddress(){
        address = CexCommonOption.getAddress(presetUser,presetUserPwd,depositCurrency);
    }
    //提币地址 新增的异常用例1
    @Test(description = "异常用例没有token，返回100006")
    public void withdrawAddressError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", depositCurrency);
        jsonbody.put("chain", "ETH");
        jsonbody.put("currencyAliasName", depositCurrency);
        jsonbody.put("securityPwd", presetUsersecurityPwd);
        jsonbody.put("currencyAddress", address);
        jsonbody.put("addressRemark", "lxm");
        jsonbody.put("labelContent", "");
        Response response = OkHttpClientManager.post(ip_gateway+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    //提币地址 新增的异常用例2
    @Test(description = "输入错误的资金密码，返回资金密码错误100108")
    public void withdrawAddressError2() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", depositCurrency);
        jsonbody.put("chain", "ETH");
        jsonbody.put("currencyAliasName", depositCurrency);
        jsonbody.put("securityPwd", "123");
        jsonbody.put("currencyAddress", address);
        jsonbody.put("addressRemark", "lxm");
        jsonbody.put("labelContent", "");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100108",rspjson.get("code").toString());
    }
    //查询该用户IDA币种的保存的提币地址，返回bizid用户后面的删除并新增
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "查询用户USDT提币地址，chain不传，返回000000，返回两个usdt币种地址")
    public void qwithdrawAddress() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", "USDT");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+qwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.assertEquals(JSON.parseArray(rspjson.getString("data")).size()==2,true);
    }

    //查询该用户IDA币种的保存的提币地址，返回bizid用户后面的删除并新增
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "查询用户USDT提币地址，currency不传，返回000000，返回两个usdt币种地址")
    public void qwithdrawAddress1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("chain", "ETH");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+qwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        AssertTool.assertEquals(JSON.parseArray(rspjson.getString("data")).size()>0,true);
    }

    //查询该用户IDA币种的保存的提币地址,currency为空
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "查询用户提币地址，返回000000，返回bizid用于后面删除并添加地址")
    public void qwithdrawAddress2() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency",depositCurrency);
        jsonbody.put("chain","ETH");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+qwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        System.out.println("chucan:"+rspjson.toJSONString());
        AssertTool.assertEquals(JSON.parseArray(rspjson.getString("data")).size()>0,true);
        try {
            bizId = JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("bizId").toString();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //删除该用户保存的IDA地址，后面可以新增IDA提币地址,如果biz为空断言100006，否则断言000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "qwithdrawAddress2",description = "删除该用户保存的IDA地址，后面可以新增IDA提币地址,返回000000")
    public void dwithdrawAddress() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("bizId", bizId);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+dwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        if(bizId!=null) {
            AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
        }
        else
        {
            AssertTool.isContainsExpect("100006", rspjson.get("code").toString());
        }

    }
    //增加删除地址的异常用例，bizid乱传一个
    @Test(description = "删除该用户保存的IDA地址，bizid乱传一个,返回100143")
    public void dwithdrawAddressError() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("bizId", "111");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+dwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100143", rspjson.get("code").toString());

    }

    //增加删除地址的异常用例，bizid乱传一个
    @Test(description = "删除该用户保存的IDA地址，bizid不传,返回100143")
    public void dwithdrawAddressError1() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("bizId", "");
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+dwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006", rspjson.get("code").toString());

    }
    //增加用户的IDA提币地址
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "dwithdrawAddress",description = "正常用例，返回000000")
    public void awithdrawAddress() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("currency", depositCurrency);
        jsonbody.put("chain", "ETH");
        jsonbody.put("currencyAliasName", depositCurrency);
        jsonbody.put("securityPwd", presetUsersecurityPwd);
        jsonbody.put("currencyAddress", address);
        jsonbody.put("addressRemark", "lxm");
        jsonbody.put("labelContent", "");
        System.out.println("in:"+jsonbody.toJSONString());
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }

    /**
     * @desc 异常用例的数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] TradeSymbolInfoErrorData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/AWithdrawAddress/error";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }

    //增加用户的提币地址异常用例
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "TradeSymbolInfoErrorData",dependsOnMethods = "awithdrawAddress",description = "添加提币地址异常用例")
    public void awithdrawAddress1(Map<?,?> param) throws IOException {
        JSONObject jsonbody = JSON.parseObject(param.get("body").toString());
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip_gateway+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment(param.get("comment").toString()+"入参",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect(param.get("assert").toString(),rspjson.get("code").toString());
    }

}
