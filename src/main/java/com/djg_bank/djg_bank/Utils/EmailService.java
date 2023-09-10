package com.djg_bank.djg_bank.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class EmailService {

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.password}")
    private String emailPassword;

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put( "mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.secure", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Reemplaza con el servidor SMTP que estés utilizando
        properties.put("mail.smtp.port", "465"); // Reemplaza con el puerto del servidor SMTP

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailUsername));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(content, "text/html");

        Transport.send(message);
    }
}
