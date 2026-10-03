-- Socle Technique & Administratif

INSERT INTO Cabinet_Medical (idCabinet, nom, email, logo, adresse, cin, tel1, tel2, siteWeb, instagram, facebook, description, dateCreation)
VALUES (1, 'Cabinet Medical Al-Chifa', 'contact@alchifa.com', 'logo.png', '123 Bd Mohamed V, Casablanca', 'CIN123456', '0522000000', '0661000000', 'www.alchifa.ma', '@alchifa_med', 'fb.com/alchifa', 'Cabinet multidisciplinaire moderne', '2023-01-01')
    ON CONFLICT (idCabinet) DO NOTHING;

INSERT INTO Role (idRole, libelle) VALUES (1, 'ADMIN') ON CONFLICT (idRole) DO NOTHING;
INSERT INTO Role (idRole, libelle) VALUES (2, 'MEDECIN') ON CONFLICT (idRole) DO NOTHING;
INSERT INTO Role (idRole, libelle) VALUES (3, 'SECRETAIRE') ON CONFLICT (idRole) DO NOTHING;

INSERT INTO Role_Privileges (idRole, privilege)
SELECT 1, 'GESTION_UTILISATEURS' WHERE NOT EXISTS (SELECT 1 FROM Role_Privileges WHERE idRole = 1 AND privilege = 'GESTION_UTILISATEURS');
INSERT INTO Role_Privileges (idRole, privilege)
SELECT 2, 'GESTION_PATIENTS' WHERE NOT EXISTS (SELECT 1 FROM Role_Privileges WHERE idRole = 2 AND privilege = 'GESTION_PATIENTS');
INSERT INTO Role_Privileges (idRole, privilege)
SELECT 3, 'GESTION_RDV' WHERE NOT EXISTS (SELECT 1 FROM Role_Privileges WHERE idRole = 3 AND privilege = 'GESTION_RDV');

INSERT INTO Utilisateur (idUser, nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate, dateNaissance, dateRecrutement, token, idCabinet, idRole, dateCreation)
VALUES (1, 'Mr. Administrateur', 'admin@alchifa.com', 'Casa Anfa', 'AB1001', '0600112233', 'HOMME', 'admin', 'password123', '2023-10-25', '1980-05-15', '2023-01-01', 'token_admin_xyz', 1, 1, '2023-01-01')
    ON CONFLICT (idUser) DO NOTHING;

INSERT INTO Utilisateur (idUser, nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate, dateNaissance, dateRecrutement, token, idCabinet, idRole, dateCreation)
VALUES (2, 'Dr. Karim Tazi', 'karim.tazi@alchifa.com', 'Rabat Agdal', 'RB2002', '0661112233', 'HOMME', 'tazi', 'password123', '2023-10-26', '1975-03-10', '2023-01-05', 'token_med_xyz', 1, 2, '2023-01-05')
    ON CONFLICT (idUser) DO NOTHING;

INSERT INTO Utilisateur (idUser, nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate, dateNaissance, dateRecrutement, token, idCabinet, idRole, dateCreation)
VALUES (3, 'Mlle. Salma', 'salma@alchifa.com', 'Casa Maarif', 'BH3003', '0662223344', 'FEMME', 'salma', 'password123', '2023-10-26', '1995-07-20', '2023-02-01', 'token_sec_xyz', 1, 3, '2023-02-01')
    ON CONFLICT (idUser) DO NOTHING;

INSERT INTO Notification (idNotification, titre, message, date, time, type, priorite, idUser)
VALUES (1, 'Rappel Réunion', 'Réunion mensuelle du staff', '2023-10-30', '09:00:00', 'INFO', 'MOYENNE', 2)
    ON CONFLICT (idNotification) DO NOTHING;

INSERT INTO Statistique (idStatistique, nom, categorie, chiffre, dateCalcul, idCabinet)
VALUES (1, 'Chiffre Affaire Octobre', 'FINANCE', 15000.00, '2023-10-26', 1)
    ON CONFLICT (idStatistique) DO NOTHING;

-- Ressources Humaines

INSERT INTO Admin (idAdmin, idUser) VALUES (1, 1) ON CONFLICT (idAdmin) DO NOTHING;

INSERT INTO Medecin (idMedecin, specialite, idUser) VALUES (1, 'Cardiologie', 2) ON CONFLICT (idMedecin) DO NOTHING;

INSERT INTO Secretaire (idSecretaire, numCNSS, comission, idUser) VALUES (1, 'CNSS-987654', 2.5, 3) ON CONFLICT (idSecretaire) DO NOTHING;

INSERT INTO Staff (idStaff, salaire, prime, soldConge, etat, idUser) VALUES (1, 15000.00, 1000.00, 20, true, 2) ON CONFLICT (idStaff) DO NOTHING;
INSERT INTO Staff (idStaff, salaire, prime, soldConge, etat, idUser) VALUES (2, 4500.00, 200.00, 15, true, 3) ON CONFLICT (idStaff) DO NOTHING;

INSERT INTO AgendaMensuel (idAgenda, mois, etat, idMedecin) VALUES (1, 'NOVEMBRE', true, 1) ON CONFLICT (idAgenda) DO NOTHING;

INSERT INTO Agenda_Jours_Non_Disponible (idAgenda, jour)
SELECT 1, '2023-11-01' WHERE NOT EXISTS (SELECT 1 FROM Agenda_Jours_Non_Disponible WHERE idAgenda = 1 AND jour = '2023-11-01');

-- Dossier Patient & Médical

INSERT INTO Patient (idPatient, nom, dateDeNaissance, sexe, adresse, telephone, assurance, dateCreation)
VALUES (1, 'Ahmed Benani', '1960-01-01', 'HOMME', 'Hay Mohammadi, Casa', '0663334455', 'CNSS', '2023-10-20')
    ON CONFLICT (idPatient) DO NOTHING;

INSERT INTO Patient (idPatient, nom, dateDeNaissance, sexe, adresse, telephone, assurance, dateCreation)
VALUES (2, 'Fatima Zahra', '1985-06-15', 'FEMME', 'Oulfa, Casa', '0664445566', 'CNOPS', '2023-10-22')
    ON CONFLICT (idPatient) DO NOTHING;

INSERT INTO Antecedents (idAntecedent, nom, catégorie, niveauDeRisque, idPatient)
VALUES (1, 'Hypertension', 'Cardiovasculaire', 'ELEVE', 1)
    ON CONFLICT (idAntecedent) DO NOTHING;

INSERT INTO Acte (idActe, libellé, catégorie, prixDeBase, description, code)
VALUES (1, 'Consultation Généraliste', 'CONSULTATION', 200.00, 'Consultation standard', 101)
    ON CONFLICT (idActe) DO NOTHING;

INSERT INTO Acte (idActe, libellé, catégorie, prixDeBase, description, code)
VALUES (2, 'Consultation Spécialiste', 'CONSULTATION', 300.00, 'Consultation spécialiste', 102)
    ON CONFLICT (idActe) DO NOTHING;

INSERT INTO Acte (idActe, libellé, catégorie, prixDeBase, description, code)
VALUES (3, 'ECG', 'TECHNIQUE', 150.00, 'Electrocardiogramme', 201)
    ON CONFLICT (idActe) DO NOTHING;

INSERT INTO RDV (idRDV, date, heure, motif, statut, noteMedecin, idPatient, idMedecin, idSecretaire)
VALUES (1, '2023-10-27', '10:30:00', 'Douleur thoracique', 'TERMINÉ', 'Patient anxieux', 1, 1, 1)
    ON CONFLICT (idRDV) DO NOTHING;

INSERT INTO Consultation (idConsultation, date, statut, observationMedecin, idRDV)
VALUES (1, '2023-10-27', 'TERMINÉ', 'Tension artérielle élevée 16/9. Nécessite repos.', 1)
    ON CONFLICT (idConsultation) DO NOTHING;

INSERT INTO InterventionMédecin (idIM, prixDePatient, idConsultation, idMedecin)
VALUES (1, 450.00, 1, 1)
    ON CONFLICT (idIM) DO NOTHING;

INSERT INTO Acte_Intervention (idActeIntervention, idActe, idIM) VALUES (1, 2, 1) ON CONFLICT (idActeIntervention) DO NOTHING;
INSERT INTO Acte_Intervention (idActeIntervention, idActe, idIM) VALUES (2, 3, 1) ON CONFLICT (idActeIntervention) DO NOTHING;

INSERT INTO Certificat (idCertif, dateDébut, dateFin, durée, noteMedecin, idPatient, idMedecin)
VALUES (1, '2023-10-27', '2023-10-30', 3, 'Repos maladie 3 jours', 1, 1)
    ON CONFLICT (idCertif) DO NOTHING;

-- Pharmacie

INSERT INTO Médicament (idMct, nom, laboratoire, type, forme, remboursable, prixUnitaire, description)
VALUES (1, 'Doliprane 1000mg', 'Sanofi', 'Antalgique', 'COMPRIME', true, 15.00, 'Paracétamol')
    ON CONFLICT (idMct) DO NOTHING;

INSERT INTO Médicament (idMct, nom, laboratoire, type, forme, remboursable, prixUnitaire, description)
VALUES (2, 'Loxen 50mg', 'Novartis', 'Anti-hypertenseur', 'GELULE', true, 80.00, 'Nicardipine')
    ON CONFLICT (idMct) DO NOTHING;

INSERT INTO Ordonnance (idOrd, date, idPatient, idMedecin)
VALUES (1, '2023-10-27', 1, 1)
    ON CONFLICT (idOrd) DO NOTHING;

INSERT INTO Prescription (idPr, quantité, fréquence, duréeEnJours, idOrd, idMct)
VALUES (1, 2, '1 matin, 1 soir', 5, 1, 1)
    ON CONFLICT (idPr) DO NOTHING;

INSERT INTO Prescription (idPr, quantité, fréquence, duréeEnJours, idOrd, idMct)
VALUES (2, 1, '1 matin', 30, 1, 2)
    ON CONFLICT (idPr) DO NOTHING;

-- Finances

INSERT INTO Facture (idFacture, totaleFacture, totalePayé, reste, statut, dateFacture, idConsultation)
VALUES (1, 450.00, 450.00, 0.00, 'PAYÉE', '2023-10-27 11:00:00', 1)
    ON CONFLICT (idFacture) DO NOTHING;

INSERT INTO SituationFinanciere (idSF, totaleDesActes, totalePayé, crédit, statut, enPromo, idFacture)
VALUES (1, 450.00, 450.00, 0.00, 'CLOTURÉ', 'NON', 1)
    ON CONFLICT (idSF) DO NOTHING;

INSERT INTO Revenue (idRevenue, titre, description, montant, date, idCabinet)
VALUES (1, 'Consultation Ahmed', 'Paiement Espèce', 450.00, '2023-10-27 11:05:00', 1)
    ON CONFLICT (idRevenue) DO NOTHING;

INSERT INTO Charge (idCharge, titre, description, montant, date, idCabinet)
VALUES (1, 'Facture Electricité', 'Lydec Octobre', 1200.00, '2023-10-25 09:00:00', 1)
    ON CONFLICT (idCharge) DO NOTHING;

INSERT INTO Charge (idCharge, titre, description, montant, date, idCabinet)
VALUES (2, 'Achat Fournitures', 'Papier, Stylos', 150.00, '2023-10-26 10:00:00', 1)
    ON CONFLICT (idCharge) DO NOTHING;





-- 1. Socle Technique & Administratif
SELECT setval(pg_get_serial_sequence('cabinet_medical', 'idcabinet'), COALESCE((SELECT MAX(idcabinet) FROM cabinet_medical), 1));
SELECT setval(pg_get_serial_sequence('role', 'idrole'), COALESCE((SELECT MAX(idrole) FROM role), 1));
SELECT setval(pg_get_serial_sequence('utilisateur', 'iduser'), COALESCE((SELECT MAX(iduser) FROM utilisateur), 1));
SELECT setval(pg_get_serial_sequence('notification', 'idnotification'), COALESCE((SELECT MAX(idnotification) FROM notification), 1));
SELECT setval(pg_get_serial_sequence('statistique', 'idstatistique'), COALESCE((SELECT MAX(idstatistique) FROM statistique), 1));

-- 2. Ressources Humaines
SELECT setval(pg_get_serial_sequence('admin', 'idadmin'), COALESCE((SELECT MAX(idadmin) FROM admin), 1));
SELECT setval(pg_get_serial_sequence('staff', 'idstaff'), COALESCE((SELECT MAX(idstaff) FROM staff), 1));
SELECT setval(pg_get_serial_sequence('medecin', 'idmedecin'), COALESCE((SELECT MAX(idmedecin) FROM medecin), 1));
SELECT setval(pg_get_serial_sequence('secretaire', 'idsecretaire'), COALESCE((SELECT MAX(idsecretaire) FROM secretaire), 1));
SELECT setval(pg_get_serial_sequence('agendamensuel', 'idagenda'), COALESCE((SELECT MAX(idagenda) FROM agendamensuel), 1));

-- 3. Dossier Patient & Médical
SELECT setval(pg_get_serial_sequence('patient', 'idpatient'), COALESCE((SELECT MAX(idpatient) FROM patient), 1));
SELECT setval(pg_get_serial_sequence('antecedents', 'idantecedent'), COALESCE((SELECT MAX(idantecedent) FROM antecedents), 1));
SELECT setval(pg_get_serial_sequence('rdv', 'idrdv'), COALESCE((SELECT MAX(idrdv) FROM rdv), 1));
SELECT setval(pg_get_serial_sequence('consultation', 'idconsultation'), COALESCE((SELECT MAX(idconsultation) FROM consultation), 1));
SELECT setval(pg_get_serial_sequence('interventionmédecin', 'idim'), COALESCE((SELECT MAX(idim) FROM interventionmédecin), 1));
SELECT setval(pg_get_serial_sequence('acte', 'idacte'), COALESCE((SELECT MAX(idacte) FROM acte), 1));
SELECT setval(pg_get_serial_sequence('acte_intervention', 'idacteintervention'), COALESCE((SELECT MAX(idacteintervention) FROM acte_intervention), 1));
SELECT setval(pg_get_serial_sequence('certificat', 'idcertif'), COALESCE((SELECT MAX(idcertif) FROM certificat), 1));

-- 4. Pharmacie
SELECT setval(pg_get_serial_sequence('médicament', 'idmct'), COALESCE((SELECT MAX(idmct) FROM médicament), 1));
SELECT setval(pg_get_serial_sequence('ordonnance', 'idord'), COALESCE((SELECT MAX(idord) FROM ordannance), 1));
SELECT setval(pg_get_serial_sequence('prescription', 'idpr'), COALESCE((SELECT MAX(idpr) FROM prescription), 1));

-- 5. Finances
SELECT setval(pg_get_serial_sequence('facture', 'idfacture'), COALESCE((SELECT MAX(idfacture) FROM facture), 1));
SELECT setval(pg_get_serial_sequence('situationfinanciere', 'idsf'), COALESCE((SELECT MAX(idsf) FROM situationfinanciere), 1));
SELECT setval(pg_get_serial_sequence('revenue', 'idrevenue'), COALESCE((SELECT MAX(idrevenue) FROM revenue), 1));
SELECT setval(pg_get_serial_sequence('charge', 'idcharge'), COALESCE((SELECT MAX(idcharge) FROM charge), 1));