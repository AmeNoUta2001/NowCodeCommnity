package com.bistu.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        // Redis库中操作Redis的方法名基本与Redis中的命令相同
        // 存数据
        redisTemplate.opsForValue().set(redisKey, 30);
        // 取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangwei");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));

    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "张伟","小丑","蔡徐坤","小黑子");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey, "坤坤", 80);
        redisTemplate.opsForZSet().add(redisKey, "只因", 90);
        redisTemplate.opsForZSet().add(redisKey, "你干嘛", 50);
        redisTemplate.opsForZSet().add(redisKey, "amagi", 70);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "amagi"));
        // 查看当前key在ZSet中的排名（从小到大）
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "只因"));
        // 查看当前value在ZSet中的排名（从大到小）
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "只因"));
        // 查看规定范围内的value在ZSet中的排名（从小到大）
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        // 查看规定范围内的value在ZSet中的排名（从大到小）
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // 可以直接将key绑定到operations对象上，省去多次操作redis中的同一个key时需要重复输入key的动作。
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    // 因为redis不是关系型数据库，所以redis不满足ACID四个特性，所以redis在处理事务时会先存储输入的命令，在提交之后统一执行。
    // 在启用事务后，在执行redis命令时并不会立刻执行该命令，而是先将命令存在一个队列中。直到操作结束，提交事务的时候才会统一执行队列中的命令
    // 例如在事务中间查询这种情况，在这种情况下查询语句就不会立即执行。所以需要注意不要在redis的事务中间执行查询语句。
    // 也因为redis事务的这个特性，所以在使用事务的大多数情况都使用编程式事务

    // 编程式事务
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            String redisKey = "test:tx";
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForSet().add(redisKey, "蔡徐坤");
                operations.opsForSet().add(redisKey, "坤坤");
                operations.opsForSet().add(redisKey, "只因");
                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec();
            }
        });

        System.out.println(obj);
    }
}
