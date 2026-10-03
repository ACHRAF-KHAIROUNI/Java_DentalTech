package ma.dentalTech.repository.modules.ordonnance.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // Nom de la table (avec majuscule/guillemets pour PostgreSQL si nécessaire)
    private static final String TABLE = "Ordonnance";

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY date DESC";
        List<Ordonnance> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapOrdonnance(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idOrd = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapOrdonnance(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Ordonnance o) {
        String sql = "INSERT INTO " + TABLE + "(date, idPatient, idMedecin) VALUES(?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(o.getDate()));
            ps.setLong(2, o.getIdPatient());
            ps.setLong(3, o.getIdMedecin());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdOrd(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Ordonnance o) {
        String sql = "UPDATE " + TABLE + " SET date=?, idPatient=?, idMedecin=? WHERE idOrd=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(o.getDate()));
            ps.setLong(2, o.getIdPatient());
            ps.setLong(3, o.getIdMedecin());
            ps.setLong(4, o.getIdOrd());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Ordonnance o) {
        deleteById(o.getIdOrd());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE idOrd = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Ordonnance> findByPatientId(Long idPatient) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idPatient = ? ORDER BY date DESC";
        List<Ordonnance> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Ordonnance> findByMedecinId(Long idMedecin) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idMedecin = ? ORDER BY date DESC";
        List<Ordonnance> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}