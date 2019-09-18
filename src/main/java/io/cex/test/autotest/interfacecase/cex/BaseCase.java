package io.cex.test.autotest.interfacecase.cex;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cex.test.autotest.interfacecase.boss.BossBaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.FileUtil;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.common.StringUtil;
import io.cex.test.framework.dbutil.DataBaseManager;
import io.cex.test.framework.httputil.OkHttpClientManager;
import io.cex.test.framework.jsonutil.JsonFileUtil;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author shenqingyan
 * @create 2019/8/8 11:50
 * @desc cex测试基类
 **/
@Slf4j
public class BaseCase {
    //测试环境信息
    public static String ip = "http://139.9.55.125/apis";
    //Boss url，默认使用测试环境url
    public static String boss_ip = "https://cex-boss-test.up.top";
    //c2c测试环境信息
    public static String c2cip = "http://139.9.55.125/biz_center";
    //c2c数据库连接信息
    public static String c2cmysql = "jdbc:mysql://172.29.19.71:3306/c2c?useUnicode=true&characterEncoding=UTF8&user=kofo&password=48rm@hd2o3EX";
    //cex数据库连接信息
    public static String cexmysql = "jdbc:mysql://172.29.19.71:3306/cex?useUnicode=true&characterEncoding=UTF8&user=root&password=48rm@hd2o3EX";

    //接口参数
    public static String presetUser = "24244855@qq.com";
    //加密后的密码，对应明文：Aa123456
    public static String presetUserPwd = "afdd0b4ad2ec172c586e2150770fbf9e";
    //加密后的密码，对应明文：Lxm499125
    public static String presetUsersecurityPwd = "f3d3d3667220886d7a1a3f1eb9335d91";
    public static String presetToken = null;
    public static final String pwd = "afdd0b4ad2ec172c586e2150770fbf9e";
    public static final String securityPwd = "f3d3d3667220886d7a1a3f1eb9335d91";
    public static final String depositCurrency = "IDA";
    public static final String productCoin = "KOFO";
    public static final String currencyCoin = "USDT";
    public static final String depositAmount = "20";
    public static final String area = "86";
    public static final String lang = "en-US";
    public static final String countryId = "40";
    public static final String certificateType = "0";
    public static final String DEVICESOURCE = "native";
    public static final String DEVICEID = "A5A6F0c6B90638A2F-e195d43830A5e9979906e5A0A8A-9330A0B3ADBBB9d93-AFF5dBcF9-A4c749-AB10-4EB49EABF9E7-85315174-34961239";

    //认证图片路径
    public static final String fileUrl = "http://172.29.16.161/";
    //cex接口url
    public static final String loginUrl = "/user/login";
    public static final String registerUrl = "/user/register";
    public static final String upLoadFileUrl = "/user/file/upload/file";
    public static final String identityUrl = "/user/authenticate/submit/identity";
    public static final String securityPwdUrl = "/user/password/set/securityPwd";
    public static final String CheckMobileUrl = "/user/message/check/mobile";
    public static final String CheckLoginUrl = "/user/checkLogin";
    public static final String rechargeAddrUrl = "/user/wallet/query/rechargeAddr";
    public static final String withdrawUrl = "/user/withdraw/submit/withdraw";
    public static final String orderUrl = "/user/order/create/order";
    public static final String BestPriceUrl = "/order/query/marketBestPrice";
    public static final String dealListUrl = "/order/query/dealList";
    public static final String queryAssetUrl = "/user/asset/query/asset";
    public static final String cancelOrderUrl = "/user/order/cancel/order";
    public static final String querySymbolAsset = "/user/asset/query/symbolAsset";
    public static final String orderBookUrl = "/order/query/orderBook";
    public static final String symbolQuotationUrl = "/quotation/query/symbolQuotation";
    public static final String loginoutUrl = "/user/logout";
    public static final String checkpwdTestUrl = "/user/password/check/securityPwd";
    public static final String orderListUrl = "/user/order/query/orderList";
    public static final String symbolquotationgroupUrl = "/quotation/query/symbolQuotationGroupByInTradeArea";




    /**
    * @desc 测试suite运行前获取环境配置信息
     * @param  file testNG.xml文件中获取到的parameter信息
    **/
    @BeforeSuite
    @Parameters({"file"})
    public void getProperties(@Optional("./test-application.properties")String file){
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            inputStream =BaseCase.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
            ip = properties.getProperty("cexip");
            log.info("获取到ip地址为："+ip);
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
            presetToken = userCexLogin(presetUser,presetUserPwd,area);
            log.info("------------预置账户token:"+presetToken);
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
     * @desc CEX login工具,返回token
     * @param user 用户名
     * @param pwd 密码
     * @param area 区号
     * @return token
     **/
    public static String userCexLogin(String user,String pwd,String area){
        dataInit();
        JSONObject object = new JSONObject();
        object.put("loginPwd",pwd);
        object.put("identifier",user);
        object.put("mobileArea",area);
        object.put("verifyCode","111111");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        Allure.addAttachment("CEX登陆入参：",jsonbody.toJSONString());
        try {
            Response response = OkHttpClientManager.post(ip+loginUrl, jsonbody.toJSONString(),
                    "application/json", dataInit());
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("CEX登陆出参：",rspjson.toJSONString());
                if (rspjson.get("code").equals("000000")){
                    log.info("-------------Login success"+"body:"+rspjson.toJSONString()+"\n");
                    return JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("token").toString();
                }else {
                    log.error("----------------login failed, trace id is:"+rspjson.get("traceId")+"\n");
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
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        Allure.addAttachment("注册入参：",jsonbody.toJSONString());
        try {
            Response response = OkHttpClientManager.post(ip+registerUrl, jsonbody.toJSONString(),
                    "application/json", dataInit());
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("注册出参：",rspjson.toJSONString());
                if (rspjson.get("code").equals("000000")){
                    log.info("-------------Regist success"+"body:"+rspjson.toJSONString()+"\n");
                }else {
                    log.error("----------------Regist failed, trace id is:"+rspjson.get("traceId")+"\n");
            }
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
    * @desc 上传文件
    * @param  fileUrl 文件服务器地址
     * @param fileName 图片名，带文件后缀，如test.jpeg
     * @param token 登陆token
     * @return 图片ID
    **/
    public static String uploadFile(String fileUrl, String token,String fileName){
        String id = null;
        HashMap iheader = new HashMap<String, String>();
        iheader.put("DEVICEID", DEVICEID);
        iheader.put("DEVICESOURCE",DEVICESOURCE);
        iheader.put("Lang",lang);
        iheader.put("CEXTOKEN",token);
        //从服务器获取图片资源
        File file = FileUtil.getFileFromWeb(fileUrl+fileName);
        try {
            //调用上传图片接口，获取图片ID
            Response response = OkHttpClientManager.post(ip+upLoadFileUrl,
                    "multipart/form-data; boundary=----WebKitFormBoundarylsMUpMX3lOxQKla8", iheader,file,fileName);
            if (response.code()==200) {
               // System.out.println("body:"+response.body().string()+"header"+response.headers().toString());
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("上传文件出参：",rspjson.toJSONString());
                if (rspjson.get("code").equals("000000")) {
                    log.info("-------------Upload File success" + "body:"+rspjson.toJSONString()+ "\n");
                    id = rspjson.getString("data");
                } else {
                    log.error("----------------Upload File failed, trace id is:" + rspjson.get("traceId") + "\n");
                }
            //    System.out.println("body:"+rspjson.toJSONString()+"header"+response.headers().toString());
                return id;
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
                return id;
            }

        }catch (IOException e){
            e.printStackTrace();
            return id;
        }

    }


    /**
    * @desc cex身份认证
    * @param  token 登陆cex token
     * @return BOSS身份认证ID
    **/
    public static String cexIdentity(String token){
        //认证姓名随机字符串
        String userName = RandomUtil.generateString(10);
        JSONObject object = new JSONObject();
        object.put("countryId",countryId);
        //图片ID由上传文件接口返回
        object.put("backId",BaseCase.uploadFile(fileUrl,token,"2.jpeg"));
        object.put("frontId",BaseCase.uploadFile(fileUrl,token,"3.jpg"));
        object.put("userName", "TEST"+userName);
        object.put("certificateNo",RandomUtil.generateLong(18));
        object.put("personId",BaseCase.uploadFile(fileUrl,token,"1.png"));
        object.put("certificateType",certificateType);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        String certificate_no = null;
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        try {
            Response response = OkHttpClientManager.post(ip+identityUrl, jsonbody.toJSONString(),
                "application/json", header);
            if (response.code()==200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                log.info("------------Identity response is:" + rspjson);
                Allure.addAttachment("认证入参：", jsonbody.toJSONString());
                Allure.addAttachment("认证出参：", rspjson.toJSONString());
                if (rspjson.get("code").equals("000000")) {
                    String sql = String.format("SELECT certificate_no FROM member_certification_record WHERE first_name = '%s';", "TEST"+userName);
                    DataBaseManager dataBaseManager = new DataBaseManager();
                    certificate_no = JSON.parseObject(dataBaseManager.executeSingleQuery(sql, cexmysql).getString(0)).getString("certificate_no");
                    log.info("------certificate_no is:" + certificate_no + "\n");
                    return certificate_no;
                }else {
                    log.error("----------------Cex Identity failed, trace id is:" + rspjson.get("traceId") + "\n");
                    return certificate_no;
                }
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
                return certificate_no;
            }
        }catch (IOException e){
            e.printStackTrace();
            return certificate_no;
        }
    }

    /**
    * @desc 设置资金密码
    * @param  token cex登陆token
    **/
    public static void setSecurityPwd(String token){
        JSONObject object = new JSONObject();
        object.put("securityPwd",securityPwd);
        object.put("verifyCode","111112");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        try {
            Response response = OkHttpClientManager.post(ip+securityPwdUrl, jsonbody.toJSONString(),
                    "application/json", header);
            if (response.code()==200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("设置资金密码入参：", jsonbody.toJSONString());
                Allure.addAttachment("设置资金密码出参：", rspjson.toJSONString());
                log.info("-------------SecurityPwd response is:" + rspjson);
                if (rspjson.get("code").equals("000000")) {
                    log.info("-------------SecurityPwd Set success" + "body:"+rspjson.toJSONString()+ "\n");
                }else {
                    log.error("----------------SecurityPwd Set failed, trace id is:" + rspjson.get("traceId") + "\n");
                }
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
    * @desc 获取用户对应currency的充币地址
    * @param  user 用户名
     * @param pwd 密码
     * @param currency 币种
     * @return 地址
    **/
    public static String getAddress(String user ,String pwd,String currency){
        String address = null;
        JSONObject object = new JSONObject();
        object.put("currency",currency);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("data",object);
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        String token = null;
        if (user.equals(presetUser)){
            token = presetToken;
        }else {
            token = userCexLogin(user,pwd,area);
        }
        header.put("CEXTOKEN",token);
        Allure.addAttachment("获取充币地址入参：",jsonbody.toJSONString());
        try {
            Response response = OkHttpClientManager.post(ip+rechargeAddrUrl,jsonbody.toJSONString(),"application/json",header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("获取充币地址出参：",rspjson.toJSONString());
                if (rspjson.get("code").equals("000000")){
                    log.info("------------Get address success"+"body:"+rspjson.toJSONString()+"\n");
                    return JsonFileUtil.jsonToMap(rspjson,new HashMap<String, Object>()).get("rechargeAddress").toString();
                }else {
                    log.error("----------------Get address failed, trace id is:"+rspjson.get("traceId")+"\n");
                    return null;
                }
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");
                return null;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return address;
    }

    /**
    * @desc 提币
    * @param securityPwd 资金密码
     * @param walletAddress 提币地址
     * @param amount 提币数量
     * @param currency 币种
     * @param token CEX token
     * @return 响应码
    **/
    public static String withDraw(String securityPwd, String walletAddress,String token, String amount,String currency){
        JSONObject object = new JSONObject();
        object.put("amount",amount);
        object.put("currency",currency);
        object.put("verifyCode1","111111");
        object.put("walletAddress",walletAddress);
        object.put("securityPwd",securityPwd);
        object.put("verifyCode2","");
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        HashMap header = dataInit();
        Allure.addAttachment("提币入参：",jsonbody.toJSONString());
        header.put("CEXTOKEN",token);

        try {
            Response response = OkHttpClientManager.post(ip+withdrawUrl,jsonbody.toJSONString(),"application/json",header);
            if (response.code()==200){
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("提币出参：",rspjson.toJSONString());
                log.info("-------------Withdraw response is:"+rspjson);
                return rspjson.get("code").toString();
            }else {
                log.error("----------------Server connect failed"+response.body()+"\n");

            }

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
    * @desc 下单
    * @param
    **/
    public static Map order(String symbol, String action, String orderType, String limitPrice, String quantity, String amount, String token){
        JSONObject object = new JSONObject();
        object.put("symbol",symbol);
        object.put("action",action);
        object.put("orderType",orderType);
        object.put("limitPrice",limitPrice);
        object.put("quantity",quantity);
        object.put("amount", amount);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment("下单入参：",jsonbody.toJSONString());
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        HashMap result = new HashMap();
        try {
            Response response = OkHttpClientManager.post(ip + orderUrl, jsonbody.toJSONString(), "application/json", header);
            if (response.code() == 200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("下单出参：",rspjson.toJSONString());
                log.info("-------------Order response is:" + rspjson);
                result.put("code", rspjson.get("code").toString());
                if (result.get("code").equals("000000")) {
                    log.info("------------Order success");
                    result.put("orderNo", JsonFileUtil.jsonToMap(rspjson, new HashMap<String, Object>()).get("orderNo").toString());
                    return result;
                } else {
                    log.error("----------------Server connect failed" + response.body() + "\n");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            return result;
        }
        return result;

    }
    /**
    * @desc 撤单
    * @param token cex 登陆token
     * @param orderNo 订单号
    **/
    public static HashMap cancelOrder(String token,String orderNo){
        JSONObject object = new JSONObject();
        object.put("orderNo",orderNo);
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        jsonbody.put("data",object);
        Allure.addAttachment("撤单入参：",jsonbody.toJSONString());
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        HashMap result = new HashMap();
        try {
            Response response = OkHttpClientManager.post(ip + cancelOrderUrl, jsonbody.toJSONString(), "application/json", header);
            if (response.code() == 200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("撤单出参：", rspjson.toJSONString());
                result.put("code", rspjson.get("code").toString());
                log.info(("撤单出参："+ rspjson.toJSONString()));
                return result;
            }else {
                log.error("----------------Server connect failed" + response.body() + "\n");
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
    * @desc 批量撤销交易对未成交订单
    * @param symbol 需要撤单的交易对
    **/
    public static void batchCancelOrder(String symbol){
        //status-1 委托单状态正常，即未撤单、state != 3不为全部成交
        String sqlOrder = String.format("SELECT order_no,user_no from order_info WHERE `status` = 0 and state != 3 and symbol = '%s';",symbol);
        DataBaseManager dataBaseManager = new DataBaseManager();
        //查询未成交的下单
        JSONArray array = dataBaseManager.executeSingleQuery(sqlOrder,cexmysql);
        log.info("order data is "+array.toJSONString());
        if (array.size()>0){
            for (int i = 0 ; i< array.size(); i++){
                //遍历未成交市价单进行撤销
                JSONObject order = JSON.parseObject(array.get(i).toString());
                String orderNo = order.getString("order_no");
                String userNo = order.getString("user_no");
                //查询该市价单用户信息
                String sqlQueryMobile = String.format("SELECT mobile_num from member_user WHERE user_no = '%s';",userNo);
                log.info("-------sqlQueryMobile is"+sqlQueryMobile);
                JSONArray mobileArry = dataBaseManager.executeSingleQuery(sqlQueryMobile,cexmysql);
                JSONObject mobile = JSON.parseObject(mobileArry.getString(0));
                //使用下单用户登陆，获取登陆token
                String cancelOrderToken = null;
                try {
                    //使用测试密码登陆
                    cancelOrderToken = userCexLogin(mobile.getString("mobile_num"),pwd,area);
                    cancelOrder(cancelOrderToken,orderNo);
                }catch (Exception e){
                    e.printStackTrace();
                    //使用预置账户的密码登陆
                    cancelOrderToken = presetToken;
                    cancelOrder(cancelOrderToken,orderNo);
                }

            }
        }
    }

    /**
    * @desc 获取资产信息
    * @param token 登陆cex token
     * @param currency 币种信息
     * @return HashMap中存放币种的totalAmount、availableAmount、frozenAmount
    **/
    public static HashMap queryAsset(String token,String currency){
        HashMap result = new HashMap();
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("lang",lang);
        HashMap header = dataInit();
        header.put("CEXTOKEN",token);
        try {
            Response response = OkHttpClientManager.post(ip + queryAssetUrl,jsonbody.toJSONString(),"application/json", header);
            if (response.code() == 200) {
                JSONObject rspjson = JSON.parseObject(response.body().string());
                Allure.addAttachment("获取资产信息出参：",rspjson.toJSONString());
                if (rspjson.get("code").toString().equals("000000")){
                    JSONObject data = rspjson.getJSONObject("data");
                    JSONArray array = JSON.parseArray(data.get("currencyList").toString());
                    if (array.size() !=0){
                        for (int i = 0;i<array.size();i++){
                            JSONObject currencyContent = JSON.parseObject(array.getString(i));
                            if (currencyContent.get("currencyCode").equals(currency)){
                                result.put("totalAmount",currencyContent.get("totalAmount"));
                                result.put("availableAmount",currencyContent.get("availableAmount"));
                                result.put("frozenAmount",currencyContent.get("frozenAmount"));
                            }
                        }
                        log.info("----------------QueryAsset success,response is:"+rspjson+"\n");
                        return result;
                    }else {
                        log.info("----------------QueryAsset success, no currency,response is "+rspjson+"\n");
                        return null;
                    }
                }else {
                    log.error("----------------QueryAsset failed, trace id is:"+rspjson.get("traceId")+"\n");
                    return null;
                }
            }else {
                log.error("----------------Server connect failed" + response.body() + "\n");
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    /**
    * @desc 根据费率计算实际成交后的可用数量
    * @param symbol 交易对
     * @param beforAvailableAmount 交易前可用数量
     * @param getAmount 成交量
    **/
    public static String countAvailableAmount(String symbol,String beforAvailableAmount,String getAmount){
        String queryRateSql = String.format("select taker_fee_rate from biz_fee_group_trade_config where symbol='%s' and group_name='默认现货交易手续费组';",symbol);
        DataBaseManager dataBaseManager = new DataBaseManager();
        JSONArray mobileArry = dataBaseManager.executeSingleQuery(queryRateSql,cexmysql);
        String rate = JSON.parseObject(mobileArry.getString(0)).getString("taker_fee_rate");
        Allure.addAttachment("交易手续费费率为：", StringUtil.stripTrailingZeros(rate));
        BigDecimal rateNum = new BigDecimal(rate).stripTrailingZeros();
        BigDecimal beforNum = new BigDecimal(beforAvailableAmount).stripTrailingZeros();
        BigDecimal getNum = new BigDecimal(getAmount).stripTrailingZeros();
        //result=beforNum+(getNum-getNum*rateNum)
        BigDecimal result = beforNum.add(getNum.subtract(getNum.multiply(rateNum)));
        return result.toString();
    }

}




