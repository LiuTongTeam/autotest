package io.cex.test.autotest.interfacecase.cex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
    @Test(dataProvider = "provideTransferData")
    public void testReadFileDemo(Map<?,?> param){
        JSONObject object = JSON.parseObject(param.get("body").toString());
        System.out.printf("读取json中内容为："+object.toJSONString()+"\n");
    }

    /**
    * @desc 读取数据库
    * @param
    **/
    @Test
    public void testDB(){
        DataBaseManager dataBaseManager = new DataBaseManager();
        //执行查询语句
        JSONArray data = dataBaseManager.executeSingleQuery("select * from member_user order by id desc limit 2;",mysql);
        System.out.printf("第一行数据："+data.getString(0));
        //执行删除语句
        dataBaseManager.executeSingleDelete("delete from member_user where mobile_num = '13778339517';",mysql);
        //执行更新语句
        dataBaseManager.executeSingleUpdate("update member_user set email = 'test@test.com' where mobile_num = '16602829196';",mysql);
    }

}
