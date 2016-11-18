package org.nerif.util;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

	private static Email instance = null;

	private static Transport transport;
	private static Session session;

	public static Email getInstance() {
		if (instance == null) {
			instance = new Email();

			Properties props = System.getProperties();
			String host = "smtp.gmail.com";
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.user", Config.EMAIL_USERNAME);
			props.put("mail.smtp.password", Config.EMAIL_PASSWORD);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			session = Session.getDefaultInstance(props);

			try {
				transport = session.getTransport("smtp");
				transport.connect(host, Config.EMAIL_USERNAME, Config.EMAIL_PASSWORD);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public void sendFromGMail(List<String> to, String data, String descricao, String num) {
		Message message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(Config.EMAIL_USERNAME, Config.EMAIL_FROM));

			to.stream().forEach(a -> {
				try {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(a));
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			});

			message.setSubject(Config.EMAIL_SUBJECT);
			message.setContent(
					Config.EMAIL_BODY.replace("%indicador%", descricao).replace("%data%", data).replace("%vezes%", num),
					"text/html; charset=utf-8");

			transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (transport != null) {
			try {
				transport.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}
