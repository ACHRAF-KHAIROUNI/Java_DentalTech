package ma.dentalTech.repository.modules.dashboard.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.rdvPatient.RDV; // Assure-toi que c'est le bon import
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dashboard.api.DashboardRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardRepositoryImpl implements DashboardRepository {

    @Override
    public long getNombrePatients() {
        String sql = "SELECT COUNT(*) FROM Patient";
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public long getRDVToday() {
        String sql = "SELECT COUNT(*) FROM RDV WHERE date = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public double getChiffreAffaireMois() {
        String sql = "SELECT SUM(totalePayé) FROM Facture WHERE EXTRACT(MONTH FROM dateFacture) = ? AND EXTRACT(YEAR FROM dateFacture) = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            LocalDate now = LocalDate.now();
            ps.setInt(1, now.getMonthValue());
            ps.setInt(2, now.getYear());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    @Override
    public long getAlertesStock() {
        return 0;
    }

    // --- C'EST CETTE MÉTHODE QUI MANQUAIT ---
    @Override
    public List<RDV> getListRDVToday() {
        String sql = "SELECT * FROM RDV WHERE date = ? ORDER BY heure ASC";
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}