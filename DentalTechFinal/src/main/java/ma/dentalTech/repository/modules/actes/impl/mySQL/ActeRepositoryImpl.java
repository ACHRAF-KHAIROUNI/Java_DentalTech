/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.actes.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.actes.api.ActeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActeRepositoryImpl implements ActeRepository {

    @Override
    public List<Acte> findAll() {
        String sql = "SELECT * FROM Acte";
        List<Acte> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Acte findById(Long id) {
        String sql = "SELECT * FROM Acte WHERE idActe = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapActe(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void create(Acte a) {
        /* Best Practice: Columns without accents */
        String sql = """
            INSERT INTO Acte(libellé, catégorie, prixDeBase, description, code)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());
            ps.setDouble(3, a.getPrixDeBase());
            ps.setString(4, a.getDescription());
            ps.setInt(5, a.getCode());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setIdActe(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Acte a) {
        String sql = """
            UPDATE Acte
            SET libellé=?, catégorie=?, prixDeBase=?, description=?, code=?
            WHERE idActe=?
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());
            ps.setDouble(3, a.getPrixDeBase());
            ps.setString(4, a.getDescription());
            ps.setInt(5, a.getCode());
            ps.setLong(6, a.getIdActe());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Acte a) {
        deleteById(a.getIdActe());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Acte WHERE idActe = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* Specific ActeRepository Methods */

    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = "SELECT * FROM Acte WHERE catégorie = ?";
        List<Acte> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Acte> findByLibelleLike(String keyword) {
        String sql = "SELECT * FROM Acte WHERE libellé LIKE ?";
        List<Acte> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
