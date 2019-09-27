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
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

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
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        object.put("securityPwd", presetUsersecurityPwd);
        object.put("currencyAddress", address);
        object.put("addressRemark", "lxm");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100006",rspjson.get("code").toString());
    }
    //提币地址 新增的异常用例2
    @Test(description = "输入错误的资金密码，返回资金密码错误100108")
    public void withdrawAddressError2() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency",depositCurrency);
        object.put("securityPwd", "123");
        object.put("currencyAddress", address);
        object.put("addressRemark", "lxm");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("100108",rspjson.get("code").toString());
    }
    //查询该用户IDA币种的保存的提币地址，返回bizid用户后面的删除并新增
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "查询用户提币地址，返回000000，返回bizid用于后面删除并添加地址")
    public void qwithdrawAddress() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+qwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        try {
            bizId = JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("bizId").toString();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //删除该用户保存的IDA地址，后面可以新增IDA提币地址,如果biz为空断言100006，否则断言000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "qwithdrawAddress",description = "删除该用户保存的IDA地址，后面可以新增IDA提币地址,返回000000")
    public void dwithdrawAddress() throws IOException {
        JSONObject object = new JSONObject();
        object.put("bizId", bizId);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+dwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
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
        JSONObject object = new JSONObject();
        object.put("bizId", "123");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+dwithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());

        AssertTool.isContainsExpect("100143", rspjson.get("code").toString());

    }
    //增加用户的IDA提币地址
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "dwithdrawAddress",description = "正常用例，返回000000")
    public void awithdrawAddress() throws IOException {
        JSONObject object = new JSONObject();
        object.put("currency", depositCurrency);
        object.put("securityPwd", presetUsersecurityPwd);
        object.put("currencyAddress", address);
        object.put("addressRemark", "lxm");
        object.put("labelContent", "");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data", object);
        jsonbody.put("lang", lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN", presetToken);
        Response response = OkHttpClientManager.post(ip+awithdrawAddressUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        Allure.addAttachment("入参：",jsonbody.toJSONString());
        Allure.addAttachment("出参：",rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }

}
