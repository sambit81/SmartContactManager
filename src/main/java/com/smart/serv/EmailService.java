package com.smart.serv;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to) {
		System.out.println("Executed");
		boolean f = false;
		
		String from = "sambitsinha81@gmail.com";
		
		// variable for gmail
    	String host = "smtp.gmail.com";
    	
    	//get the system properties
    	Properties properties = System.getProperties();
    	System.out.println("PROPERTIES " + properties);
    	
    	
    	// setting important information to properties object
    	
    	//host set
    	properties.put("mail.smtp.host", host);
    	properties.put("mail.smtp.port", "465");
    	properties.put("mail.smtp.ssl.enable", "true");
    	properties.put("mail.smtp.auth", "true");
    	
    	// step 1: to get the session object
    	Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("sambitsinha81@gmail.com", "zzba umum onrh mjzg");
			}
    		
		});
    	
    	session.setDebug(true);
    	
    	//Step 2 : Compose the message [text, multi media]
    	MimeMessage m = new MimeMessage(session);
    	
    	try {
    		//from email
			m.setFrom(from);
			
			//adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
			m.setText(message);
			
			//send
			
			//Step 3 : send the message using Transport class
			Transport.send(m);
			
			f = true;
			
			System.out.println("Sent success");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
