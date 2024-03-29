package com.becb.processnewpoint.service.email;

import java.util.Properties;
import com.becb.processnewpoint.exception.EmailException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.simplejavamail.mailer.MailerBuilder.buildMailer;

@Service("sendMailCourier")
public class SendEmailServiceImplCourier implements SendEmailService {

    @Override
    public boolean sendEmail(String to, String subject, String htmlContent) throws MailException {
        try {
            return sendToCourier(to, subject, htmlContent);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendToCourier(String to, String subject, String htmlContent) throws MailException, MessagingException {


        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("guidemapper@gmail.com", "gpgf uwcg vpca tpnf");
            }
        });

        MimeMessage message = new MimeMessage(session);
// Sender email
        message.setFrom(new InternetAddress("guidemapper@gmail.com"));
// Receiver email
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
// Email subject
        message.setSubject(subject);
// Email body
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
        return true;
    }
}
