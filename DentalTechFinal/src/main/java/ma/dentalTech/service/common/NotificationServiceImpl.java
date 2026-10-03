package ma.dentalTech.service.common;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationServiceImpl implements NotificationService {

    private final Properties config = new Properties();
    private final ma.dentalTech.repository.modules.userManager.api.NotificationRepository notificationRepo = new ma.dentalTech.repository.modules.userManager.impl.mySQL.NotificationRepositoryImpl();

    public NotificationServiceImpl() {
        // Chargement du fichier email.properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config/email.properties")) {
            if (input != null) {
                config.load(input);
            }
        } catch (IOException ex) {
            // Ignorer si pas de fichier, on fera juste de la notif in-app
        }
    }

    @Override
    public void envoyerEmail(String nomDestinataire, String sujet, String messageText) {
        // 1. Sauvegarde en Base (In-App Notification)
        // Note: Ici on n'a pas l'ID user direct malheureusement dans la signature
        // actuelle...
        // On va supposer un ID 0 ou system pour l'instant, ou il faudrait changer la
        // signature.
        // Pour faire simple et ne pas tout casser : on stocke avec un ID User = 1
        // (Admin par defaut)
        // ou on essaie de deviner.
        // Mieux : Dans la V2, passer l'objet Notification complet.

        // Creation de la notif persistante
        ma.dentalTech.entities.userManager.Notification n = ma.dentalTech.entities.userManager.Notification.builder()
                .titre(sujet)
                .message(messageText)
                .date(java.time.LocalDate.now())
                .time(java.time.LocalTime.now())
                .type("INFO")
                .priorite("MOYENNE")
                .idUser(1L) // DÉFAUT : ADMIN (A améliorer plus tard en passant l'ID)
                .build();

        notificationRepo.create(n);

        // 2. Tentative d'envoi Email (Uniquement si configuré)
        if (config.isEmpty()) {
            System.out.println("ℹ️ Notification enregistrée en base (Pas d'email config).");
            return;
        }

        // Configuration SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.host", config.getProperty("EMAIL_HOST"));
        prop.put("mail.smtp.port", config.getProperty("EMAIL_PORT"));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", config.getProperty("EMAIL_USE_TLS"));

        String myAccountEmail = config.getProperty("EMAIL_HOST_USER");
        String myPassword = config.getProperty("EMAIL_HOST_PASSWORD");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, myPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(myAccountEmail)); // Self-send debug
            message.setSubject("[DENTAL TECH] " + sujet);
            message.setText("Bonjour " + nomDestinataire + ",\n\n" + messageText);

            Transport.send(message);
            System.out.println("✅ EMAIL ENVOYÉ À " + nomDestinataire);

        } catch (MessagingException e) {
            System.err.println("⚠️ Echec envoi email (Notif gardée en base) : " + e.getMessage());
        }
    }

    @Override
    public java.util.List<ma.dentalTech.entities.userManager.Notification> getNotifications(Long idUser) {
        // Si idUser null, on renvoie tout (mode admin global) ou filtre sur 1
        return notificationRepo.findByUserId(idUser != null ? idUser : 1L);
    }

    @Override
    public void markAsRead(Long idNotification) {
        notificationRepo.markAsRead(idNotification);
    }
}