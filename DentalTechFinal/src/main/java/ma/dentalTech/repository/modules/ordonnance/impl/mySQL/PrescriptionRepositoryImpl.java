package ma.dentalTech.repository.modules.ordonnance.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.ordonnance.api.PrescriptionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private static final String TABLE = "Prescription";

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM " + TABLE;
        List<Prescription> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapPrescription(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idPr = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPrescription(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Prescription p) {
        String sql = "INSERT INTO " + TABLE + "(quantité, fréquence, duréeEnJours, idOrd, idMct) VALUES(?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getQuantite());
            ps.setString(2, p.getFrequence());
            ps.setInt(3, p.getDureeEnJours());
            ps.setLong(4, p.getIdOrd());
            ps.setLong(5, p.getIdMct());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdPr(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Prescription p) {
        String sql = "UPDATE " + TABLE + " SET quantité=?, fréquence=?, duréeEnJours=?, idOrd=?, idMct=? WHERE idPr=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getQuantite());
            ps.setString(2, p.getFrequence());
            ps.setInt(3, p.getDureeEnJours());
            ps.setLong(4, p.getIdOrd());
            ps.setLong(5, p.getIdMct());
            ps.setLong(6, p.getIdPr());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Prescription p) { deleteById(p.getIdPr()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE idPr = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Prescription> findByOrdonnanceId(Long idOrdonnance) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idOrd = ?";
        List<Prescription> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idOrdonnance);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // Cette méthode ne doit PAS avoir @Override si elle n'est pas dans CrudRepository ou PrescriptionRepository
    // Mais comme on l'a ajoutée dans PrescriptionRepository (Etape 2), le @Override est valide ici.
    @Override
    public List<Prescription> findByPatientId(Long idPatient) {
        String sql = "SELECT p.* FROM " + TABLE + " p " +
                "JOIN Ordonnance o ON p.idOrd = o.idOrd " +
                "WHERE o.idPatient = ?";
        List<Prescription> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}