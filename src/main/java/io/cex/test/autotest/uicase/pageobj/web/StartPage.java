package io.cex.test.autotest.uicase.pageobj.web;
import java.io.IOException;
import java.io.InputStream;
import io.cex.test.framework.ui.BaseAction;
import io.cex.test.framework.ui.Locator;
import io.cex.test.framework.ui.GeneratePageObjectCode;
//APP启动首页_对象库类
public class StartPage extends BaseAction {
//用于工程内运行查找对象库文件路径
private String path="./src/main/java/io/cex/test/autotest/uicase/libs/cex_web_lib.xml";
 public   StartPage() {
//工程内读取对象库文件
	setXmlObjectPath(path);
getLocatorMap();
}
/***
* 登录
* @return
* @throws IOException
*/
public  Locator cex_login() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("cex_login");
   return locator;
 }

/***
* 注册
* @return
* @throws IOException
*/
public  Locator cex_register() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("cex_register");
   return locator;
 }
}