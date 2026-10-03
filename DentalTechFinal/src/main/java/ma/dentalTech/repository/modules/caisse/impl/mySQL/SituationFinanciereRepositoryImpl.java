/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.caisse.SituationFinanciere;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = "SELECT * FROM SituationFinanciere ORDER BY idSF DESC";
        List<SituationFinanciere> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(RowMappers.mapSituation(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public SituationFinanciere findById(Long id) {
        String sql = "SELECT * FROM SituationFinanciere WHERE idSF = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapSituation(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(SituationFinanciere s) {
        String sql = "INSERT INTO SituationFinanciere(totaleDesActes, totalePayé, crédit, statut, enPromo, idFacture) VALUES(?,?,?,?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, s.getTotaleDesActes());
            ps.setDouble(2, s.getTotalePaye());
            ps.setDouble(3, s.getCredit());
            ps.setString(4, s.getStatut());
            ps.setString(5, s.getEnPromo());
            ps.setLong(6, s.getIdFacture());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setIdSF(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(SituationFinanciere s) {
        String sql = "UPDATE SituationFinanciere SET totaleDesActes=?, totalePayé=?, crédit=?, statut=?, enPromo=? WHERE idSF=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, s.getTotaleDesActes());
            ps.setDouble(2, s.getTotalePaye());
            ps.setDouble(3, s.getCredit());
            ps.setString(4, s.getStatut());
            ps.setString(5, s.getEnPromo());
            ps.setLong(6, s.getIdSF());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(SituationFinanciere s) { deleteById(s.getIdSF()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM SituationFinanciere WHERE idSF=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<SituationFinanciere> findByFactureId(Long idFacture) {
        String sql = "SELECT * FROM SituationFinanciere WHERE idFacture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idFacture);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSituation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void deleteByFactureId(Long idFacture) {
        String sql = "DELETE FROM SituationFinanciere WHERE idFacture = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idFacture);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
