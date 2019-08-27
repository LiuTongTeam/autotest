package io.cex.test.autotest.interfacecase.boss;

import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class BossBaseCase {
    //接口url，默认使用测试环境url
    public static String boss_ip = "https://cex-boss-test.up.top";

    //boss接口url
    public static final String bossLoginUrl = "/boss/account/login";
    public static final String firstTrial = "/boss/cex/audit/firstTrial";
    public static final String reviewing = "/boss/cex/audit/reviewing";
    /**
     * @desc BOSS login工具,返回token
     * @param  accountId 用户名
     * @param pwd 密码
     **/
    public static String userBossLogin(String accountId, String pwd){
        String bossToken = null;
        JSONObject object = new JSONObject();
        object.put("accountId", accountId);
        object.put("password", pwd);
        Allure.addAttachment("BOSS登陆入参：",object.toJSONString());
        try {
            String response = OkHttpClientManager.postAsString(boss_ip+bossLoginUrl,object.toJSONString());
            try {
                Allure.addAttachment("BOSS登陆出参：",response);
                bossToken = JsonFileUtil.jsonToMap(JSONObject.parseObject(response),new HashMap<String, Object>()).get("token").toString();
            }catch (Exception e){
                e.printStackTrace();
                log.error("-----------BOSS login ERROR, response is "+ response);
            }
        }catch (IOException e){
            e.printStackTrace();
            log.error("--------------Server connect failed");
        }        return bossToken;
    }
}
