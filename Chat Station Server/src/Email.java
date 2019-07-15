import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email
{
	public static void send(String email, String username, String code)
	{
	    String subject = "Chat Station account confirmation code";
	    String msg = String.format(
	    		     "Hello %s,\n"
	    		   + "thank you for registering at Chat Station.\n"
	    		   + "To activate your account you must insert this confirmation code.\n"
	    		   + "Confirmation code: %s\n"
	    		   + "Kind regards,\n"
	    		   + "the Chat Station team"
	    		   , username, code);
	    
	    final String from = "chatstation.noreply@gmail.com";
	    final String password = "qwerty-123";

	    Properties properties = new Properties();  
	    properties.setProperty("mail.transport.protocol", "smtp");     
	    properties.setProperty("mail.host", "smtp.gmail.com");  
	    properties.put("mail.smtp.auth", "true");  
	    properties.put("mail.smtp.port", "465");  
	    properties.put("mail.debug", "true");  
	    properties.put("mail.smtp.socketFactory.port", "465");  
	    properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
	    properties.put("mail.smtp.socketFactory.fallback", "false");  
	    
	    Authenticator authenticator = new Authenticator() {
	    	protected PasswordAuthentication getPasswordAuthentication()
	    	{
	    		return new PasswordAuthentication(from, password);
	    	}
	    };
	    
	   Session session = Session.getDefaultInstance(properties, authenticator);  

	   session.setDebug(false);  
	   try
	   {
		   Transport transport = session.getTransport();  
		   InternetAddress addressFrom = new InternetAddress(from);  

		   MimeMessage message = new MimeMessage(session);  
		   message.setSender(addressFrom);  
		   message.setSubject(subject);  
		   message.setContent(msg, "text/plain");  
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));  

		   transport.connect();  
		   Transport.send(message);  
		   transport.close();
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	}
}
