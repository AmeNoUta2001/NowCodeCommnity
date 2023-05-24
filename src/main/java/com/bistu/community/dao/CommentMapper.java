package com.bistu.community.dao;

import com.bistu.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 因为在页面上要对评论进行分页处理,同时也要判断评论的实体类型，下面这个方法的返回值是list，目的是规定每一页上有多少条评论，这样方便分页
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
    // 返回评论的数量
    int selectCountByEntity(int entityType, int entityId);
    // 增加评论
    int insertComment(Comment comment);


}
