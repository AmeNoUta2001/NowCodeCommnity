package com.bistu.community;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.bistu.community.dao.DiscussPostMapper;
import com.bistu.community.dao.Elasticsearch.DiscussPostRepository;
import com.bistu.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    @Test
    // 添加一条数据
    public void testInsert() {
        discussRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussRepository.save(discussPostMapper.selectDiscussPostById(243));
    }
    @Test
    // 添加多条数据
    public void testInsertList() {
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(138,0,100));
//        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(145,0,100));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(171,0,100));
    }
    @Test
    // 修改数据
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，库库灌水");
        // save方法面对在es中已经存在的数据会保存最新的一份数据
        discussRepository.save(post);
    }

    @Test
    // 删除数据
    public void testDelete() {
//        discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }

    @Test
    public void testSearchByTemplate(){
//        高亮实现仿的这 https://www.cnblogs.com/gdwkong/p/17331639.html
        HighlightField titleHighlightField = new HighlightField("title");
        HighlightField contentHighlightField = new HighlightField("content");

        Highlight titleHighlight = new Highlight(List.of(titleHighlightField,contentHighlightField));

        NativeQuery searchQuery =new NativeQueryBuilder()
//                查询这里直接使用了lambda表达式，不用写实现类
                .withQuery(Query.of(q -> q.multiMatch(mq -> mq.query("互联网寒冬").fields("title","content"))))
//                倒序直接用Sort.by后面加descending()就行
                .withSort(Sort.by("type","score","createTime").descending())
                .withPageable(PageRequest.of(0, 10))
                .withHighlightQuery(
                        new HighlightQuery(titleHighlight,DiscussPost.class)
                )
                .build();

        SearchHits<DiscussPost> search = elasticTemplate.search(searchQuery, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, searchQuery.getPageable());

        if (!page.isEmpty()){
            for (SearchHit<DiscussPost> hit : page) {
                DiscussPost discussPost = hit.getContent();
//                获取高亮部分
                List<String> title = hit.getHighlightFields().get("title");
                if (title!=null){
                    discussPost.setTitle(title.get(0));
                }
                List<String> content = hit.getHighlightFields().get("content");
                if (content!=null){
                    discussPost.setContent(content.get(0));
                }
                System.out.println(hit.getContent());
            }
        }

/*        SearchPage<DiscussPost> searchPage = page;
        List<Map<String, Object>> discussPosts = new ArrayList<Map<String, Object>>();
        if (searchPage != null) {
            for (SearchHit<DiscussPost> discussPostSearchHit : searchPage) {
                Map<String, Object> map = new HashMap<>();
                //帖子
                DiscussPost post = discussPostSearchHit.getContent();
                System.out.println(post);
            }
        }*/

    }


}
