package com.bistu.community.controller;

import com.bistu.community.entity.User;
import com.bistu.community.service.LikeService;
import com.bistu.community.util.CommunityUtil;
import com.bistu.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    // 按照项目的逻辑 点赞功能是用户登陆之后才能使用的，但是我们在此先不做处理
    // 因为后续会使用SpringSecurity对拦截器进行重构，用这个模块统一处理安全和权限的问题
    public String like(int entityType, int entityId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 把数据放在map中方便传递
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);


        return CommunityUtil.getJSONString(0,null, map);
    }
}
