package com.bistu.community.dao;

import com.bistu.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
//    一般业务中不会删除数据，只会改变数据的状态。这样可以方便以后进行统计等工作
//    此处使用了一些新的注解，这些注解可以把一个或多个字符串拼成SQL语句，跟在xml里写SQL的效果是一样的
//    但是这种方法最好用在SQL语句较少的业务中，如果SQL语句多的话还是XML看着舒服
//    而且在这是以字符串的形式写的语句，也就是说这种方式没有标签提示，而且表名和字段名都是手写的，容易出错，测试没有问题后再继续
//    在写多行SQL语句时 最好在每行的结尾加一个空格 不让多个SQL语句连在一起
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired)" ,
            "values(#{user_id}, #{ticket}, #{status}, #{expired})"
    })
    /**
     * @Options注解在此处的用处跟application.properties中useGeneratedKeys的作用一样，都是实现id字段的自增
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    @Select({
            "select id,user_id,ticket,status,expired" ,
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);
    @Update({
//    要想实现动态SQL语句（if标签）需要在SQL语句的首尾加上<script></script>标签，这样script中的语法就跟xml中的语法一致了
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);
}
