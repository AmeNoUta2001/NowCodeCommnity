package com.bistu.community.controller.interceptor;

import com.bistu.community.entity.LoginTicket;
import com.bistu.community.entity.User;
import com.bistu.community.service.UserService;
import com.bistu.community.util.CookieUtil;
import com.bistu.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 因为cookie是从request传回的，所以我们可以通过request得到cookie
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        // 确认登陆后需要查询用户数据
        if (ticket != null) {
//            查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 验证凭证有效性
            //1、loginTicket不为空 2、账户未激活 3、超时时间在当前时间之后
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    // 在请求结束时清理用户数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
