package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.params.SendMailParams;
import com.chairpick.ecommerce.services.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(Task<SendMailParams> task) {
        SendMailParams mailParams = task.getInfo();
        String to = mailParams.getUser().getEmail();
        String subject = mailParams.getSubject();
        String content = mailParams.getContent();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setBcc(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}
