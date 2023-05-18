package com.bistu.community.controller;

import com.bistu.community.service.AlphaService;
import com.bistu.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot I am AmeNoUta";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":"+value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>测试</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    GET请求
    //  /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1")int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10")int limit) {
        System.out.println(current);
        System.out.println(limit);

        return "some Students";
    }

    //  /students/123
    @RequestMapping(path = "/students/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    //以下函数的参数与html中表单的参数一致即可传递数据
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "阿伟");
        mav.addObject("age", "8");
        mav.setViewName("/demo/view");//此处的view指的是view.html
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","摆信科");
        model.addAttribute("age",120);
        return "/demo/view";
    }
    // 响应JSON数据（异步请求）
    // Java对象 -> JSON字符串 -> Js对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object>getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","阿伟");
        emp.put("age","8");
        emp.put("salary",10000.00);
        return emp;

    }

    //集合型JSON数据
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>>getEmps() {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "阿伟");
        emp.put("age", "8");
        emp.put("salary", 10000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "冲哥");
        emp.put("age", "22");
        emp.put("salary", 100000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "梓洋");
        emp.put("age", "25");
        emp.put("salary", 15000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "io");
        emp.put("age", "22");
        emp.put("salary", 999999999.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Troy");
        emp.put("age", "22");
        emp.put("salary", 999999999.00);
        list.add(emp);

        return list;
    }

    // Cookies示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效范围
        cookie.setPath("/community/alpha");
        // 设置cookie的生存时间
        // cookie默认是存在内存中，关掉网页就会消失，设置生存时间后会存在硬盘中，直至生存时间结束失效
        // 单位是秒
        cookie.setMaxAge(60*10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    // Session和Cookie不同 Session不需要创建 通过注解注入即可
    public String setSession(HttpSession session) {
        session.setAttribute("id" ,1);
        session.setAttribute("name","test");
        return "set session";
    }
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id" ));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // AJAX示例 因为是异步请求，所以这部分方法向浏览器返回的是字符串
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
