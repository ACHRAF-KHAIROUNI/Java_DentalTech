package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.dto.AntecedentDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AntecedentView extends JFrame {

    private final AntecedentController controller;
    private final Long idPatient;

    // Composants
    private JTextField txtId, txtNom;
    private JComboBox<CategorieAntecedent> cbCategorie;
    private JComboBox<NiveauRisque> cbRisque;
    private JTable table;
    private DefaultTableModel model;

    public AntecedentView(AntecedentController controller, Long idPatient) {
        this.controller = controller;
        this.idPatient = idPatient;
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Dossier Médical - Antécédents (Patient ID: " + idPatient + ")");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- FORMULAIRE (Haut) ---
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nouvel Antécédent"));

        txtId = new JTextField(); txtId.setEditable(false); txtId.setVisible(false); // ID caché
        txtNom = new JTextField();
        cbCategorie = new JComboBox<>(CategorieAntecedent.values());
        cbRisque = new JComboBox<>(NiveauRisque.values());

        panelForm.add(new JLabel("Nom (ex: Diabète):")); panelForm.add(txtNom);
        panelForm.add(new JLabel("Catégorie:")); panelForm.add(cbCategorie);
        panelForm.add(new JLabel("Niveau Risque:")); panelForm.add(cbRisque);

        JButton btnAdd = new JButton("Ajouter");
        btnAdd.setBackground(new Color(0, 100, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> actionAdd());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(panelForm, BorderLayout.CENTER);
        topContainer.add(btnPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // --- TABLEAU (Centre) ---
        String[] cols = {"ID", "Nom", "Catégorie", "Risque"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);

        // Couleur rouge pour les risques CRITIQUE/ELEVE (Custom Renderer simple)
        // (Optionnel, on garde simple pour l'instant)

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- ACTIONS (Bas) ---
        JPanel panelBottom = new JPanel(new FlowLayout());
        JButton btnDelete = new JButton("Supprimer");
        JButton btnUpdate = new JButton("Modifier (Sélection)");

        btnDelete.addActionListener(e -> actionDelete());
        btnUpdate.addActionListener(e -> actionUpdate());

        panelBottom.add(btnUpdate);
        panelBottom.add(btnDelete);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<AntecedentDTO> list = controller.getAntecedentsByPatient(idPatient);
        for (AntecedentDTO dto : list) {
            model.addRow(new Object[]{dto.getId(), dto.getNom(), dto.getCategorie(), dto.getNiveauRisque()});
        }
    }

    private void actionAdd() {
        if (txtNom.getText().isBlank()) return;
        Antecedent a = Antecedent.builder()
                .nom(txtNom.getText())
                .categorie((CategorieAntecedent) cbCategorie.getSelectedItem())
                .niveauRisque((NiveauRisque) cbRisque.getSelectedItem())
                .idPatient(idPatient)
                .build();

        controller.addAntecedent(a);
        txtNom.setText("");
        refreshTable();
    }

    private void actionDelete() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) model.getValueAt(row, 0);
        controller.deleteAntecedent(id);
        refreshTable();
    }

    private void actionUpdate() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) model.getValueAt(row, 0);
        String oldName = (String) model.getValueAt(row, 1);

        String newName = JOptionPane.showInputDialog(this, "Modifier nom:", oldName);
        if (newName != null && !newName.isBlank()) {
            // Pour faire simple, on ne modifie que le nom ici,
            // mais on pourrait ouvrir un formulaire complet
            Antecedent a = Antecedent.builder()
                    .idAntecedent(id)
                    .nom(newName)
                    .categorie((CategorieAntecedent) cbCategorie.getSelectedItem()) // Garde la sélection actuelle
                    .niveauRisque((NiveauRisque) cbRisque.getSelectedItem())
                    .idPatient(idPatient)
                    .build();

            controller.updateAntecedent(a);
            refreshTable();
        }
    }
}