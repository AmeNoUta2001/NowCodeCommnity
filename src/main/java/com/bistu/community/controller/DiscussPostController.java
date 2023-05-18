package com.bistu.community.controller;

import com.bistu.community.entity.DiscussPost;
import com.bistu.community.entity.User;
import com.bistu.community.service.DiscussPostService;
import com.bistu.community.service.UserService;
import com.bistu.community.util.CommunityUtil;
import com.bistu.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 处理请求
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403,"用户未登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDisscussPost(post);

        // 报错将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");

    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    // 像这种只需要id的方法，通常会在path中添加上id
    // 因为这个方法需要返回模板 所以不加@ResponseBody注解
    // @PathVariable可以将方法内的参数提交到上面的path中
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 回复（待开发）
        return "/site/discuss-detail";
    }

}
