package com.bistu.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
@Repository
@Primary//增加这个bean的优先级
public class AlphaDaoMyBatisImpl implements AlphaDao{

    @Override
    public String select() {
        return "MyBatis";
    }
}
