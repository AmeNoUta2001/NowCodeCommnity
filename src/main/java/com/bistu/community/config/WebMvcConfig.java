package com.bistu.community.config;

import com.bistu.community.controller.interceptor.AlphaInterceptor;
import com.bistu.community.controller.interceptor.LoginRequiredInterceptor;
import com.bistu.community.controller.interceptor.LoginTicketInterceptor;
import com.bistu.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;
    @Override
    // 上面注册接口就是为了实现这个方法
    public void addInterceptors(InterceptorRegistry registry) {
        // 如果只用这一个方法，那么就会拦截一切请求
        registry.addInterceptor(alphaInterceptor)
                // 排除某些路径的请求
                // 在此处是使用通配符方式让静态资源不被拦截
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                // 希望一部分请求被拦截,除此之外的请求不被拦截
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
