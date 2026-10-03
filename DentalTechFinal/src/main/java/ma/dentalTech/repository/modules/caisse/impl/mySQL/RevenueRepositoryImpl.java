// RevenueRepositoryImpl.java (Similaire pour ChargeRepositoryImpl)
/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.RevenueRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueRepositoryImpl implements RevenueRepository {
    @Override
    public List<Revenue> findAll() {
        String sql = "SELECT * FROM Revenue ORDER BY date DESC";
        List<Revenue> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while(rs.next()) list.add(RowMappers.mapRevenue(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // ... Implement Create/Update/Delete standard ...
    @Override public Revenue findById(Long id) { return null; }
    @Override public void create(Revenue r) {
        String sql = "INSERT INTO Revenue(titre, description, montant, date, idCabinet) VALUES(?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getDescription());
            ps.setDouble(3, r.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(r.getDate()));
            ps.setLong(5, r.getIdCabinet());
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){ if(keys.next()) r.setIdRevenue(keys.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override
    public void update(Revenue r) {
        String sql = "UPDATE Revenue SET titre=?, description=?, montant=?, date=?, idCabinet=? WHERE idRevenue=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getDescription());
            ps.setDouble(3, r.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(r.getDate()));
            ps.setLong(5, r.getIdCabinet());
            ps.setLong(6, r.getIdRevenue());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Revenue r) {
        deleteById(r.getIdRevenue());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Revenue WHERE idRevenue = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}