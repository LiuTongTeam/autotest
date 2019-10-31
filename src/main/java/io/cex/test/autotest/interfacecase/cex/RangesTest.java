package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;


import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;


@Feature("Ranges获取K线支持的数据时间维度")
public class RangesTest extends BaseCase {
    //获取K线支持的数据时间维度，返回000000
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Ranges正常用例")
    public void testRanges() throws IOException {
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang", lang);
        Response response = OkHttpClientManager.post(ip + rangesUrl, jsonbody.toJSONString(),
                "application/json", BaseCase.dataInit());
        JSONObject rspjson = resultDeal(response);
        Allure.addAttachment("入参：", jsonbody.toJSONString());
        Allure.addAttachment("出参：", rspjson.toJSONString());
        AssertTool.isContainsExpect("000000", rspjson.get("code").toString());
    }
}