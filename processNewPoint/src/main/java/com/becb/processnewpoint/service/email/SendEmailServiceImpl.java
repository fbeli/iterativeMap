package com.becb.processnewpoint.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.becb.processnewpoint.exception.EmailException;
import com.becb.processnewpoint.core.EmailProperties;
import org.springframework.stereotype.Service;

@Service
public class SendEmailServiceImpl implements SendEmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    EmailProperties emailProperties;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean sendEmail(String to, String subject,  String htmlContent) throws EmailException {

        try {

            /*MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(htmlContent, true);
            helper.setFrom(emailProperties.getRemetente());
*/
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(htmlContent);
            mailMessage.setFrom(emailProperties.getRemetente());
            mailSender.send(mailMessage);

        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new EmailException("Email did not send",e);
        }

        return true;
    }
}
