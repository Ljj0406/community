package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSensitiveFilter(){
        String s = "吴亦凡和都美竹嫖娼被抓了";
        String text = sensitiveFilter.filter(s);
        System.out.println(text);
    }
}
