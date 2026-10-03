package ma.dentalTech.repository.modules.agenda.impl.mySQL;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.AgendaRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendaRepositoryImpl implements AgendaRepository {

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM AgendaMensuel";
        List<AgendaMensuel> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while(rs.next()) list.add(RowMappers.mapAgenda(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM AgendaMensuel WHERE idAgenda = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return RowMappers.mapAgenda(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void create(AgendaMensuel a) {
        String sql = "INSERT INTO AgendaMensuel(mois, etat, idMedecin) VALUES(?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getMois());
            ps.setBoolean(2, a.getEtat());
            ps.setLong(3, a.getIdMedecin());
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()) a.setIdAgenda(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(AgendaMensuel a) {
        String sql = "UPDATE AgendaMensuel SET mois=?, etat=? WHERE idAgenda=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getMois());
            ps.setBoolean(2, a.getEtat());
            ps.setLong(3, a.getIdAgenda());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(AgendaMensuel a) { deleteById(a.getIdAgenda()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM AgendaMensuel WHERE idAgenda=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long idMedecin) {
        String sql = "SELECT * FROM AgendaMensuel WHERE idMedecin = ?";
        List<AgendaMensuel> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) list.add(RowMappers.mapAgenda(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
    @Override
    public List<LocalDate> getJoursNonDisponibles(Long idAgenda) {
        String sql = "SELECT jour FROM Agenda_Jours_Non_Disponible WHERE idAgenda = ? ORDER BY jour";
        List<LocalDate> jours = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idAgenda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jours.add(rs.getDate("jour").toLocalDate());
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return jours;
    }
    @Override
    public void addJourNonDisponible(Long idAgenda, LocalDate jour) {
        // On utilise INSERT IGNORE (ou ON CONFLICT DO NOTHING) pour éviter de planter si on clique 2 fois sur le même jour
        // Si PostgreSQL : utiliser "INSERT INTO ... ON CONFLICT DO NOTHING" ou vérifier avant.
        // Pour faire simple ici :
        String sql = "INSERT INTO Agenda_Jours_Non_Disponible(idAgenda, jour) VALUES(?,?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idAgenda);
            ps.setDate(2, Date.valueOf(jour));
            ps.executeUpdate();

        } catch (SQLException e) {
            // Si c'est une erreur de duplicata (clé primaire), on l'ignore silencieusement, sinon on lance l'erreur
            if (!e.getSQLState().startsWith("23")) { // 23 = contrainte d'intégrité
                throw new RuntimeException("Erreur insert jour indisponible", e);
            }
        }
    }
}