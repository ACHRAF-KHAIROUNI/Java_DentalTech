package ma.dentalTech.mvc.ui.modules.ordonnance;

import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.ordonnance.api.OrdonnanceController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//ROBAII IKRAM
public class OrdonnanceFormDialog extends JDialog {

    private final OrdonnanceController controller;
    private boolean isSaved = false;

    // Données temporaires
    private final List<Prescription> prescriptionsList = new ArrayList<>();

    // Composants UI
    private JComboBox<Patient> cmbPatient;
    private JComboBox<Medicament> cmbMedicament;
    private JSpinner spnQuantite, spnDuree;
    private JTextField txtFrequence;
    private JTable tableRecap;
    private DefaultTableModel modelRecap;

    public OrdonnanceFormDialog(Window parent, OrdonnanceController controller) {
        super(parent, "Nouvelle Ordonnance", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setSize(900, 600);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // === HEADER : Info Patient ===
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(230, 240, 255)); // Bleu très pâle

        JLabel lblDate = new JLabel("Date : " + LocalDate.now());
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 14));

        cmbPatient = new JComboBox<>();
        cmbPatient.setPreferredSize(new Dimension(250, 30));
        chargerPatients();

        headerPanel.add(lblDate);
        headerPanel.add(new JLabel("   |   Patient :"));
        headerPanel.add(cmbPatient);

        add(headerPanel, BorderLayout.NORTH);

        // === CENTER : Split (Saisie vs Tableau) ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350); // Taille panneau gauche
        splitPane.setResizeWeight(0.0);

        // --- GAUCHE : Formulaire Ajout Médicament ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(null, "Ajouter un Médicament", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 12), Color.BLUE));
        leftPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Médicament
        leftPanel.add(new JLabel("Médicament :"), gbc);
        gbc.gridy++;
        cmbMedicament = new JComboBox<>(); // Autocomplétion idéale ici
        chargerMedicaments();
        leftPanel.add(cmbMedicament, gbc);

        // Quantité
        gbc.gridy++;
        leftPanel.add(new JLabel("Quantité (boîtes) :"), gbc);
        gbc.gridy++;
        spnQuantite = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        leftPanel.add(spnQuantite, gbc);

        // Fréquence
        gbc.gridy++;
        leftPanel.add(new JLabel("Fréquence (ex: Matin, Soir) :"), gbc);
        gbc.gridy++;
        txtFrequence = new JTextField("3 fois par jour");
        leftPanel.add(txtFrequence, gbc);

        // Durée
        gbc.gridy++;
        leftPanel.add(new JLabel("Durée (Jours) :"), gbc);
        gbc.gridy++;
        spnDuree = new JSpinner(new SpinnerNumberModel(5, 1, 90, 1));
        leftPanel.add(spnDuree, gbc);

        // Bouton Ajouter Ligne
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton btnAddLine = new JButton("⬇️ Ajouter à la liste");
        btnAddLine.setBackground(new Color(240, 248, 255));
        btnAddLine.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAddLine.addActionListener(e -> ajouterLigne());
        leftPanel.add(btnAddLine, gbc);

        // Pousser vers le haut
        gbc.gridy++; gbc.weighty = 1.0;
        leftPanel.add(new JLabel(), gbc);

        splitPane.setLeftComponent(leftPanel);

        // --- DROITE : Tableau Récapitulatif ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Récapitulatif de l'Ordonnance"));

        String[] cols = {"Médicament", "Qté", "Fréquence", "Durée"};
        modelRecap = new DefaultTableModel(cols, 0);
        tableRecap = new JTable(modelRecap);
        tableRecap.setRowHeight(25);
        tableRecap.setShowGrid(false);
        tableRecap.setIntercellSpacing(new Dimension(0, 0));

        // Bouton supprimer ligne (petit bouton rouge en bas du tableau)
        JButton btnRemoveLine = new JButton("Supprimer la ligne sélectionnée");
        btnRemoveLine.setForeground(Color.RED);
        btnRemoveLine.addActionListener(e -> retirerLigne());

        rightPanel.add(new JScrollPane(tableRecap), BorderLayout.CENTER);
        rightPanel.add(btnRemoveLine, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // === FOOTER : Actions Globales ===
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footerPanel.setBackground(new Color(245, 245, 245));

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());

        JButton btnValider = new JButton("✅ VALIDER & IMPRIMER");
        btnValider.setBackground(new Color(40, 167, 69)); // Vert Bootstrap
        btnValider.setForeground(Color.WHITE);
        btnValider.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnValider.setPreferredSize(new Dimension(200, 40));
        btnValider.addActionListener(e -> validerOrdonnance());

        footerPanel.add(btnCancel);
        footerPanel.add(btnValider);

        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- LOGIQUE ---

    private void chargerPatients() {
        for (Patient p : controller.getTousLesPatients()) {
            cmbPatient.addItem(p);
        }
    }

    private void chargerMedicaments() {
        for (Medicament m : controller.getTousLesMedicaments()) {
            cmbMedicament.addItem(m);
        }
    }

    private void ajouterLigne() {
        Medicament m = (Medicament) cmbMedicament.getSelectedItem();
        if (m == null) return;

        int qte = (int) spnQuantite.getValue();
        String freq = txtFrequence.getText();
        int duree = (int) spnDuree.getValue();

        if (freq.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir une fréquence.");
            return;
        }

        // Création objet temporaire
        Prescription p = Prescription.builder()
                .idMct(m.getIdMct())
                .quantite(qte)
                .frequence(freq)
                .dureeEnJours(duree)
                .build();

        prescriptionsList.add(p);

        // Ajout visuel
        modelRecap.addRow(new Object[]{m.getNom(), qte, freq, duree + " jours"});
    }

    private void retirerLigne() {
        int row = tableRecap.getSelectedRow();
        if (row != -1) {
            prescriptionsList.remove(row);
            modelRecap.removeRow(row);
        }
    }

    private void validerOrdonnance() {
        Patient patient = (Patient) cmbPatient.getSelectedItem();
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un patient !");
            return;
        }
        if (prescriptionsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ajoutez au moins un médicament !");
            return;
        }

        try {
            Ordonnance ord = Ordonnance.builder()
                    .idPatient(patient.getIdPatient())
                    .idMedecin(1L) // ID Médecin connecté (simulé)
                    .build();

            controller.enregistrerOrdonnance(ord, prescriptionsList);

            isSaved = true;
            JOptionPane.showMessageDialog(this, "Ordonnance enregistrée avec succès !");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return isSaved;
    }
}