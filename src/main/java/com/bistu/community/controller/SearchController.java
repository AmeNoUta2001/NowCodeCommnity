package com.bistu.community.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.Like;
import com.bistu.community.entity.DiscussPost;
import com.bistu.community.entity.Page;
import com.bistu.community.service.ElasticsearchService;
import com.bistu.community.service.LikeService;
import com.bistu.community.service.UserService;
import com.bistu.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    // search?keyword=xxxx
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        // 由于封装的page中的当前页面是从1开始，然而search方法中的page要从0开始，所以-1
        SearchPage<DiscussPost> searchResult =
            elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (!searchResult.isEmpty()) {
            for (SearchHit<DiscussPost> discussPostSearchHit : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                DiscussPost post = discussPostSearchHit.getContent();
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
