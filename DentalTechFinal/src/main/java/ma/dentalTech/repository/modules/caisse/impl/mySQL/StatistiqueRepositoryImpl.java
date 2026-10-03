/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.caisse.Statistique;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.StatistiqueRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class StatistiqueRepositoryImpl implements StatistiqueRepository {

    @Override
    public List<Statistique> findAll() {
        String sql = "SELECT * FROM Statistique ORDER BY dateCalcul DESC";
        List<Statistique> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapStatistique(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Statistique findById(Long id) {
        String sql = "SELECT * FROM Statistique WHERE idStatistique = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistique(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Statistique s) {
        String sql = "INSERT INTO Statistique(nom, categorie, chiffre, dateCalcul, idCabinet) VALUES(?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNom());
            ps.setString(2, s.getCategorie());
            ps.setDouble(3, s.getChiffre());
            ps.setDate(4, Date.valueOf(s.getDateCalcul()));
            ps.setLong(5, s.getIdCabinet());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setIdStatistique(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Statistique s) {
        String sql = "UPDATE Statistique SET nom=?, categorie=?, chiffre=?, dateCalcul=? WHERE idStatistique=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNom());
            ps.setString(2, s.getCategorie());
            ps.setDouble(3, s.getChiffre());
            ps.setDate(4, Date.valueOf(s.getDateCalcul()));
            ps.setLong(5, s.getIdStatistique());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Statistique s) { deleteById(s.getIdStatistique()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Statistique WHERE idStatistique=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Statistique> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Statistique WHERE dateCalcul = ?";
        List<Statistique> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapStatistique(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
