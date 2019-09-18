package io.cex.test.autotest.interfacecase.c2c.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.BaseCase.c2cip;
import static io.cex.test.autotest.interfacecase.c2c.tool.C2CConfig.assetsDetailUrl;
@Slf4j
public class C2CCommonOption {
    /**
    * @desc 查询用户资产信息
    * @param  assetType 查询资产类型，如amount为总资产，freezeAmount为冻结资产
     * @param token 用户登陆token
    **/
    public static String queryC2CAsset(String assetType, String token){
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",token);
        JSONObject jsonbody = new JSONObject();
        try {
            Response response = OkHttpClientManager.post(c2cip+assetsDetailUrl,jsonbody.toJSONString(),"application/json",header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("查询用户资产出参：",rspjson.toJSONString());
                if (rspjson.get("respCode").equals("000000")){
                    String amount = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get(assetType).toString();
                    return amount;
                }else {
                    log.error("----------------Query failed, trace id is:"+rspjson.get("traceId")+"\n");
                    return null;
                }
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
                return null;
            }


        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }
}
