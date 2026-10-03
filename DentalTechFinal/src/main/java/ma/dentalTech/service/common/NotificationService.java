package ma.dentalTech.service.common;

public interface NotificationService {
    void envoyerEmail(String nomDestinataire, String sujet, String message);

    // New methods for In-App Notifications
    java.util.List<ma.dentalTech.entities.userManager.Notification> getNotifications(Long idUser);

    void markAsRead(Long idNotification);
}