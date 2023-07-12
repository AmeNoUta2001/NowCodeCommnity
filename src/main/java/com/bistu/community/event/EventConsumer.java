package com.bistu.community.event;

import com.alibaba.fastjson.JSONObject;
import com.bistu.community.entity.DiscussPost;
import com.bistu.community.entity.Event;
import com.bistu.community.entity.Message;
import com.bistu.community.service.DiscussPostService;
import com.bistu.community.service.ElasticsearchService;
import com.bistu.community.service.MessageService;
import com.bistu.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    // 在这里用一个方法处理评论、点赞、关注这三个事件
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        // 把生产者生产的JSON字符串再转换回Evnet对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()) {
            // entryMap的具体作用参见 https://blog.csdn.net/Isringring/article/details/81940367
            for (Map.Entry<String, Object> entry: event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }
        // 把生产者生产的JSON字符串再转换回Evnet对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        // 将贴子存入es服务器
        elasticsearchService.saveDiscussPost(post);
    }

}
