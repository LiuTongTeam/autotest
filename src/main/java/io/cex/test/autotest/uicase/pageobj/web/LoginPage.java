package io.cex.test.autotest.uicase.pageobj.web;
import java.io.IOException;
import java.io.InputStream;
import io.cex.test.framework.ui.BaseAction;
import io.cex.test.framework.ui.Locator;
import io.cex.test.framework.ui.GeneratePageObjectCode;
//web首页_对象库类
public class LoginPage extends BaseAction {
//用于工程内运行查找对象库文件路径
private String path="./src/main/java/io/cex/test/autotest/uicase/libs/cex_web_lib.xml";
 public   LoginPage() {
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
* 邮箱登录
* @return
* @throws IOException
*/
public  Locator cex_mail_login() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("cex_mail_login");
   return locator;
 }

/***
* 邮箱输入
* @return
* @throws IOException
*/
public  Locator mail_input() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("mail_input");
   return locator;
 }

/***
* 密码输入
* @return
* @throws IOException
*/
public  Locator pwd_input() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("pwd_input");
   return locator;
 }

/***
* 点击登陆
* @return
* @throws IOException
*/
public  Locator login_button() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("login_button");
   return locator;
 }

/***
* 输入邮箱验证码
* @return
* @throws IOException
*/
public  Locator email_code_input() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("email_code_input");
   return locator;
 }

/***
* 确定
* @return
* @throws IOException
*/
public  Locator email_code_confirm_button() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("email_code_confirm_button");
   return locator;
 }
}