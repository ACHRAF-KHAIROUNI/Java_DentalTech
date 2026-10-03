package ma.dentalTech.service.modules.backup;

import ma.dentalTech.conf.SessionFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//khairouni achraf
public class BackupService {

    // Liste corrigée selon votre schema.sql (Attention aux majuscules, accents et underscores !)
    private final String[] TABLES = {
            "Cabinet_Medical",          // Était CabinetMedical
            "Role",
            "Role_Privileges",          // Ajouté car présent dans le schéma
            "Utilisateur",
            "Notification",             // Ajouté
            "Statistique",              // Ajouté
            "Admin",
            "Staff",
            "Medecin",
            "Secretaire",
            "AgendaMensuel",
            "Agenda_Jours_Non_Disponible", // Était JourNonDisponible
            "Patient",
            "Antecedents",              // Était Antecedent (Pluriel dans votre schéma)
            "RDV",
            "Consultation",
            "InterventionMédecin",      // Attention à l'accent dans votre schéma
            "Acte",
            "Acte_Intervention",        // Ajouté
            "Certificat",
            "Médicament",               // Attention à l'accent
            "Ordonnance",
            "Prescription",
            "Facture",
            "SituationFinanciere",
            "Revenue",
            "Charge"
    };

    public void exportAllDataToCSV(String directoryPath) throws Exception {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            for (String tableName : TABLES) {
                try {
                    exportTable(conn, tableName, directoryPath);
                } catch (SQLException e) {
                    System.err.println("⚠️ Attention : Impossible d'exporter la table " + tableName + " (" + e.getMessage() + ")");
                    // On continue pour les autres tables même si une échoue
                }
            }
        }
    }

    private void exportTable(Connection conn, String tableName, String directoryPath) throws SQLException, IOException {
        String filename = directoryPath + "/" + tableName + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

        // Utilisation des guillemets pour gérer la casse et les accents (ex: "Médicament")
        String sql = "SELECT * FROM \"" + tableName + "\"";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             FileWriter csvWriter = new FileWriter(filename)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 1. En-tête
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // 2. Données
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value != null) {
                        value = value.replace("\"", "\"\"");
                        if (value.contains(",") || value.contains("\n")) {
                            value = "\"" + value + "\"";
                        }
                        csvWriter.append(value);
                    } else {
                        csvWriter.append("");
                    }

                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }
            System.out.println("✅ Exporté : " + filename);
        }
    }
}