package com.bistu.community.service;

import com.bistu.community.dao.AlphaDao;
import com.bistu.community.dao.DiscussPostMapper;
import com.bistu.community.dao.UserMapper;
import com.bistu.community.entity.DiscussPost;
import com.bistu.community.entity.User;
import com.bistu.community.util.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
//@Scope("prototype")//默认值为“singleton”，将其修改为prototype时每次访问Bean就会创建一个新的实例（并不常用，大多情况都是使用单实例）
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

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


    // @Transactional Spring内部注解创建一个默认的隔离，想手动调整隔离级别可以在这个注解后添加参数(方法内部常量)
    // 在配置事务的同时一般还会配置事务的传播机制(方法内部常量)
    // 最常用的传播机制有三个
    /**
     * REQUIRED：支持当前事务（外部事物），如果不存在则创建新事务
     * REQUIRES_NEW：创建一个新事物，并且暂停当前事务（外部事物）
     * NESTED：如果当前存在事务（外部事物），则嵌套在该事务中执行（独立的提交和回滚），否则就会和REQUIRED一样
     */
    // 声明式事务管理
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("123123@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());

        userMapper.insertUser(user);
        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("你好你好你好你好你好你好你好你好你好");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 人为造一个错误
        Integer.valueOf("abc");

        return "ok";
    }

    // 编程式事务管理
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999.png");
                user.setCreateTime(new Date());

                userMapper.insertUser(user);
                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("beta");
                post.setContent("再见再见再见再见再见再见再见再见");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                // 人为造一个错误
                Integer.valueOf("abc");

                return "ok";
            }
        });

    }

}
