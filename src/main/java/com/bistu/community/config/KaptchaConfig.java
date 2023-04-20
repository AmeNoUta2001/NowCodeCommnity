package com.bistu.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    // 设置kaptcha的Bean,使得这个小工具在服务启动时能够被自动装配到容器中
    @Bean
    public Producer kaptchaProducer(){
        Properties properties = new Properties();
//        通过properties设置验证码图片和文字的参数
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");


        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 将写好的配置传给Config
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }


}
