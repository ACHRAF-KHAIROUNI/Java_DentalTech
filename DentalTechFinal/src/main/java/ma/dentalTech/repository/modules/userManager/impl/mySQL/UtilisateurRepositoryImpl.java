package ma.dentalTech.repository.modules.userManager.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.userManager.api.UtilisateurRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM Utilisateur ORDER BY idUser ASC";
        List<Utilisateur> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM Utilisateur WHERE idUser = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapUtilisateur(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = "SELECT * FROM Utilisateur WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void create(Utilisateur u) {
        // AJOUT DE 'adresse' DANS LA REQUÊTE
        String sql = "INSERT INTO Utilisateur(nom, email, login, motDePasse, cin, tel, adresse, sexe, idRole, idCabinet) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillPreparedStatement(ps, u); // Refactoring pour éviter la duplication
            ps.setLong(10, u.getIdCabinet()); // idCabinet est le 10ème paramètre ici (car insert)

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setIdUser(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Utilisateur u) {
        // AJOUT DE 'adresse' DANS LA REQUÊTE
        String sql = "UPDATE Utilisateur SET nom=?, email=?, login=?, motDePasse=?, cin=?, tel=?, adresse=?, sexe=?, idRole=? WHERE idUser=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fillPreparedStatement(ps, u);
            ps.setLong(10, u.getIdUser()); // idUser est le 10ème paramètre ici (Clause WHERE)

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // Méthode utilitaire pour remplir les 9 premiers paramètres communs
    private void fillPreparedStatement(PreparedStatement ps, Utilisateur u) throws SQLException {
        ps.setString(1, u.getNom());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getLogin());
        ps.setString(4, u.getMotDePasse());
        ps.setString(5, u.getCin());
        ps.setString(6, u.getTel());
        ps.setString(7, u.getAdresse()); // Mapping de l'adresse
        ps.setString(8, u.getSexe() != null ? u.getSexe().name() : null);
        ps.setLong(9, u.getIdRole());
    }

    @Override
    public void delete(Utilisateur u) { deleteById(u.getIdUser()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Utilisateur WHERE idUser = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}