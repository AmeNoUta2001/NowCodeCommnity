package com.bistu.community.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.bistu.community.dao.Elasticsearch.DiscussPostRepository;
import com.bistu.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);

    }

    public void deleteDisscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    public SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        HighlightField titleHighlightField = new HighlightField("title");
        HighlightField contentHighlightField = new HighlightField("content");
        Highlight titleHighlight = new Highlight(List.of(titleHighlightField,contentHighlightField));

        NativeQuery searchQueryBuilder = new NativeQueryBuilder()
                .withQuery(Query.of(q -> q.multiMatch(mq -> mq.query(keyword).fields("title","content"))))
                .withSort(Sort.by("type","score","createTime").descending())
                .withPageable(PageRequest.of(current, limit))
                .withHighlightQuery(
                        new HighlightQuery(titleHighlight,DiscussPost.class)
                )
                .build();

        //得到查询结果
        SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQueryBuilder, DiscussPost.class);
        //将其结果返回并进行分页
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, Page.empty().getPageable());

        if (!page.isEmpty()) {
            for (SearchHit<DiscussPost> discussPostSearch : page) {
                DiscussPost discussPost = discussPostSearch.getContent();
                //取高亮
                List<String> title = discussPostSearch.getHighlightFields().get("title");
                if(title!=null){
                    discussPost.setTitle(title.get(0));
                }
                List<String> content = discussPostSearch.getHighlightFields().get("content");
                if(content!=null){
                    discussPost.setContent(content.get(0));
                }
            }
        }

        return page;
    }

}
