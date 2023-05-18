package com.bistu.community;

import com.bistu.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可~~~以~赌~博~，可以嫖娼，可以吸毒，可以嫖~~~娼 哈哈哈哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
