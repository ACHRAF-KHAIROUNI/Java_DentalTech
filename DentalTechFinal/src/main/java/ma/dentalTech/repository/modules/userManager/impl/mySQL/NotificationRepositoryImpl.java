package ma.dentalTech.repository.modules.userManager.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.userManager.Notification;
import ma.dentalTech.repository.modules.userManager.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public void create(Notification n) {
        String sql = "INSERT INTO notification (titre, message, date_notif, heure_notif, type_notif, priorite, id_user, lu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, n.getTitre());
            ps.setString(2, n.getMessage());
            ps.setDate(3, Date.valueOf(n.getDate()));
            ps.setTime(4, Time.valueOf(n.getTime()));
            ps.setString(5, n.getType()); // ex: EMAIL, IN_APP
            ps.setString(6, n.getPriorite());
            ps.setLong(7, n.getIdUser() != null ? n.getIdUser() : 0); // 0 or NULL for system/global?
            ps.setBoolean(8, false); // Non lu par défaut

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // On ne bloque pas pour une notif
        }
    }

    @Override
    public void markAsRead(Long idNotification) {
        String sql = "UPDATE notification SET lu = true WHERE id_notification = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idNotification);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> findByUserId(Long idUser) {
        String sql = "SELECT * FROM notification WHERE id_user = ? ORDER BY date_notif DESC, heure_notif DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification ORDER BY date_notif DESC, heure_notif DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Notification map(ResultSet rs) throws SQLException {
        return Notification.builder()
                .idNotification(rs.getLong("id_notification"))
                .titre(rs.getString("titre"))
                .message(rs.getString("message"))
                .date(rs.getDate("date_notif") != null ? rs.getDate("date_notif").toLocalDate() : LocalDate.now())
                .time(rs.getTime("heure_notif") != null ? rs.getTime("heure_notif").toLocalTime() : LocalTime.now())
                .type(rs.getString("type_notif"))
                .priorite(rs.getString("priorite"))
                .idUser(rs.getLong("id_user"))
                // .lu(rs.getBoolean("lu")) // S'assurer que le champ existe dans l'entité si
                // besoin
                .build();
    }
}
