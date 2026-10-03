package ma.dentalTech.repository.modules.medicament.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.medicament.api.MedicamentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    // CORRECTION : Nom standard sans accent pour correspondre au script de nettoyage
    private static final String TABLE = "\"médicament\"";

    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY nom";
        List<Medicament> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapMedicament(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE idMct = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedicament(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(Medicament m) {
        // CORRECTION : Utilisation de la constante TABLE
        String sql = "INSERT INTO " + TABLE + " (nom, laboratoire, type, forme, remboursable, prixUnitaire, description) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme());

            // Gestion du null pour le booléen
            if (m.getRemboursable() != null) {
                ps.setBoolean(5, m.getRemboursable());
            } else {
                ps.setBoolean(5, false);
            }

            ps.setDouble(6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setIdMct(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Medicament m) {
        String sql = "UPDATE " + TABLE + " SET nom=?, laboratoire=?, type=?, forme=?, remboursable=?, prixUnitaire=?, description=? WHERE idMct=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme());

            if (m.getRemboursable() != null) ps.setBoolean(5, m.getRemboursable());
            else ps.setBoolean(5, false);

            ps.setDouble(6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());
            ps.setLong(8, m.getIdMct());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Medicament m) { deleteById(m.getIdMct()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE idMct = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }



    @Override
    public List<Medicament> findByNomLike(String keyword) {
        // Recherche partielle (ex: "Doli" trouve "Doliprane")
        // LOWER() pour ignorer la casse (PostgreSQL/MySQL)
        String sql = "SELECT * FROM " + TABLE + " WHERE LOWER(nom) LIKE ?";
        List<Medicament> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Medicament> findByType(String type) {
        String sql = "SELECT * FROM " + TABLE + " WHERE type = ?";
        List<Medicament> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}