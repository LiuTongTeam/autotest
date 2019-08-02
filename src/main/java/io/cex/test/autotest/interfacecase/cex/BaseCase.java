package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class BaseCase {
    //测试环境信息
    public static final String ip = "http://139.9.55.125/apis";
    public static final String mysql = "jdbc:mysql://172.29.19.71:3306/cex?useUnicode=true&characterEncoding=UTF8&user=root&password=48rm@hd2o3EX";
    public static final HashMap header = new HashMap<String, String>();
    public static final String lang = "en-US";
    public static JSONObject jsonbody = new JSONObject();
    public static final String randomPhone = RandomUtil.getRandomPhoneNum();
    public static final String loginUrl = "/user/login";
    public static final String registerUrl = "/user/register";
    /**
    * @desc 数据初始化
    **/
    public static void dataInit(){
        header.put("DEVICEID", "A5A6F0c6B90638A2F-e195d43830A5e9979906e5A0A8A-9330A0B3ADBBB9d93-AFF5dBcF9-A4c749-AB10-4EB49EABF9E7-85315174-34961239");
        header.put("DEVICESOURCE","native");
        header.put("Lang",lang);
        jsonbody.put("lang",lang);
    }

    /**
     * @desc login工具,返回token
     * @param user 用户名
     * @param pwd 密码
     * @param area 区号
     **/
    public static String userLogin(String user,String pwd,String area){
        dataInit();
        JSONObject object = new JSONObject();
        object.put("loginPwd",pwd);
        object.put("identifier",user);
        object.put("mobileArea",area);
        object.put("verifyCode","111111");
        jsonbody.put("data",object);
        System.out.printf(jsonbody.toJSONString()+"\n");
        try {
            Response response = OkHttpClientManager.post(ip+loginUrl, jsonbody.toJSONString(),
                    "application/json", header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                if (rspjson.get("code").equals("000000")){
                    return JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("token").toString();
                }else {
                    log.error("----------------login failed, trace id is:"+rspjson.get("traceID")+"\n");
                    return null;
                }
            }else {
                log.error("----------------Server connect failed"+response.body());
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @desc 注册工具
     * @param user 用户名
     * @param pwd 密码
     * @param area 区号
     * @param inviteCode 邀请码
     **/
    public static void register(String user, String pwd, String area, String inviteCode){
        dataInit();
        JSONObject object = new JSONObject();
        object.put("loginPwd",pwd);
        object.put("identifier",user);
        object.put("invitationCode",inviteCode);
        object.put("mobileArea",area);
        object.put("verifyCode","123456");
        jsonbody.put("data",object);
        System.out.printf(jsonbody.toJSONString()+"\n");
        try {
            Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                    "application/json", header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                if (rspjson.get("code").equals("000000")){
                    log.info("-------------Regist success");
                }else {
                    log.error("----------------login failed, trace id is:"+rspjson.get("traceID")+"\n");
            }
            }else {
                log.error("----------------Server connect failed"+response.body());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
