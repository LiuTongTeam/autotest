package io.cex.test.autotest.uicase.utils;

import io.cex.test.framework.ui.GeneratePageObjectCode;

/**
 * @Classname GeneratePageObj
 * @Description TODO
 * @Date 2019/12/17  14:31
 * @Created by shenqingyan
 */
public class GeneratePageObj {
    public static void main(String[] args){
        try {
            GeneratePageObjectCode.generateCode("./src/main/java/io/cex/test/autotest/uicase/libs/cex_web_lib.xml","./src/main/java/io/cex/test/autotest/uicase/pageobj/web/");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
