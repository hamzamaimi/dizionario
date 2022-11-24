package utils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class SendEmail {
    private static String emailServer;
    private static String emailPassword;

    public static boolean sendNewEmail(String recipientEmail, String subject, String emailContent) throws IOException {
        getEmailCredentials();
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props);

        try{
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(emailServer);
            msg.setRecipients(Message.RecipientType.TO, recipientEmail);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(emailContent);
            Transport.send(msg, emailServer, emailPassword);
            return true;
        } catch (MessagingException mex) {
            System.out.println("send failed, exception: " + mex);
            return false;
        }
    }

    private static void getEmailCredentials() throws IOException {
       ConfigReader configReader = new ConfigReader();
       emailServer = configReader.getValue("properties/credentials.properties", "email.server");
       emailPassword = configReader.getValue("properties/credentials.properties", "email.server.password");
    }

}
