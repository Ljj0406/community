package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void mailTest(){
        mailClient.sendMail("m17628014903@163.com","中午吃什么","不知道啊，很苦恼");
    }

    @Test
    public void HtmlMailTest(){
        Context context = new Context();
        context.setVariable("username","辜丽梅");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("m17628014903@163.com","Html邮件",content);

    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(101);
        ticket.setTicket("abc");
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));

        loginTicketMapper.insertLoginTicket(ticket);
    }

    @Test
    public void selectLoginTicket(){
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);

        loginTicketMapper.updateStatus("abc",1);
        System.out.println(ticket);
    }
}
