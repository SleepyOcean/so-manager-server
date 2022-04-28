package com.sleepy.manager.main.service.impl;

import com.sleepy.manager.main.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author captain1920
 * @classname EmailNotificationServiceImpl
 * @description TODO
 * @date 2022/4/26 10:34
 */
@Service
public class EmailNotificationServiceImpl implements NotificationService {

    @Autowired
    private MailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void send(String mailTo, String title, String content) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            //邮箱发送内容组成
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(title);
            helper.setText(content, true);
            helper.setTo(mailTo);
            helper.setFrom("SleepyOcean系统通知<" + mailFrom + ">");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private void sendSimpleMsg(String mailTo, String title, String content) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("SleepyOcean<" + mailFrom + ">");
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setTo(mailTo);
        simpleMailMessage.setText(content);

        mailSender.send(simpleMailMessage);
    }

}
