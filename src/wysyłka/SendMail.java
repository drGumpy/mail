package wysyłka;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMail {
	private final String username ;
	private final String password;
	private String from;
	private static Properties properties;
	
	SendMail(Properties _properties, String _username,
			String _password,String _from){
		properties= _properties;
		username=_username;
		password=_password;
		from=_from;
	}

	void send(String to, String text) {
		
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

         message.setText(text, "utf-8", "html");

         Transport.send(message);
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}
