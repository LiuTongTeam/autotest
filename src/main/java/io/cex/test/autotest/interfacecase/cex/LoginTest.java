package io.cex.test.autotest.interfacecase.cex;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginTest extends BaseCase{

    public static void main(String[] args) {
        //登陆
        String str = userCexLogin("16602819191","123qweQWE","86");
        System.out.printf("token:"+str);
    }
}
