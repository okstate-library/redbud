package com.okstatelibrary.redbud.service.external;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import com.okstatelibrary.redbud.util.AppSystemProperties;

public class EmailService {

	private String username = AppSystemProperties.EmailUsername;
	private String password = AppSystemProperties.EmailPassword;
	private String sendEmails = AppSystemProperties.SendEmails;

	private final Properties prop;

	public EmailService() {

		prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

	}

	public void sendMail(String subject, String msg) throws Exception {

		Session session = Session.getInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("dls.okstate@gmail.com"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.sendEmails));
		message.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);

		Transport.send(message);
	}

}