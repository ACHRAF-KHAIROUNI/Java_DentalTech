package ma.dentalTech.mvc.ui.modules.rdvPatient;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.userManager.Medecin; // Assure-toi que l'import est bon
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.api.RDVController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//ROBAII IKRAM
public class RDVFormDialog extends JDialog {

    private final RDVController controller;
    private final RDV rdvActuel;
    private boolean isSaved = false;

    // --- DÉCLARATION DES COMPOSANTS (C'est ici qu'il manquait cmbMedecin) ---
    private JComboBox<Patient> cmbPatient;
    private JComboBox<Medecin> cmbMedecin; // <--- CORRECTION ICI
    private JTextField txtDate;
    private JComboBox<String> cmbHeure;
    private JTextArea txtMotif;
    private JButton btnSave, btnCancel;

    public RDVFormDialog(Window parent, RDVController controller, RDV rdvAModifier) {
        super(parent, rdvAModifier == null ? "Nouveau Rendez-vous" : "Modifier Rendez-vous",
                ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.rdvActuel = rdvAModifier;

        initUI();
        remplirDonnees();

        setSize(450, 500); // Un peu plus grand pour contenir le médecin
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // 6 lignes
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Patient
        formPanel.add(new JLabel("Patient :"));
        cmbPatient = new JComboBox<>();
        List<Patient> patients = controller.getTousLesPatients();
        for (Patient p : patients)
            cmbPatient.addItem(p);
        formPanel.add(cmbPatient);

        // 2. Médecin (Ajouté)
        formPanel.add(new JLabel("Médecin :"));
        cmbMedecin = new JComboBox<>();
        List<Medecin> medecins = controller.getTousLesMedecins();
        for (Medecin m : medecins)
            cmbMedecin.addItem(m);
        formPanel.add(cmbMedecin);

        // 3. Date
        formPanel.add(new JLabel("Date (YYYY-MM-DD) :"));
        txtDate = new JTextField(LocalDate.now().plusDays(1).toString());
        formPanel.add(txtDate);

        // 4. Heure
        formPanel.add(new JLabel("Heure :"));
        cmbHeure = new JComboBox<>();
        for (int h = 8; h <= 18; h++) {
            cmbHeure.addItem(String.format("%02d:00", h));
            cmbHeure.addItem(String.format("%02d:30", h));
        }
        formPanel.add(cmbHeure);

        // 5. Motif
        formPanel.add(new JLabel("Motif :"));
        txtMotif = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(txtMotif));

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(144, 238, 144));
        btnCancel = new JButton("Annuler");

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> enregistrer());
    }

    private void remplirDonnees() {
        if (rdvActuel != null) {
            // Sélection Patient
            for (int i = 0; i < cmbPatient.getItemCount(); i++) {
                if (cmbPatient.getItemAt(i).getIdPatient().equals(rdvActuel.getIdPatient())) {
                    cmbPatient.setSelectedIndex(i);
                    break;
                }
            }
            // Sélection Médecin
            for (int i = 0; i < cmbMedecin.getItemCount(); i++) {
                Long idMedecinCombo = cmbMedecin.getItemAt(i).getIdMedecin();
                Long idMedecinRdv = rdvActuel.getIdMedecin();

                if (idMedecinCombo != null && idMedecinCombo.equals(idMedecinRdv)) {
                    cmbMedecin.setSelectedIndex(i);
                    break;
                }
            }

            txtDate.setText(rdvActuel.getDate().toString());
            String heureStr = String.format("%02d:%02d", rdvActuel.getHeure().getHour(),
                    rdvActuel.getHeure().getMinute());
            cmbHeure.setSelectedItem(heureStr);
            txtMotif.setText(rdvActuel.getMotif());
        }
    }

    // Méthode pour pré-remplir depuis le calendrier (Clic sur "+")
    public void preRemplirDateHeure(LocalDate date, LocalTime heure) {
        txtDate.setText(date.toString());
        String heureStr = String.format("%02d:%02d", heure.getHour(), heure.getMinute());
        cmbHeure.setSelectedItem(heureStr);
    }

    private void enregistrer() {
        try {
            Patient patient = (Patient) cmbPatient.getSelectedItem();
            Medecin medecin = (Medecin) cmbMedecin.getSelectedItem();

            if (medecin == null) {
                throw new Exception("Veuillez sélectionner un médecin.");
            }
            if (patient == null) {
                throw new Exception("Veuillez sélectionner un patient.");
            }

            LocalDate date = LocalDate.parse(txtDate.getText());
            LocalTime heure = LocalTime.parse((String) cmbHeure.getSelectedItem());
            String motif = txtMotif.getText();

            if (rdvActuel == null) {
                // Création
                RDV nouveauRdv = RDV.builder()
                        .idPatient(patient.getIdPatient())
                        .idMedecin(medecin.getIdMedecin())
                        .date(date)
                        .heure(heure)
                        .motif(motif)
                        .build();
                controller.enregistrerNouveauRDV(nouveauRdv);
            } else {
                // Modification
                rdvActuel.setDate(date);
                rdvActuel.setHeure(heure);
                rdvActuel.setMotif(motif);
                rdvActuel.setIdMedecin(medecin.getIdMedecin()); // On permet de changer le médecin
                controller.modifierRDV(rdvActuel);
            }

            isSaved = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return isSaved;
    }
}