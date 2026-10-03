package ma.dentalTech.repository.modules.rdvPatient.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RDVRepositoryImpl implements RDVRepository {

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM RDV ORDER BY date, heure";
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM RDV WHERE idRDV = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(RDV r) {
        String sql = "INSERT INTO RDV(date, heure, motif, statut, noteMedecin, idPatient, idMedecin, idSecretaire) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(r.getDate()));
            ps.setTime(2, Time.valueOf(r.getHeure()));
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatut().toString());
            ps.setString(5, r.getNoteMedecin());
            ps.setObject(6, r.getIdPatient());
            ps.setObject(7, r.getIdMedecin());
            ps.setObject(8, r.getIdSecretaire());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setIdRDV(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(RDV r) {
        String sql = "UPDATE RDV SET date=?, heure=?, motif=?, statut=?, noteMedecin=? WHERE idRDV=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(r.getDate()));
            ps.setTime(2, Time.valueOf(r.getHeure()));
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatut().toString());
            ps.setString(5, r.getNoteMedecin());
            ps.setLong(6, r.getIdRDV());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(RDV r) { deleteById(r.getIdRDV()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM RDV WHERE idRDV = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // --- Méthodes spécifiques au module ---

    @Override
    public List<RDV> findByPatientId(Long idPatient) {
        String sql = "SELECT * FROM RDV WHERE idPatient = ? ORDER BY date DESC";
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = "SELECT * FROM RDV WHERE date = ? ORDER BY heure";
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // --- Mapper local pour ton module (Isolation) ---
    private RDV mapRow(ResultSet rs) throws SQLException {
        RDV r = new RDV();
        r.setIdRDV(rs.getLong("idRDV"));
        if (rs.getDate("date") != null) r.setDate(rs.getDate("date").toLocalDate());
        if (rs.getTime("heure") != null) r.setHeure(rs.getTime("heure").toLocalTime());
        r.setMotif(rs.getString("motif"));
        if (rs.getString("statut") != null) r.setStatut(StatutRDV.valueOf(rs.getString("statut")));
        r.setNoteMedecin(rs.getString("noteMedecin"));
        r.setIdPatient((Long) rs.getObject("idPatient"));
        r.setIdMedecin((Long) rs.getObject("idMedecin"));
        r.setIdSecretaire((Long) rs.getObject("idSecretaire"));
        return r;
    }

    @Override
    public List<RDV> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM RDV WHERE date >= ? AND date <= ? ORDER BY date, heure";
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs)); // Utilise ta méthode mapRow existante
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}