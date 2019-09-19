package io.cex.test.autotest.interfacecase.c2c.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.BaseCase.c2cip;
import static io.cex.test.autotest.interfacecase.BaseCase.c2cmysql;
import static io.cex.test.autotest.interfacecase.c2c.tool.C2CConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;


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

    /**
    * @desc  查询广告具体信息，如freezeAmount：冻结数量，remainAmount：剩余数量，amount：总量
    * @param
    **/
    public static String selectOne(String token,String tradeId,String type){
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",token);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeId);
        Allure.addAttachment("查询广告详情入参：",jsonbody.toJSONString());
        try {
            Response response = OkHttpClientManager.post(c2cip+selectOneUrl,jsonbody.toJSONString(),"application/json",header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("查询广告详情出参：",rspjson.toJSONString());
                if (rspjson.get("respCode").equals("000000")){
                    String amount = JsonFileUtil.jsonToMap(rspjson,new HashMap<>()).get(type).toString();
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

    /**
    * @desc 取消单个广告
    * @param user 用户手机号或邮箱地址，默认使用统一密码
     * @param tradeId 广告id
    **/
    public static void tradeCancel(String user,String tradeId){
        String token = CexCommonOption.userCexLogin(user,pwd,area);
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", token);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("tradeId",tradeId);
        jsonbody.put("securityPwd","Lxm499125");
        try {
            Response response = OkHttpClientManager.post(c2cip+tradeCancelUrl,jsonbody.toJSONString(),"application/json",header);
            JSONObject rspjson = JSON.parseObject(response.body().string());
            if (rspjson.get("respCode").equals("000000")){
                Allure.addAttachment("取消广告tradeId：",tradeId);
            }
            log.info("-------------cancel user:"+user+"----result:"+rspjson.toJSONString());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
    * @desc 取消测试账户创建的所有广告，清除脏数据
    **/
    public static void tradeCancelAll(){
        JSONArray userArry = new DataBaseManager().executeSingleQuery("SELECT mobile from `user` WHERE nick_name like 'merchant%' and user_id in (SELECT user_id from user_role WHERE role = 'ROLE_MERCHANT');\n",c2cmysql);
        JSONArray tradeIdArry = new DataBaseManager().executeSingleQuery("SELECT trade_id from trade WHERE `status` = 'NORMAL' and `remark` = 'autotest';",c2cmysql);
        if (tradeIdArry.size()!=0){
            for (int i = 0;i<tradeIdArry.size(); i++){
                String tradeId = JSON.parseObject(tradeIdArry.getString(i)).getString("trade_id");
                for(int j = 0;j< userArry.size();j++){
                    String user = JSON.parseObject(userArry.getString(j)).getString("mobile");
                    try {
                        tradeCancel(user,tradeId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
