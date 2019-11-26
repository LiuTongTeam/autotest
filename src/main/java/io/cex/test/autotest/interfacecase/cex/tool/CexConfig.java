package io.cex.test.autotest.interfacecase.cex.tool;

public class CexConfig {
    //接口参数
    public static String presetUser = "24244855@qq.com";
    //加密后的密码，对应明文：Aa123456
    public static String presetUserPwd = "afdd0b4ad2ec172c586e2150770fbf9e";
    //加密后的密码，对应明文：Lxm499125
    public static String presetUsersecurityPwd = "f3d3d3667220886d7a1a3f1eb9335d91";
    public static String presetToken = null;
    public static String cexpassport = "1629310746677555201.123";
    //提供给regandlogin接口邀请绑定的passport
    public static final String pwd = "afdd0b4ad2ec172c586e2150770fbf9e";
    public static final String securityPwd = "f3d3d3667220886d7a1a3f1eb9335d91";
    public static final String depositCurrency = "IDA";
    public static final String productCoin = "KOFO";
    public static final String currencyCoin = "USDT";
    public static final String depositAmount = "20";
    public static final String area = "87";
    public static final String lang = "en-US";
    public static final String countryId = "40";
    public static final String certificateType = "0";
    public static final String DEVICESOURCE = "native";
    public static final String DEVICEID = "A5A6F0c6B90638A2F-e195d43830A5e9979906e5A0A8A-9330A0B3ADBBB9d93-AFF5dBcF9-A4c749-AB10-4EB49EABF9E7-85315174-34961239";

    //认证图片路径
    public static final String fileUrl = "http://172.29.16.161/";
    //cex接口url
    public static final String loginUrl = "/app/userCenter/login";
    public static final String registerUrl = "/app/userCenter/register";
    public static final String upLoadFileUrl = "/user/file/upload/file";
    public static final String identityUrl = "/user/authenticate/submit/identity";
    public static final String securityPwdUrl = "/app/userCenter/user/set/securityPwd";
    public static final String CheckMobileUrl = "/app/userCenter/message/check/mobile";
    public static final String CheckLoginUrl = "/app/userCenter/checkLogin";
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
    public static final String loginoutUrl = "/app/userCenter/user/logout";
    public static final String checkpwdTestUrl = "/app/userCenter/user/check/securityPwd";
    public static final String orderListUrl = "/user/order/query/orderList";
    public static final String symbolquotationgroupUrl = "/quotation/query/symbolQuotationGroupByInTradeArea";
    public static final String dealDetailUrl = "/user/order/query/dealDetail";
    public static final String rechargeUrl = "/user/record/query/recharge";
    public static final String userInfoUrl = "/app/userCenter/user/query/userInfo";
    public static final String userBindStatusUrl = "/app/userCenter/user/query/userBindStatus";
    public static final String withdrawInfoUrl = "/user/withdraw/query/withdrawInfo";
    public static final String qwithdrawUrl = "/user/record/query/withdraw";
    public static final String currencyChainInfoUrl = "/currency/query/currencyChainInfo";
    public static final String recommendSymbolQuotationUrl = "/quotation/query/recommendSymbolQuotation";
    public static final String symbolDetailUrl = "/kline/get/symbolDetail";
    public static final String allArticleListUrl = "/app/cms/article/query/allArticleList";
    public static final String depthDataUrl = "/quotation/query/depthData";
    public static final String symbolUrl = "/app/userCenter/user/symbol/list/symbol";
    public static final String exchangeRateUrl = "/exchangeRate/query/exchangeRate";
    public static final String batchChannelTokenUrl = "/user/query/batchChannelToken";
    public static final String userAuthInfoUrl = "/app/userCenter/user/query/userAuthInfo";
    public static final String authAuditReasonUrl = "/app/userCenter/user/query/authAuditReason";
    public static final String currencyInfoUrl = "/currency/query/currencyInfo";
    public static final String getSecurityUrl = "/app/userCenter/user/query/getSecurity";
    public static final String awithdrawAddressUrl = "/user/withdraw/add/withdrawAddress";
    public static final String qwithdrawAddressUrl = "/user/withdraw/query/withdrawAddress";
    public static final String dwithdrawAddressUrl = "/user/withdraw/delete/withdrawAddress";
    public static final String addressUrl = "/user/address/validate/address";
    public static final String loginorregisterUrl = "/app/userCenter/loginOrRegister";
    public static final String checkwithdrawaddressUrl = "/user/withdraw/check/withdrawAddress";
    public static final String querytransferUrl = "/user/record/query/transfer";
    public static final String channeltokenUrl = "/user/query/channelToken";
    public static final String onlineUserSummaryUrl = "/user/mm/query/onlineUserSummary";
    public static final String orderDetailUrl = "/user/mm/query/orderDetail";
    public static final String verifySmsUrl = "/message/send/verifySms";
    public static final String verifyEmailUrl = "/message/send/verifyEmail";
    public static final String uverifySmsUrl = "/app/userCenter/user/message/send/verifySms";
    public static final String uverifyEmailUrl = "/app/userCenter/user/message/send/verifyEmail";
    public static final String CheckemailUrl = "/app/userCenter/message/check/email";
    public static final String currencyAssetUrl = "/user/asset/query/currencyAsset";
    public static final String dealOrderListUrl = "/user/order/query/dealOrderList";
    public static final String addsymbolUrl = "/app/userCenter/user/symbol/add/symbol";
    public static final String delsymbolUrl = "/app/userCenter/user/symbol/delete/symbol";
    public static final String countryListUrl = "/cex/country/query/countryList";
    public static final String loginHistoryUrl = "/app/userCenter/user/query/loginHistory";
    public static final String lastLoginInfoUrl = "/app/userCenter/user/query/lastLoginInfo";
    public static final String configInfoUrl = "/mqtt/query/configInfo";
    public static final String captchaUrl = "/captcha/start/captcha";
    public static final String vcaptchaUrl = "/captcha/validate/captcha";
    public static final String symbolListUrl = "/symbol/query/symbolList";
    public static final String currencyListUrl = "/currency/query/currencyList";
    public static final String announcementUrl = "/app/cms/article/query/announcement";
    public static final String helpCenterUrl = "/app/cms/article/query/helpCenter";
    public static final String fullArticleUrl = "/app/cms/article/query/fullArticle";
    public static final String loginPwdWithoutLoginUrl = "/app/userCenter/reset/loginPwdWithoutLogin";
    public static final String loginPwdUrl = "/app/userCenter/user/reset/loginPwd";
    public static final String rssecurityPwdUrl = "/app/userCenter/user/reset/securityPwd";
    public static final String emailUrl = "/app/userCenter/user/bind/email";
    public static final String rbemailUrl = "/app/userCenter/user/rebind/email";
    public static final String symbolLimitOrderStatUrl = "/user/mm/query/symbolLimitOrderStat";
    public static final String preCreateInfoUrl = "/user/order/query/preCreateInfo";
    public static final String tradeSymbolInfoUrl = "/user/order/query/tradeSymbolInfo";
    public static final String depthStepUrl = "/order/query/depthStep";
    public static final String systemTimeUrl = "/kline/get/systemTime";
    public static final String quotationHistoryUrl = "/kline/get/quotationHistory";
    public static final String rangesUrl = "/kline/get/ranges";
    public static final String rebindmobileUrl = "/app/userCenter/user/mobile/rebind/mobile";
    public static final String bindmobileUrl = "/app/userCenter/user/mobile/bind/mobile";
    public static final String submitwithdrawUrl = "/user/withdraw/submit/withdraw";
    public static final String regandloginUrl = "/commission-web/v2/user/regandlogin";


}
