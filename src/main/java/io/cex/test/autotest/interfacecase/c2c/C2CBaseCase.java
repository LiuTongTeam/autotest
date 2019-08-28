package io.cex.test.autotest.interfacecase.c2c;

import io.cex.test.autotest.interfacecase.cex.BaseCase;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author shenqingyan
 * @create 2019/8/28 11:49
 * @desc c2c测试基类
 **/
@Slf4j
public class C2CBaseCase {
    //c2c测试环境信息
    public static String c2cip = "http://c2c.uat.192.168.50.146.xip.io:31000";
    //c2c数据库连接信息
    public static String c2cmysql = "jdbc:mysql://192.168.50.150:3306/c2c?useUnicode=true&characterEncoding=UTF8&user=kofo&password=48rm@hd2o3EX";

    //接口参数
    public static String presetUser = "24244855@qq.com";
    public static String presetUserPwd = "afdd0b4ad2ec172c586e2150770fbf9e";
    public static String presetUsersecurityPwd = "f3d3d3667220886d7a1a3f1eb9335d91";

    /**
     * @desc 测试suite运行前获取环境配置信息
     * @param  file testNG.xml文件中获取到的parameter信息
     **/
    @BeforeSuite
    @Parameters({"file"})
    public void getProperties(@Optional("test-application.properties")String file){
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            inputStream = BaseCase.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
            c2cip = properties.getProperty("c2cip");
            log.info("获取到c2cip地址为："+c2cip);
            c2cmysql = properties.getProperty("c2cmysql");
            log.info("获取到c2cmysql连接信息为："+c2cmysql);
        }catch (IOException e){
            e.printStackTrace();
            log.error("------------未获取到properties配置文件，默认使用测试环境信息");
        }
    }



}
