package com.bistu.community.controller.interceptor;

import com.bistu.community.annotation.LoginRequired;
import com.bistu.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 判断异常情况 （需要登录但是用户未登录）
            if (loginRequired != null && hostHolder.getUser() == null) {
                // 重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
