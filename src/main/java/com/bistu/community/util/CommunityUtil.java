package com.bistu.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if (map != null) {
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    // 有的业务不一定有msg和map，所以写下面两个方法方便处理。
    public static String getJSONString(int code ,String msg) {
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code) {
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        // 测试
        HashMap<String ,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0,"ok",map));
    }

}
