package ma.dentalTech.repository;

import ma.dentalTech.conf.SessionFactory;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DbTestUtils {

    public static void cleanDatabase() {
        // Noms exacts entre guillemets pour forcer PostgreSQL à les trouver
        String[] tables = {
                "\"Prescription\"", "\"Ordonnance\"", "\"Medicament\"",
                "\"Facture\"", "\"SituationFinanciere\"", "\"Revenue\"", "\"Charge\"",
                "\"Statistique\"", "\"Certificat\"",
                "\"Acte_Intervention\"", "\"InterventionMedecin\"", "\"Consultation\"",
                "\"RDV\"",  // <--- C'est lui qui posait problème
                "\"Antecedents\"", "\"Patient\"",
                "\"Utilisateur\"", "\"Role\"", "\"Cabinet_Medical\""
        };

        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement s = c.createStatement()) {

            for (String table : tables) {
                try {
                    // TRUNCATE ... CASCADE vide la table et toutes celles qui en dépendent
                    s.executeUpdate("TRUNCATE TABLE " + table + " CASCADE");
                    System.out.println("DEBUG: Table " + table + " vidée.");
                } catch (SQLException e) {
                    // On ignore si la table n'existe pas, mais on affiche si c'est une autre erreur
                    if(!e.getSQLState().equals("42P01")) {
                        System.err.println("ERREUR NETTOYAGE " + table + " : " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}