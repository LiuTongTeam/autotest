package io.cex.test.autotest.interfacecase.boss.tool;

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

import static io.cex.test.autotest.interfacecase.BaseCase.*;
import static io.cex.test.autotest.interfacecase.boss.tool.BossConfig.*;

@Slf4j
public class BossCommonOption {
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
            Allure.addAttachment("BOSS登陆出参：",response);
            try {
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

    /**
     * @desc 个人认证初审
     * @param  certificateNo BOSS身份认证ID
     **/
    public static void firstTrial(String certificateNo){
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
        log.info("------boss token is :"+header.get("Boss-Token").toString());
        //组装初审接口入参
        JSONObject object = new JSONObject();
        object.put("auditStatus","1");
        object.put("auditType","USER_CERT_AUTH");
        object.put("bid",certificateNo);
        Allure.addAttachment("认证初审入参：", object.toJSONString());
        try {
            //调用初审接口
            Response response = OkHttpClientManager.post(boss_ip+firstTrial, object.toJSONString(),
                    "application/json", header);
            JSONObject rspjson = resultDeal(response);
            log.info("-------------Identity first trial response is:" + rspjson);
            Allure.addAttachment("认证初审出参：", rspjson.toJSONString());
            if (response.code()==200) {
                if (rspjson.get("respCode").equals("000000")){
                    log.info("-------------FirstTrial success"+"body:"+rspjson.toJSONString()+"\n");
                }else {
                    log.error("----------------FirstTrial failed, trace id is:"+rspjson.get("traceId")+"\n");
                }
            }else {
                String errorMsg = rspjson.getString("error");
                Allure.addAttachment("认证初审出错：", errorMsg);
                log.error("----------------Server connect failed"+errorMsg+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * @desc 个人认证初审,打回
     * @param  certificateNo BOSS身份认证ID
     **/
    public static void firstTrial(String certificateNo,String failureMsg){
        HashMap header = dataInit();
        header.put("Boss-Token", BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
        log.info("------boss token is :"+header.get("Boss-Token").toString());
        //组装初审接口入参
        JSONObject object = new JSONObject();
        object.put("auditStatus","2");
        object.put("auditType","USER_CERT_AUTH");
        object.put("bid",certificateNo);
        object.put("failureMsg",failureMsg);
        Allure.addAttachment("认证初审入参：", object.toJSONString());
        try {
            //调用初审接口
            Response response = OkHttpClientManager.post(boss_ip+firstTrial, object.toJSONString(),
                    "application/json", header);
            JSONObject rspjson = resultDeal(response);
            log.info("-------------Identity first trial response is:" + rspjson);
            Allure.addAttachment("认证初审出参：", rspjson.toJSONString());
            if (response.code()==200) {
                if (rspjson.get("respCode").equals("000000")){
                    log.info("-------------FirstTrial success"+"body:"+rspjson.toJSONString()+"\n");
                }else {
                    log.error("----------------FirstTrial failed, trace id is:"+rspjson.get("traceId")+"\n");
                }
            }else {
                String errorMsg = rspjson.getString("error");
                Allure.addAttachment("认证初审出错：", errorMsg);
                log.error("----------------Server connect failed"+errorMsg+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * @desc 个人认证复审
     * @param  certificateNo BOSS身份认证ID
     **/
    public static void cexReviewing(String certificateNo){
        HashMap header = BaseCase.dataInit();
        header.put("Boss-Token",BossCommonOption.userBossLogin(bossUserName,bossLoginPwd));
        log.info("------boss token is :"+header.get("Boss-Token").toString());
        //组装复审接口入参
        JSONObject object = new JSONObject();
        object.put("auditType","USER_CERT_AUTH");
        object.put("auditStatus","1");
        object.put("bid",certificateNo);
        Allure.addAttachment("认证复审入参：", object.toJSONString());
        try {
            //调用复审接口
            Response response = OkHttpClientManager.post(boss_ip+reviewing, object.toJSONString(),
                    "application/json", header);
            JSONObject rspjson = resultDeal(response);
            Allure.addAttachment("认证复审出参：", rspjson.toJSONString());
            if (response.code()==200) {
                log.info("-------------Identity reviewing response is:" + rspjson);
                if (rspjson.get("respCode").equals("000000")){
                    log.info("-------------Identity reviewing success"+"body:"+rspjson.toJSONString()+"\n");
                }else {
                    log.error("----------------Identity reviewing failed, trace id is:"+rspjson.get("traceId")+"\n");
                }
            }else {
                String errorMsg = rspjson.getString("error");
                Allure.addAttachment("认证复审出错：", errorMsg);
                log.error("----------------Server connect failed"+errorMsg+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
