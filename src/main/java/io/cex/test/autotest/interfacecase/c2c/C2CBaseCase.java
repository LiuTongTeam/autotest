package io.cex.test.autotest.interfacecase.c2c;

import io.cex.test.autotest.interfacecase.cex.BaseCase;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.cex.test.autotest.interfacecase.cex.BaseCase.c2cip;
import static io.cex.test.autotest.interfacecase.cex.BaseCase.c2cmysql;

/**
 * @author shenqingyan
 * @create 2019/8/28 11:49
 * @desc c2c测试基类
 **/
@Slf4j
public class C2CBaseCase {
    public static final String nikeNameAddUrl = "/user/nickname/add";
    public static final String pmCreateUrl = "/pm/create";
    public static final String transferOutUrl = "/transfer/out";
    public static final String transferInUrl = "/transfer/in";
    public static final String merchantAddUrl = "/gateway/merchant/add";
    public static final String tradeAddUrl = "/admin/trade/add";
    public static final String tradeUpdateUrl = "/admin/trade/update";
    public static final String submitBuyOrderUrl = "/order/submitBuyOrder";
    public static final String userCancelUrl = "/order/userCancel";





}
