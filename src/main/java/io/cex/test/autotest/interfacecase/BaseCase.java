package io.cex.test.autotest.interfacecase;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.boss.tool.BossCommonOption;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import static io.cex.test.autotest.interfacecase.cex.tool.CexConfig.*;
import static io.cex.test.autotest.interfacecase.cex.tool.CexCommonOption.userCexLogin;
import static io.cex.test.autotest.interfacecase.boss.tool.BossConfig.*;

/**
 * @author shenqingyan
 * @create 2019/8/8 11:50
 * @desc cex测试基类
 **/
@Slf4j
public class BaseCase {
    //测试环境信息
    public static String ip = "http://139.9.55.125/apis";
    //测试环境信息
    public static String ip_gateway = "http://139.9.55.125";
    //Boss url，默认使用测试环境url
    public static String boss_ip = "https://cex-boss-test.up.top";
    //c2c测试环境信息
    public static String c2cip = "http://139.9.55.125/biz_center";
    //c2c数据库连接信息
    public static String c2cmysql = "jdbc:mysql://172.29.19.71:3306/c2c?useUnicode=true&characterEncoding=UTF8&user=root&password=48rm@hd2o3EX";
    //cex数据库连接信息
    public static String cexmysql = "jdbc:mysql://172.29.19.71:3306/cex?useUnicode=true&characterEncoding=UTF8&user=root&password=48rm@hd2o3EX";
    //持续下单用户使用的token(15095998872)
    public static String orderToken = "bm8zgb13gs1n9btxtfxe10e6aj3khtc1uupkvtn3cuszrn4qxp11rvcfjpu4mjf8_CEX";


    /**
    * @desc 测试suite运行前获取环境配置信息
     * @param  file testNG.xml文件中获取到的parameter信息
    **/
    @BeforeSuite
    @Parameters({"file"})
    public void getProperties(@Optional("./test-application.properties")String file){
        try {
            InputStream inputStream = null;
            Properties properties = new Properties();
            inputStream =BaseCase.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
            ip = properties.getProperty("cexip");
            log.info("获取到ip地址为："+ip);
            orderToken = properties.getProperty("orderToken");
            log.info("获取到下单用户token为："+orderToken);
            cexmysql = properties.getProperty("cexmysql");
            log.info("获取到cexmysql连接信息为："+cexmysql);
            presetUser = properties.getProperty("presetUser");
            log.info("获取到presetUser信息为："+presetUser);
            presetUserPwd = properties.getProperty("presetUserPwd");
            log.info("获取到presetUserPwd信息为："+presetUserPwd);
            presetUsersecurityPwd = properties.getProperty("presetUsersecurityPwd");
            log.info("获取到presetUsersecurityPwd信息为："+presetUsersecurityPwd);
            c2cip = properties.getProperty("c2cip");
            log.info("获取到c2cip地址为："+c2cip);
            c2cmysql = properties.getProperty("c2cmysql");
            log.info("获取到c2cmysql连接信息为："+c2cmysql);
        }catch (IOException e){
            e.printStackTrace();
            log.error("------------未获取到properties配置文件，默认使用测试环境信息");
        }finally {
            presetToken = userCexLogin(presetUser,presetUserPwd,"86");
            log.info("------------预置账户token:"+presetToken);
            bossToken = BossCommonOption.userBossLogin(bossUserName,bossLoginPwd);
            log.info("------------BOSS token:"+presetToken);
        }
    }
    /**
    * @desc 数据初始化
    **/
    public static HashMap dataInit(){
        HashMap header = new HashMap<String, String>();
        header.put("DEVICEID",DEVICEID);
        header.put("DEVICESOURCE",DEVICESOURCE);
        header.put("Lang",lang);
        return header;
    }

    /**
     * @desc 处理http响应的body,返回一个json对象
     **/
    public static JSONObject resultDeal(Response response) {
        JSONObject object = new JSONObject();
        String res = null;
        try {
            res = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
            log.error("get response string error");
            object.put("error",res);
        }
        try {
            object = JSON.parseObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("translate response json error");
            object.put("error",res);
        }
        return object;
    }
}




