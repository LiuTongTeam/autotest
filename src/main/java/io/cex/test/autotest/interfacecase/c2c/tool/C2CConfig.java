package io.cex.test.autotest.interfacecase.c2c.tool;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqingyan
 * @create 2019/8/28 11:49
 * @desc c2c测试基类
 **/
@Slf4j
public class C2CConfig {
    public static final String nikeNameAddUrl = "/user/nickname/add";
    public static final String pmCreateUrl = "/pm/create";
    public static final String merchantPmCreateUrl = "/admin/pm/create";
    public static final String merchantCreateBankCardUrl = "/admin/pm/createBankCard";
    public static final String transferOutUrl = "/transfer/out";
    public static final String transferInUrl = "/transfer/in";
    public static final String merchantAddUrl = "/gateway/merchant/add";
    public static final String tradeAddUrl = "/admin/trade/add";
    public static final String tradeUpdateUrl = "/admin/trade/update";
    public static final String tradeCancelUrl = "/admin/trade/cancel";
    public static final String submitBuyOrderUrl = "/order/submitBuyOrder";
    public static final String userCancelUrl = "/order/userCancel";
    public static final String merchantCancelUrl = "/admin/order/merchantCancel";
    public static final String assetsDetailUrl = "/assets/detail";
    public static final String userConfirmUrl = "/order/userConfirm";
    public static final String userConfirmPayUrl = "/order/userConfirmPay";
    public static final String merchantConfirmUrl = "/admin/order/merchantConfirm";
    public static final String merchantConfirmPayUrl = "/admin/order/merchantConfirmPay";
    public static final String selectOneUrl = "/admin/trade/selectOne";
    public static final String userUploadImageUrl = "/pm/uploadImage";
    public static final String merchantUploadImageUrl = "/admin/pm/uploadImage";
    public static final String merchantGetImageUrl = "/admin/pm/getImageUrl";
    public static final String userGetImageUrl = "/pm/getImageUrl";

}
