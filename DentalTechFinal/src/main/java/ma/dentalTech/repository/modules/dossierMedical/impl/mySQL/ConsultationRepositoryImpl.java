/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.dossierMedical.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    @Override
    public void create(Consultation c) {
        String sql = "INSERT INTO Consultation(date, statut, observationMedecin, idRDV) VALUES(?,?,?,?)";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(c.getDate()));
            ps.setString(2, c.getStatut());
            ps.setString(3, c.getObservationMedecin());
            ps.setLong(4, c.getIdRDV());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if(keys.next()) c.setIdConsultation(keys.getLong(1)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Consultation> findByRDVId(Long idRDV) {
        String sql = "SELECT * FROM Consultation WHERE idRDV = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRDV);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return Optional.of(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    /* Standard CRUD Methods */
    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM Consultation ORDER BY date DESC";
        List<Consultation> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while(rs.next()) list.add(RowMappers.mapConsultation(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM Consultation WHERE idConsultation = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return RowMappers.mapConsultation(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void update(Consultation c) {
        String sql = "UPDATE Consultation SET date=?, statut=?, observationMedecin=?, idRDV=? WHERE idConsultation=?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(c.getDate()));
            ps.setString(2, c.getStatut());
            ps.setString(3, c.getObservationMedecin());
            ps.setLong(4, c.getIdRDV());
            ps.setLong(5, c.getIdConsultation());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Consultation c) { deleteById(c.getIdConsultation()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Consultation WHERE idConsultation=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Consultation WHERE date = ?";
        List<Consultation> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) list.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Consultation> findByPatientId(Long idPatient) {
        /* JOIN with RDV to filter by patient */
        String sql = "SELECT c.* FROM Consultation c JOIN RDV r ON c.idRDV = r.idRDV WHERE r.idPatient = ? ORDER BY c.date DESC";
        List<Consultation> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPatient);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) list.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}