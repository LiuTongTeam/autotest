package io.cex.test.autotest.interfacecase.c2c.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.BaseCase;
import io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption;
import io.cex.test.framework.common.FileUtil;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static io.cex.test.autotest.interfacecase.BaseCase.*;
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
                log.error("----------------Server connect failed"+response.body().string()+"\n");
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
                log.error("----------------Server connect failed"+response.body().string()+"\n");
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
        jsonbody.put("securityPwd",securityPwd);
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


    /**
     * @desc 用户上传文件
     * @param  fileUrl 文件服务器地址
     * @param fileName 图片名，带文件后缀，如test.jpeg
     * @param token 登陆token
     * @param url 上传接口url,userUploadImageUrl/merchantUploadImageUrl
     * @return 图片ID
     **/
    public static String userUploadFile(String fileUrl, String token,String fileName,String url){
        String id = null;
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", token);
        //从服务器获取图片资源
        File file = FileUtil.getFileFromWeb(fileUrl+fileName);
        try {
            //调用上传图片接口，获取图片ID
            Response response = OkHttpClientManager.post(c2cip+url,
                    "multipart/form-data; boundary=----WebKitFormBoundarylsMUpMX3lOxQKla8", header,file,fileName);
            if (response.code()==200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("上传文件出参：",rspjson.toJSONString());
                System.out.println("response"+rspjson.toJSONString());
                if (rspjson.get("respCode").equals("000000")) {
                    log.info("-------------Upload File success" + "body:"+rspjson.toJSONString()+ "\n");
                    id = rspjson.getString("data");
                } else {
                    log.error("----------------Upload File failed, trace id is:" + rspjson.get("traceId") + "\n");
                }
                return id;
            }else {
                log.error("----------------Server connect failed"+response.body().string()+"\n");
                return id;
            }

        }catch (IOException e){
            e.printStackTrace();
            return id;
        }

    }

    /**
     * @desc 用户上传文件
     * @param token 登陆token
     * @param url 上传接口url,userUploadImageUrl/merchantUploadImageUrl
     * @return 图片ID
     **/
    public static String getImageFile(String token,String url,String fileId){
        String id = null;
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN", token);
        JSONObject object = new JSONObject();
        object.put("fileId",fileId);
        try {
            Response response = OkHttpClientManager.post(c2cip+url,object.toJSONString(),"application/json",header);
            if (response.code()==200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("获取二维码图片出参：",rspjson.toJSONString());
                if (rspjson.get("respCode").equals("000000")) {
                    log.info("------------Get File success" + "body:"+rspjson.toJSONString()+ "\n");
                    id = rspjson.getString("data");
                } else {
                    log.error("---------------Get File failed, trace id is:" + rspjson.get("traceId") + "\n");
                }
                return id;
            }else {
                log.error("----------------Server connect failed"+response.body().string()+"\n");
                return id;
            }

        }catch (IOException e){
            e.printStackTrace();
            return id;
        }

    }

    /**
    * @desc 查询用户支付方式的ID
    * @param
    **/
    public static String getPayMethodId(String token,String type, String url){
        HashMap header = BaseCase.dataInit();
        header.put("CEXTOKEN",token);
        JSONObject jsonbody = new JSONObject();
        try {
            Response response = OkHttpClientManager.post(c2cip+url,jsonbody.toJSONString(),"application/json",header);
            if (response.code()==200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("查询用户支付方式的ID出参：", rspjson.toJSONString());
                if (rspjson.get("respCode").equals("000000")) {
                    JSONObject data = JSON.parseObject(rspjson.get("data").toString());
                    if (data.get("totalNum").equals(0)){
                        return null;
                    }else {
                        //遍历接口返回的支付方式，根据支付type获取对应的ID，但是同种支付方式有多个时只返回第一个ID
                        JSONArray rows = JSON.parseArray(data.get("rows").toString());
                        for (int i = 0; i<rows.size();i++){
                            JSONObject object = JSON.parseObject(rows.getString(i));
                            if (object.get("type").toString().equals(type)){
                                return object.get("id").toString();
                            }
                        }
                        return null;
                    }
                }else {
                    log.error("---------------Query failed, trace id is:" + rspjson.get("traceId") + "\n");
                }
            }
            log.error("----------------Server connect failed"+response.body().string()+"\n");
            return null;
        }catch (IOException e){
            e.printStackTrace();
            return  null;
        }
    }


}
