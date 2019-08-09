package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author shenqingyan
 * @create 2019/8/8 11:25
 * @desc 主流程测试类
 **/
@Slf4j
public class CoreProcessTest extends BaseCase{
    String token = null;
    String certificate_no = null;
    @BeforeClass
    public void beforeClazz(){
        //初始化header数据
        dataInit();
    }

    @Test(description = "注册")
    public void testRegister() throws IOException {
        JSONObject object = new JSONObject();
        object.put("identifier",randomPhone);
        object.put("loginPwd",pwd);
        object.put("mobileArea",area);
        object.put("verifyCode","111111");
        jsonbody.put("data",object);
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------register response is:"+rspjson.toJSONString());
        AssertTool.isContainsExpect("success",rspjson.get("msg").toString());
    }
    @Test(dependsOnMethods = "testRegister",description = "登陆")
    public void testLogin(){

        token = BaseCase.userCexLogin(randomPhone,pwd,area);
        AssertTool.assertNotEquals(null,token);
        log.info("------------token:"+token);
    }


    @Test(dependsOnMethods = "testLogin",description = "身份认证")
    public void testIdentity() throws IOException{
        String userName = RandomUtil.generateString(10);
        JSONObject object = new JSONObject();
        object.put("countryId",countryId);
        object.put("backId",BaseCase.uploadFile(fileUrl,token,"1.jpeg"));
        object.put("frontId",BaseCase.uploadFile(fileUrl,token,"2.jpg"));
        object.put("userName", userName);
        object.put("certificateNo",RandomUtil.generateLong(18));
        object.put("personId",BaseCase.uploadFile(fileUrl,token,"3.png"));
        object.put("certificateType",certificateType);
        jsonbody.put("data",object);
        header.put("CEXTOKEN",token);
        Response response = OkHttpClientManager.post(ip+identityUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        System.out.printf("-------------Identity response is:"+rspjson);
        AssertTool.isContainsExpect("success",rspjson.get("msg").toString());
        //从数据库中获取身份认证ID
        String sql = String.format("SELECT certificate_no FROM member_certification_record WHERE first_name = '%s';",userName);
        DataBaseManager dataBaseManager = new DataBaseManager();
        certificate_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql,mysql).getString(0)).getString("certificate_no");
    }


}