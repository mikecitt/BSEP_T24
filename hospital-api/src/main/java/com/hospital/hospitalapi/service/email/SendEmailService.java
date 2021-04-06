package com.hospital.hospitalapi.service.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.hospital.hospitalapi.service.email.exception.EmailNotSentException;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {

	private static final String NOREPLY_ADDRESS = "noreply@inviggo.com";

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	MailProperties mailProperties;

	public void sendSimpleMessage(String[] to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(NOREPLY_ADDRESS);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);

			javaMailSender.send(message);
		} catch (MailException exception) {
			exception.printStackTrace();
		}
	}

	public void sendHtmlMail(String[] to, String subject, String body) {
		MimeMessage mail = javaMailSender.createMimeMessage();

		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mail, true);
			helper.setFrom(NOREPLY_ADDRESS);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
		} catch (MessagingException e) {
			throw new EmailNotSentException(ExceptionErrorCodeType.EMAIL_NOT_SENT, "Email was not sent");
		}

		javaMailSender.send(mail);
	}

}
