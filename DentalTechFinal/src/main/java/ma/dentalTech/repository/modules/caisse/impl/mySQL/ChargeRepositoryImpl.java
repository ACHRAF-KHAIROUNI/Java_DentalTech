/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.ChargeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChargeRepositoryImpl implements ChargeRepository {

    @Override
    public void create(Charge ch) {
        // Code déjà fourni précédemment...
        String sql = "INSERT INTO Charge(titre, description, montant, date, idCabinet) VALUES(?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ch.getTitre());
            ps.setString(2, ch.getDescription());
            ps.setDouble(3, ch.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(ch.getDate()));
            ps.setLong(5, ch.getIdCabinet());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if(keys.next()) ch.setIdCharge(keys.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Charge> findAll() {
        String sql = "SELECT * FROM Charge ORDER BY date DESC";
        List<Charge> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while(rs.next()) list.add(RowMappers.mapCharge(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Charge findById(Long id) {
        String sql = "SELECT * FROM Charge WHERE idCharge = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCharge(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void update(Charge c) {
        String sql = "UPDATE Charge SET titre=?, description=?, montant=?, date=?, idCabinet=? WHERE idCharge=?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitre());
            ps.setString(2, c.getDescription());
            ps.setDouble(3, c.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(c.getDate()));
            ps.setLong(5, c.getIdCabinet());
            ps.setLong(6, c.getIdCharge());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Charge c) {
        deleteById(c.getIdCharge());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Charge WHERE idCharge = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}