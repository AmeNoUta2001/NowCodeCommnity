package com.bistu.community.service;

import com.bistu.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 在关注一个用户时会涉及到两次存储的操作，一个是关注的目标，另一个是粉丝。所以就需要涉及到事务处理的内容了。
    public void follow (int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.gerFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.gerFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    public void unfollow (int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.gerFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.gerFollowerKey(entityType, entityId);

                operations.multi();

                // 进行删除操作不需要分数
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                return operations.exec();
            }
        });
    }

    // 查询关注的实体数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.gerFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.gerFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否关注该实体
    public boolean hasFollowed(int userId,int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.gerFolloweeKey(userId, entityType);
        // 查询某个实体的分数判断是否关注
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}
