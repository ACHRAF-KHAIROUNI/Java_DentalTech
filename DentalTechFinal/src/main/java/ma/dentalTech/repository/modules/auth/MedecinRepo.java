package ma.dentalTech.repository.modules.auth;

import ma.dentalTech.conf.SessionFactory;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.entities.userManager.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepo {

    public List<Medecin> findAll() {
        // SQL adapté à ta capture d'écran (minuscules)
        // On récupère idmedecin, specialite, et le nom de l'utilisateur joint
        String sql = "SELECT m.idmedecin, m.specialite, u.nom, u.email " +
                "FROM medecin m " +
                "JOIN utilisateur u ON m.iduser = u.iduser";

        List<Medecin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // 1. Création de l'objet Utilisateur (juste pour porter le nom)
                Utilisateur u = new Utilisateur();
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));

                // 2. Création de l'objet Medecin
                Medecin m = new Medecin();
                m.setIdMedecin(rs.getLong("idmedecin"));
                m.setSpecialite(rs.getString("specialite"));

                // 3. Liaison
                m.setUtilisateur(u);

                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}