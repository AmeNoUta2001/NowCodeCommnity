package com.bistu.community.service;

import com.bistu.community.dao.AlphaDao;
import com.bistu.community.dao.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Scope("prototype")//默认值为“singleton”，将其修改为prototype时每次访问Bean就会创建一个新的实例（并不常用，大多情况都是使用单实例）
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private UserMapper userMapper;
    public AlphaService(){
        System.out.println("实例化AlphaService");
    }
    @PostConstruct//下面的方法会在构造器之后调用
    public void init(){
        System.out.println("初始化AlphaService");
    }
    @PreDestroy//在销毁对象之前调用下面的方法
    public void distory(){
        System.out.println("销毁AlphaService");
    }
    public String find(){
        return alphaDao.select();
    }

}
