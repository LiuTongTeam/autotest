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
import java.util.HashMap;

/**
 * @author shenqingyan
 * @create 2019/8/8 11:25
 * @desc 主流程测试类
 **/
@Slf4j
public class CoreProcessTest extends BaseCase{
    private String token = null;
    private String certificate_no = null;
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
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------register response is:"+rspjson.toJSONString());
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }
    @Test(dependsOnMethods = "testRegister",description = "登陆")
    public void testLogin(){

        token = BaseCase.userCexLogin(randomPhone,pwd,area);
        AssertTool.assertNotEquals(null,token);
        log.info("------------cex token:"+token);
    }


    @Test(dependsOnMethods = "testLogin",description = "身份认证")
    public void testIdentity() throws IOException{
        //认证姓名随机字符串
        String userName = RandomUtil.generateString(10);
        JSONObject object = new JSONObject();
        object.put("countryId",countryId);
        //图片ID由上传文件接口返回
        object.put("backId",BaseCase.uploadFile(fileUrl,token,"1.jpeg"));
        object.put("frontId",BaseCase.uploadFile(fileUrl,token,"2.jpg"));
        object.put("userName", userName);
        object.put("certificateNo",RandomUtil.generateLong(18));
        object.put("personId",BaseCase.uploadFile(fileUrl,token,"3.png"));
        object.put("certificateType",certificateType);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        Response response = OkHttpClientManager.post(ip+identityUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------Identity response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
        //从数据库中获取身份认证ID
        String sql = String.format("SELECT certificate_no FROM member_certification_record WHERE first_name = '%s';",userName);
        DataBaseManager dataBaseManager = new DataBaseManager();
        certificate_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql,mysql).getString(0)).getString("certificate_no");
        log.info("------certificate_no is:"+certificate_no+"\n");
    }

    @Test(dependsOnMethods = "testIdentity", description = "身份认证初审通过")
    public void testFirstTrial() throws IOException{
        //boss登陆token放入header
        HashMap header = dataInit();
        header.put("Boss-Token",BaseCase.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装初审接口入参
        JSONObject object = new JSONObject();
        object.put("auditStatus","1");
        object.put("auditType","USER_CERT_AUTH");
        object.put("bid",certificate_no);
        //调用初审接口
        Response response = OkHttpClientManager.post(boss_ip+firstTrial, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------Identity first trial response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Test(dependsOnMethods = "testFirstTrial", description = "身份认证复审通过")
    public void testReviewing() throws IOException{
        HashMap header = dataInit();
        header.put("Boss-Token",BaseCase.userBossLogin(bossUserName,bossLoginPwd));
        log.info("-----boss token is :"+header.get("Boss-Token").toString());
        //组装复审接口入参
        JSONObject object = new JSONObject();
        object.put("auditType","USER_CERT_AUTH");
        object.put("auditStatus","1");
        object.put("bid",certificate_no);
        //调用复审接口
        Response response = OkHttpClientManager.post(boss_ip+reviewing, object.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------Identity reviewing response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("respCode").toString());
    }

    @Test(dependsOnMethods = "testReviewing", description = "设置资金密码")
    public void testSecurityPwd() throws IOException{
        JSONObject object = new JSONObject();
        object.put("securityPwd",securityPwd);
        object.put("verifyCode","111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        Response response = OkHttpClientManager.post(ip+securityPwdUrl, jsonbody.toJSONString(),
                "application/json", header);
        JSONObject rspjson = JSON.parseObject(response.body().string());
        log.info("-------------SecurityPwd response is:"+rspjson);
        AssertTool.isContainsExpect("000000",rspjson.get("code").toString());
    }

}
