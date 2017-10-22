package wysyłka;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMail {
	

   public static void send(String to, String text) {
      String from = "laboratorium@mera-sp.com.pl";

      String host = "mera.home.pl";

      Properties properties = System.getProperties();

      properties.setProperty("mail.smtp.host", host);
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.port", "587");

      final String username = "laboratorium@mera-sp.com.pl";
      final String password = "%*U&es0Jp@KI";
      
      Session session = Session.getInstance(properties,
    	         new javax.mail.Authenticator() {
    	            protected PasswordAuthentication getPasswordAuthentication() {
    	               return new PasswordAuthentication(username, password);
    		   }
      });
      
      try {
         MimeMessage message = new MimeMessage(session);

         message.setFrom(new InternetAddress(from));

         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         message.setSubject("Przypomnienie o wzorcowaniu MERA Sp. z o.o.");

         message.setText(text);

         Transport.send(message);
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}
