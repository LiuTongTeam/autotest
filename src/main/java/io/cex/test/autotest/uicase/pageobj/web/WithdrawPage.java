package io.cex.test.autotest.uicase.pageobj.web;
import java.io.IOException;
import java.io.InputStream;
import io.cex.test.framework.ui.BaseAction;
import io.cex.test.framework.ui.Locator;
import io.cex.test.framework.ui.GeneratePageObjectCode;
//提币页_对象库类
public class WithdrawPage extends BaseAction {
//用于工程内运行查找对象库文件路径
private String path="./src/main/java/io/cex/test/autotest/uicase/libs/cex_web_lib.xml";
 public   WithdrawPage() {
//工程内读取对象库文件
	setXmlObjectPath(path);
getLocatorMap();
}
/***
* 资产管理
* @return
* @throws IOException
*/
public  Locator asset_manage() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("asset_manage");
   return locator;
 }

/***
* 提币
* @return
* @throws IOException
*/
public  Locator withdraw() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("withdraw");
   return locator;
 }

/***
* 选择币种
* @return
* @throws IOException
*/
public  Locator currency_select() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("currency_select");
   return locator;
 }

/***
* 选择地址
* @return
* @throws IOException
*/
public  Locator chose_addr() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("chose_addr");
   return locator;
 }

/***
* 点击地址
* @return
* @throws IOException
*/
public  Locator click_addr() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("click_addr");
   return locator;
 }

/***
* 输入数量
* @return
* @throws IOException
*/
public  Locator amount_input() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("amount_input");
   return locator;
 }

/***
* 确认提交
* @return
* @throws IOException
*/
public  Locator submit_click() throws IOException{
   setXmlObjectPath(path);
   Locator locator=getLocator("submit_click");
   return locator;
 }
}