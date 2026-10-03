package ma.dentalTech.repository.modules.userManager.api;

import ma.dentalTech.entities.userManager.Notification;
import java.util.List;

public interface NotificationRepository {
    void create(Notification n);

    void markAsRead(Long idNotification);

    List<Notification> findByUserId(Long idUser);

    List<Notification> findAll();
}
