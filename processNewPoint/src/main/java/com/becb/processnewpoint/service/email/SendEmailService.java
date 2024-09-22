package com.becb.processnewpoint.service.email;

import com.becb.processnewpoint.exception.EmailException;
import org.springframework.mail.MailException;

public interface SendEmailService {

    boolean sendEmail(String to, String subject, String htmlContent) throws MailException, EmailException;

    default String createHtmlContentForgotPasswod(String nome, String email, String code) {

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>Reset Password</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h2>Hello ");
        sb.append(nome);
        sb.append("</h2>");
        sb.append("<p>Somebody requested a new password for the  <b>GuideMapper</b> account associated with <b>");
        sb.append(email);
        sb.append("</b>.</p>");
        sb.append("<p>No changes have been made to your account yet.</p>");
        sb.append("<p>You can reset your password by clicking the link below:</p>");
        sb.append("<p><a href=\"m.guidemapper.com/reset/reset.html\">Reset Password</a></p>");
        sb.append("<p>Use this Code as Validation: <b> ");
        sb.append(code);
        sb.append("</b></p>");
        sb.append("<p>This code is valid for 30 min</p>");
        sb.append("<p>If you did not request a new password, please let us know immediately by replying to this email.</p>");
        sb.append("<p>Yours,</p>");
        sb.append("<p>The GuideMapper team</p>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();

    }
}
