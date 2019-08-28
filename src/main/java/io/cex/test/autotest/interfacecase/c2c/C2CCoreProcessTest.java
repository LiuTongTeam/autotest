package io.cex.test.autotest.interfacecase.c2c;


import io.cex.test.autotest.interfacecase.cex.BaseCase;
import io.cex.test.framework.assertutil.AssertTool;
import io.cex.test.framework.common.RandomUtil;
import io.cex.test.framework.testng.Retry;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static io.cex.test.autotest.interfacecase.boss.BossBaseCase.cexReviewing;
import static io.cex.test.autotest.interfacecase.boss.BossBaseCase.firstTrial;
import static io.cex.test.autotest.interfacecase.cex.BaseCase.*;

/**
 * @author shenqingyan
 * @create 2019/8/28 14:18
 * @desc c2c主流程测试
 **/
@Epic("C2C主流程")
@Slf4j
public class C2CCoreProcessTest extends C2CBaseCase {
    private String merchanttoken = null;
    private String usertoken = null;
    //随机手机号，用于注册
    private String randomPhoneMerchant = null;
    private String randomPhoneUser = null;

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(retryAnalyzer = Retry.class,description = "两个账户注册并登陆")
    public void registAndLoginTest(){
        //生成两个随机手机号
        randomPhoneMerchant = RandomUtil.getRandomPhoneNum();
        randomPhoneUser = RandomUtil.getRandomPhoneNum();
        //注册
        BaseCase.register(randomPhoneMerchant,pwd,area,"000000");
        BaseCase.register(randomPhoneUser, pwd, area,"000000");
        //登陆
        merchanttoken = BaseCase.userCexLogin(randomPhoneMerchant, pwd, area);
        usertoken = BaseCase.userCexLogin(randomPhoneUser, pwd, area);
        AssertTool.assertNotEquals(merchanttoken,null);
        AssertTool.assertNotEquals(usertoken,null);
    }

    @Feature("数据准备")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dependsOnMethods = "registAndLoginTest",description = "身份认证")
    public void identityTest(){
        //提交认证
        String certifercationMerchant = cexIdentity(merchanttoken);
        String certifercationUser = cexIdentity(usertoken);
        //初审
        firstTrial(certifercationMerchant);
        firstTrial(certifercationUser);
        //复审
        cexReviewing(certifercationMerchant);
        cexReviewing(certifercationUser);
    }


}
