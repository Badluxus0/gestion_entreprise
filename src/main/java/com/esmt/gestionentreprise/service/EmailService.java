package com.esmt.gestionentreprise.service;

import com.esmt.gestionentreprise.utils.SecParams;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private final String username = SecParams.USERNAME;
    private final String password = SecParams.PASSWORD;

    public void envoyerEmail(String destinataire, String sujet, String messageTexte) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(messageTexte);

            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + destinataire);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error processing mail", e);
        }
    }
}
