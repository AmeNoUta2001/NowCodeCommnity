package com.bistu.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    /*
        MD5加密 用于将明文的密码加密
        例：hello -> acb123456
        为了增加密码的安全性，我们在密码的后面加上一个随机字符串（数据表中的salt），防止黑客撞库
        hello + 1e2gfqf3(随机字符串) -> acb23413tnac
     */
    public static String md5(String key){
        //判空
        if(StringUtils.isBlank(key)){
            return null;
        }
        //加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}
