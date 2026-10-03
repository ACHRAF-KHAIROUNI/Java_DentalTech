package ma.dentalTech.repository.modules.userManager.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.userManager.api.MedecinRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    @Override
    public List<Medecin> findAll() {
        // La jointure est essentielle ici pour avoir le nom du médecin
        String sql = "SELECT m.*, u.* FROM Medecin m " +
                "JOIN Utilisateur u ON m.idUser = u.idUser";
        List<Medecin> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void create(Medecin m) {
        String sql = "INSERT INTO Medecin(specialite, idUser) VALUES(?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getSpecialite());
            ps.setLong(2, m.getIdUser()); // L'utilisateur doit déjà être créé
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Medecin m) {
        String sql = "UPDATE Medecin SET specialite=? WHERE idMedecin=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getSpecialite());
            ps.setLong(2, m.getIdMedecin());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Long idMedecin) {
        String sql = "DELETE FROM Medecin WHERE idMedecin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}