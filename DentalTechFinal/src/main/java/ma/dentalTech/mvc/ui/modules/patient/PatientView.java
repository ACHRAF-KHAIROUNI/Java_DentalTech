package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
// IMPORT IMPORTANT : On a besoin du contrôleur des antécédents pour l'ouvrir
import ma.dentalTech.mvc.controllers.modules.patient.swing_implementation.AntecedentControllerImpl;
import ma.dentalTech.mvc.ui.modules.dossierMedical.DossierMedicalView;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.swing_implementation.DossierMedicalControllerImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class PatientView extends JFrame {

    private final PatientController controller;

    // Composants Formulaire
    private JTextField txtId, txtNom, txtPrenom, txtTel, txtDateNaiss, txtSearch;
    private JComboBox<Sexe> cbSexe;
    private JComboBox<Assurance> cbAssurance;
    private JTable tablePatients;
    private DefaultTableModel tableModel;

    public PatientView(PatientController controller) {
        this.controller = controller;
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Gestion des Patients & Dossiers Médicaux");
        setSize(1100, 650); // Un peu plus large pour le nouveau bouton
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. PANEL GAUCHE (Formulaire) ---
        JPanel panelForm = new JPanel(new GridLayout(8, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Informations Patient"));
        panelForm.setBackground(new Color(240, 248, 255));
        panelForm.setPreferredSize(new Dimension(320, 0));

        txtId = new JTextField(); txtId.setEditable(false);
        txtNom = new JTextField();
        txtPrenom = new JTextField();
        txtTel = new JTextField();
        txtDateNaiss = new JTextField();
        txtDateNaiss.setToolTipText("Format: AAAA-MM-JJ");

        cbSexe = new JComboBox<>(Sexe.values());
        cbAssurance = new JComboBox<>(Assurance.values());

        panelForm.add(new JLabel("ID:")); panelForm.add(txtId);
        panelForm.add(new JLabel("Nom:")); panelForm.add(txtNom);
        panelForm.add(new JLabel("Prénom:")); panelForm.add(txtPrenom);
        panelForm.add(new JLabel("Tél:")); panelForm.add(txtTel);
        panelForm.add(new JLabel("Date Naiss:")); panelForm.add(txtDateNaiss);
        panelForm.add(new JLabel("Sexe:")); panelForm.add(cbSexe);
        panelForm.add(new JLabel("Assurance:")); panelForm.add(cbAssurance);

        // --- BOUTONS ACTIONS CRUD ---
        JPanel panelBtn = new JPanel(new GridLayout(2, 2, 5, 5)); // Grille 2x2 pour faire de la place
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnClear = new JButton("Vider champs");

        // Couleurs
        btnAdd.setBackground(new Color(0, 128, 0)); btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(0, 0, 128)); btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(128, 0, 0)); btnDelete.setForeground(Color.WHITE);

        btnAdd.addActionListener(e -> actionAdd());
        btnUpdate.addActionListener(e -> actionUpdate());
        btnDelete.addActionListener(e -> actionDelete());
        btnClear.addActionListener(e -> clearForm());

        panelBtn.add(btnAdd); panelBtn.add(btnUpdate);
        panelBtn.add(btnDelete); panelBtn.add(btnClear);

        // Container Gauche
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(panelForm, BorderLayout.CENTER);
        leftContainer.add(panelBtn, BorderLayout.SOUTH);

        add(leftContainer, BorderLayout.WEST);

        // --- 2. PANEL CENTRE (Tableau + Bouton Dossier) ---
        JPanel panelCenter = new JPanel(new BorderLayout());

        // Barre Recherche
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> refreshTable(txtSearch.getText()));
        panelSearch.add(new JLabel("Recherche:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);

        // Tableau
        String[] columns = {"ID", "Nom", "Prénom", "Téléphone", "Assurance", "Âge"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablePatients = new JTable(tableModel);
        tablePatients.setRowHeight(25);

        tablePatients.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePatients.getSelectedRow();
                if (row != -1) fillFormFromTable(row);

                // Double clic ouvre les antécédents directement
                if (e.getClickCount() == 2 && row != -1) {
                    actionOpenAntecedents();
                }
            }
        });

        // --- BOUTON SPECIAL : GÉRER ANTÉCÉDENTS ---
        JPanel panelBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAntecedents = new JButton("📂 Gérer Antécédents / Dossier");
        btnAntecedents.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAntecedents.setBackground(new Color(255, 140, 0)); // Orange
        btnAntecedents.setForeground(Color.WHITE);
        btnAntecedents.setPreferredSize(new Dimension(250, 40));

        btnAntecedents.addActionListener(e -> actionOpenAntecedents());

        panelBottomRight.add(btnAntecedents);

        // Assemblage Centre
        panelCenter.add(panelSearch, BorderLayout.NORTH);
        panelCenter.add(new JScrollPane(tablePatients), BorderLayout.CENTER);
        panelCenter.add(panelBottomRight, BorderLayout.SOUTH);

        add(panelCenter, BorderLayout.CENTER);
    }

    // --- LOGIQUE ---

    private void actionOpenAntecedents() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un patient dans la liste.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Long idPatient = Long.parseLong(txtId.getText());
            
            // OUVRIR LE DOSSIER MEDICAL COMPLET
            DossierMedicalControllerImpl dossierController = new DossierMedicalControllerImpl();
            new DossierMedicalView(dossierController, idPatient).setVisible(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Patient invalide.");
        }
    }

    private void refreshTable() { refreshTable(""); }

    private void refreshTable(String keyword) {
        tableModel.setRowCount(0);
        List<Patient> patients;
        if (keyword == null || keyword.isBlank()) {
            patients = controller.getAllPatients();
        } else {
            patients = controller.searchPatients(keyword);
        }

        for (Patient p : patients) {
            int age = (p.getDateDeNaissance() != null) ?
                    java.time.Period.between(p.getDateDeNaissance(), LocalDate.now()).getYears() : 0;

            tableModel.addRow(new Object[]{
                    p.getIdPatient(), p.getNom(), p.getPrenom(), p.getTelephone(), p.getAssurance(), age
            });
        }
    }

    private void actionAdd() {
        Patient p = buildPatientFromForm();
        if (p != null) {
            controller.savePatient(p);
            clearForm();
            refreshTable();
        }
    }

    private void actionUpdate() {
        if (txtId.getText().isEmpty()) return;
        Patient p = buildPatientFromForm();
        if (p != null) {
            p.setIdPatient(Long.parseLong(txtId.getText()));
            controller.updatePatient(p);
            clearForm();
            refreshTable();
        }
    }

    private void actionDelete() {
        if (txtId.getText().isEmpty()) return;
        Long id = Long.parseLong(txtId.getText());
        controller.deletePatient(id);
        clearForm();
        refreshTable();
    }

    private void clearForm() {
        txtId.setText(""); txtNom.setText(""); txtPrenom.setText("");
        txtTel.setText(""); txtDateNaiss.setText("");
        tablePatients.clearSelection();
    }

    private void fillFormFromTable(int row) {
        Long id = (Long) tableModel.getValueAt(row, 0);
        List<Patient> currentList = controller.searchPatients(txtSearch.getText());
        if(currentList.isEmpty()) currentList = controller.getAllPatients();

        Patient p = currentList.stream().filter(pat -> pat.getIdPatient().equals(id)).findFirst().orElse(null);

        if (p != null) {
            txtId.setText(String.valueOf(p.getIdPatient()));
            txtNom.setText(p.getNom());
            txtPrenom.setText(p.getPrenom());
            txtTel.setText(p.getTelephone());
            if (p.getDateDeNaissance() != null) txtDateNaiss.setText(p.getDateDeNaissance().toString());
            cbSexe.setSelectedItem(p.getSexe());
            cbAssurance.setSelectedItem(p.getAssurance());
        }
    }

    private Patient buildPatientFromForm() {
        try {
            return Patient.builder()
                    .nom(txtNom.getText())
                    .prenom(txtPrenom.getText())
                    .telephone(txtTel.getText())
                    .dateDeNaissance(txtDateNaiss.getText().isEmpty() ? null : LocalDate.parse(txtDateNaiss.getText()))
                    .sexe((Sexe) cbSexe.getSelectedItem())
                    .assurance((Assurance) cbAssurance.getSelectedItem())
                    .dateCreation(LocalDate.now())
                    .build();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur données (Format Date: AAAA-MM-JJ ?)");
            return null;
        }
    }
}