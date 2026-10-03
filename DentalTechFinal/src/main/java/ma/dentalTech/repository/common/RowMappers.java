package ma.dentalTech.repository.common;

import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.caisse.*;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.entities.userManager.Role;
import ma.dentalTech.entities.userManager.Utilisateur;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class RowMappers {

    private RowMappers(){}

    // --- PATIENT ---
    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setIdPatient(rs.getLong("idPatient"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAdresse(rs.getString("adresse"));
        p.setTelephone(rs.getString("telephone"));

        if (rs.getDate("dateDeNaissance") != null)
            p.setDateDeNaissance(rs.getDate("dateDeNaissance").toLocalDate());

        // --- CORRECTION ROBUSTE POUR LE SEXE ---
        String sexeDb = rs.getString("sexe");
        if (sexeDb != null) {
            // Nettoyage (supprime espaces et met en majuscule pour comparer)
            String sexeClean = sexeDb.trim().toUpperCase();

            if (sexeClean.equals("F") || sexeClean.equals("FEMME")) {
                p.setSexe(Sexe.FEMME);
            } else if (sexeClean.equals("H") || sexeClean.equals("M") || sexeClean.equals("HOMME")) {
                p.setSexe(Sexe.HOMME);
            } else {
                // Si c'est une autre valeur, on essaie le mapping par défaut
                try {
                    // Attention à la casse de votre Enum (Homme/Femme ou HOMME/FEMME)
                    // Ici on tente tel quel, sinon on laisse null pour ne pas crasher
                    p.setSexe(Sexe.valueOf(sexeDb));
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ Attention : Valeur de sexe inconnue en BDD ignorée : " + sexeDb);
                    p.setSexe(null); // On évite le crash de l'appli
                }
            }
        }
        // ---------------------------------------

        // Faire de même pour l'Assurance si nécessaire (souvent source d'erreur aussi)
        String assuranceDb = rs.getString("assurance");
        if (assuranceDb != null) {
            try {
                p.setAssurance(Assurance.valueOf(assuranceDb));
            } catch (IllegalArgumentException e) {
                p.setAssurance(Assurance.Aucune); // Valeur par défaut
                System.err.println("⚠️ Assurance inconnue : " + assuranceDb);
            }
        }

        if (rs.getDate("dateCreation") != null)
            p.setDateCreation(rs.getDate("dateCreation").toLocalDate());

        return p;
    }
    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();

        // 1. ID : On essaie le nom minuscule (Postgres) ou CamelCase au cas où
        try {
            a.setIdAntecedent(rs.getLong("idantecedent")); // Nom vu sur ta capture
        } catch (SQLException e) {
            a.setIdAntecedent(rs.getLong("idAntecedent"));
        }

        a.setNom(rs.getString("nom"));

        // 2. CATÉGORIE : Gestion robuste (MEDICAL -> AUTRE)
        String catDb = rs.getString("catégorie"); // Attention à l'accent
        if (catDb != null) {
            try {
                a.setCategorie(CategorieAntecedent.valueOf(catDb));
            } catch (IllegalArgumentException e) {
                a.setCategorie(CategorieAntecedent.AUTRE); // Fallback pour éviter le crash
            }
        }

        // 3. NIVEAU RISQUE : Nom colonne minuscule "niveauderisque"
        String nivDb = null;
        try {
            nivDb = rs.getString("niveauderisque"); // Nom vu sur ta capture
        } catch (SQLException e) {
            try { nivDb = rs.getString("niveauDeRisque"); } catch (SQLException ex) {}
        }

        if (nivDb != null) {
            try {
                a.setNiveauRisque(NiveauRisque.valueOf(nivDb));
            } catch (IllegalArgumentException e) {
                a.setNiveauRisque(NiveauRisque.FAIBLE);
            }
        }

        // 4. FK PATIENT : Nom colonne minuscule "idpatient"
        try {
            a.setIdPatient(rs.getLong("idpatient"));
        } catch (SQLException e) {
            a.setIdPatient(rs.getLong("idPatient"));
        }

        return a;
    }


    // --- USER MANAGER ---
    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setIdUser(rs.getLong("idUser"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("motDePasse"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));
        u.setAdresse(rs.getString("adresse")); // AJOUT DE L'ADRESSE

        // --- GESTION DU SEXE ---
        String sexeDb = rs.getString("sexe");
        if (sexeDb != null) {
            String sexeClean = sexeDb.trim().toUpperCase();
            if (sexeClean.equals("F") || sexeClean.equals("FEMME")) u.setSexe(Sexe.FEMME);
            else if (sexeClean.equals("H") || sexeClean.equals("M") || sexeClean.equals("HOMME")) u.setSexe(Sexe.HOMME);
            else {
                try { u.setSexe(Sexe.valueOf(sexeClean)); }
                catch (IllegalArgumentException e) { u.setSexe(null); }
            }
        }

        u.setIdRole(rs.getLong("idRole"));
        u.setIdCabinet(rs.getLong("idCabinet"));
        return u;
    }

    // --- MEDECIN (Avec Jointure Utilisateur) ---
    public static ma.dentalTech.entities.userManager.Medecin mapMedecin(ResultSet rs) throws SQLException {
        ma.dentalTech.entities.userManager.Medecin m = new ma.dentalTech.entities.userManager.Medecin();
        m.setIdMedecin(rs.getLong("idMedecin"));
        m.setSpecialite(rs.getString("specialite"));
        m.setIdUser(rs.getLong("idUser")); // Clé étrangère

        // On map l'utilisateur associé (les colonnes sont présentes grâce au JOIN)
        m.setUtilisateur(mapUtilisateur(rs));

        return m;
    }

    // --- DOSSIER MEDICAL ---
    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV r = new RDV();
        r.setIdRDV(rs.getLong("idRDV"));
        if(rs.getDate("date") != null) r.setDate(rs.getDate("date").toLocalDate());
        if(rs.getTime("heure") != null) r.setHeure(rs.getTime("heure").toLocalTime());
        r.setMotif(rs.getString("motif"));
        if(rs.getString("statut") != null) r.setStatut(StatutRDV.valueOf(rs.getString("statut"))); // Assure-toi que l'Enum existe ou utilise String
        r.setNoteMedecin(rs.getString("noteMedecin"));
        r.setIdPatient(rs.getLong("idPatient"));
        r.setIdMedecin(rs.getLong("idMedecin"));
        return r;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setIdConsultation(rs.getLong("idConsultation"));
        if(rs.getDate("date") != null) c.setDate(rs.getDate("date").toLocalDate());
        c.setStatut(rs.getString("statut"));
        c.setObservationMedecin(rs.getString("observationMedecin"));
        c.setIdRDV(rs.getLong("idRDV"));
        return c;
    }

    // --- ACTES ---
    public static Acte mapActe(ResultSet rs) throws SQLException {
        return Acte.builder()
                .idActe(rs.getLong("idActe"))
                .libelle(rs.getString("libellé"))
                .categorie(rs.getString("catégorie"))
                .prixDeBase(rs.getDouble("prixDeBase"))
                .code(rs.getInt("code"))
                .description(rs.getString("description"))
                .build();
    }

    // --- PHARMACIE ---
    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        return Medicament.builder()
                .idMct(rs.getLong("idMct"))
                .nom(rs.getString("nom"))
                .laboratoire(rs.getString("laboratoire"))
                .type(rs.getString("type"))
                .forme(rs.getString("forme"))
                .remboursable(rs.getBoolean("remboursable"))
                .prixUnitaire(rs.getDouble("prixUnitaire"))
                .description(rs.getString("description"))
                .build();
    }

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();
        o.setIdOrd(rs.getLong("idOrd"));
        if(rs.getDate("date") != null) o.setDate(rs.getDate("date").toLocalDate());
        o.setIdPatient(rs.getLong("idPatient"));
        o.setIdMedecin(rs.getLong("idMedecin"));
        return o;
    }

    public static Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getLong("idFacture"));
        f.setTotaleFacture(rs.getDouble("totaleFacture"));
        // Try/Catch pour compatibilité accents
        try { f.setTotalePaye(rs.getDouble("totalePayé")); } catch(SQLException e) { f.setTotalePaye(rs.getDouble("totalepayé")); }
        f.setReste(rs.getDouble("reste"));
        f.setStatut(rs.getString("statut"));
        if(rs.getTimestamp("dateFacture") != null)
            f.setDateFacture(rs.getTimestamp("dateFacture").toLocalDateTime());
        f.setIdConsultation(rs.getLong("idConsultation"));
        return f;
    }

    public static SituationFinanciere mapSituation(ResultSet rs) throws SQLException {
        SituationFinanciere sf = new SituationFinanciere();
        sf.setIdSF(rs.getLong("idSF"));
        sf.setTotaleDesActes(rs.getDouble("totaleDesActes"));
        try { sf.setTotalePaye(rs.getDouble("totalePayé")); } catch(SQLException e) { sf.setTotalePaye(rs.getDouble("totalepayé")); }
        try { sf.setCredit(rs.getDouble("crédit")); } catch(SQLException e) { sf.setCredit(rs.getDouble("credit")); }
        sf.setStatut(rs.getString("statut"));
        sf.setEnPromo(rs.getString("enPromo"));
        sf.setIdFacture(rs.getLong("idFacture"));
        return sf;
    }

    public static Revenue mapRevenue(ResultSet rs) throws SQLException {
        Revenue r = new Revenue();
        r.setIdRevenue(rs.getLong("idRevenue"));
        r.setTitre(rs.getString("titre"));
        r.setDescription(rs.getString("description"));
        r.setMontant(rs.getDouble("montant"));
        if(rs.getTimestamp("date") != null) r.setDate(rs.getTimestamp("date").toLocalDateTime());
        r.setIdCabinet(rs.getLong("idCabinet"));
        return r;
    }

    public static Charge mapCharge(ResultSet rs) throws SQLException {
        Charge c = new Charge();
        c.setIdCharge(rs.getLong("idCharge"));
        c.setTitre(rs.getString("titre"));
        c.setDescription(rs.getString("description"));
        c.setMontant(rs.getDouble("montant"));
        if(rs.getTimestamp("date") != null) c.setDate(rs.getTimestamp("date").toLocalDateTime());
        c.setIdCabinet(rs.getLong("idCabinet"));
        return c;
    }

    public static Statistique mapStatistique(ResultSet rs) throws SQLException {
        Statistique s = new Statistique();
        s.setIdStatistique(rs.getLong("idStatistique"));
        s.setNom(rs.getString("nom"));
        s.setCategorie(rs.getString("categorie"));
        s.setChiffre(rs.getDouble("chiffre"));
        if(rs.getDate("dateCalcul") != null) s.setDateCalcul(rs.getDate("dateCalcul").toLocalDate());
        return s;
    }

    // --- CERTIFICAT ---
    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat c = new Certificat();
        c.setIdCertif(rs.getLong("idCertif"));
        if(rs.getDate("dateDébut") != null) c.setDateDebut(rs.getDate("dateDébut").toLocalDate());
        if(rs.getDate("dateFin") != null) c.setDateFin(rs.getDate("dateFin").toLocalDate());
        c.setDuree(rs.getInt("durée"));
        c.setNoteMedecin(rs.getString("noteMedecin"));
        c.setIdPatient(rs.getLong("idPatient"));
        c.setIdMedecin(rs.getLong("idMedecin"));
        return c;
    }

    // --- AGENDA ---
    public static AgendaMensuel mapAgenda(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();
        a.setIdAgenda(rs.getLong("idAgenda"));
        a.setMois(rs.getString("mois"));
        a.setEtat(rs.getBoolean("etat"));
        a.setIdMedecin(rs.getLong("idMedecin"));
        return a;
    }

    // --- ROLE ---
    public static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setIdRole(rs.getLong("idRole"));
        r.setLibelle(rs.getString("libelle"));
        return r;
    }

    // --- LIAISONS (Prescription & Intervention) ---
    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setIdPr(rs.getLong("idPr"));
        p.setQuantite(rs.getInt("quantité")); // Attention accent
        p.setFrequence(rs.getString("fréquence")); // Attention accent
        p.setDureeEnJours(rs.getInt("duréeEnJours")); // Attention accent
        p.setIdOrd(rs.getLong("idOrd"));
        p.setIdMct(rs.getLong("idMct"));
        return p;
    }

    public static InterventionMedecin mapIntervention(ResultSet rs) throws SQLException {
        InterventionMedecin im = new InterventionMedecin();
        im.setIdIM(rs.getLong("idIM"));
        im.setPrixDePatient(rs.getDouble("prixDePatient"));
        im.setIdConsultation(rs.getLong("idConsultation"));
        im.setIdMedecin(rs.getLong("idMedecin"));
        return im;
    }

}