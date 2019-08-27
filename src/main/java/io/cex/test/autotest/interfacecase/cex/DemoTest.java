package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.*;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
//定义测试报告中用例功能描述信息，比如说某接口，展示在测试报告中
@Feature("Demo")
public class DemoTest extends BaseCase{
    /**
     * @desc 数据驱动
     * @param
     **/
    @DataProvider(parallel=true)
    public Object[][] provideTransferData(Method method){
        String path = "./src/main/resources/io/cex/test/autotest/interfacecase/cex/demo/";
        HashMap<String, String>[][] arrymap = (HashMap<String, String>[][]) JsonFileUtil.jsonFileToArry(path);
        return arrymap;
    }
    /**
    * @desc  读取json文件
    * @param
    **/
    //link提供对应相关的连接，比如某个关联bug地址
    @Link("https://www.baidu.com")
    //description中为方法描述信息，展示在测试报告中
    @Test(dataProvider = "provideTransferData", description = "DEMO读取文件用例")
    //severity为定义用例级别，可根据用例级别运行
    @Severity(SeverityLevel.CRITICAL)
    //属于feature之下的结构，报告中features中显示，可以理解为testcase，说明此用例是某个feature中的某个story下的用例
    @Story("DemoFile")
    public void testReadFileDemo(Map<?,?> param){
        JSONObject object = JSON.parseObject(param.get("body").toString());
        Allure.addAttachment("读取json中内容为：",object.toJSONString()+"\n");
        System.out.printf("读取json中内容为："+object.toJSONString()+"\n");
        AssertTool.isContainsExpect("1","1");
    }

    /**
    * @desc 读取数据库
    * @param
    **/
    //Retry为定义方法断言失败时进行重试
    @Test(retryAnalyzer =Retry.class)
    public void testDB(){
        DataBaseManager dataBaseManager = new DataBaseManager();
        //执行查询语句
        JSONArray data = dataBaseManager.executeSingleQuery("select * from member_user order by id desc limit 2;",cexmysql);
        System.out.printf("第一行数据："+data.getString(0));
        //执行删除语句
        dataBaseManager.executeSingleDelete("delete from member_user where mobile_num = '13778339517';",cexmysql);
        //执行更新语句
        dataBaseManager.executeSingleUpdate("update member_user set email = 'test@test.com' where mobile_num = '16602829196';",cexmysql);
        AssertTool.isContainsExpect("1","1");
    }


}
