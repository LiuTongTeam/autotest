package io.cex.test.autotest.interfacecase.boss.tool;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BossConfig {

    public static String bossToken;
    //boss接口url
    public static final String bossLoginUrl = "/boss/account/login";
    public static final String firstTrial = "/boss/cex/userAuth/firstTrial";
    public static final String reviewing = "/boss/cex/userAuth/reviewing";
    public static final String bossUserName = "autotest";
    public static final String bossLoginPwd = "123456";
    public static final String authfirstTrial = "/boss/cex/asset/withdraw/auth/firstTrial";
    public static final String authreviewing = "/boss/cex/asset/withdraw/auth/reviewing";


}
