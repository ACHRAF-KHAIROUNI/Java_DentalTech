package ma.dentalTech.mvc.ui.modules.certificat;

import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.mvc.controllers.modules.certificat.api.CertificatController;
import ma.dentalTech.mvc.dto.CertificatDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CertificatView extends JFrame {

    private final CertificatController controller;
    private final Long idPatient;

    // Components
    private JTextField txtDuree, txtNote;
    private JTable tableCertifs;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    public CertificatView(CertificatController controller, Long idPatient) {
        this.controller = controller;
        this.idPatient = idPatient;
        initComponents();
        refreshTable();
        updateStatus();
    }

    private void initComponents() {
        setTitle("Gestion Certificats - Patient ID: " + idPatient);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HAUT : Formulaire Ajout ---
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nouveau Certificat"));

        txtDuree = new JTextField(5);
        txtNote = new JTextField(20);
        JButton btnAdd = new JButton("Générer");
        btnAdd.setBackground(new Color(0, 100, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> actionGenerate());

        panelForm.add(new JLabel("Durée (jours):")); panelForm.add(txtDuree);
        panelForm.add(new JLabel("Note Médicale:")); panelForm.add(txtNote);
        panelForm.add(btnAdd);

        // Indicateur Arret Maladie
        lblStatus = new JLabel("Statut : Inconnu");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelForm.add(Box.createHorizontalStrut(50)); // Espace
        panelForm.add(lblStatus);

        add(panelForm, BorderLayout.NORTH);

        // --- CENTRE : Tableau ---
        String[] cols = {"ID", "Période", "Durée", "Note"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableCertifs = new JTable(tableModel);
        tableCertifs.setRowHeight(25);
        add(new JScrollPane(tableCertifs), BorderLayout.CENTER);

        // --- BAS : Actions ---
        JPanel panelBtn = new JPanel(new FlowLayout());
        JButton btnPrint = new JButton("Aperçu Impression");
        JButton btnUpdate = new JButton("Corriger");
        JButton btnDelete = new JButton("Supprimer");

        btnPrint.addActionListener(e -> actionPrint());
        btnUpdate.addActionListener(e -> actionUpdate());
        btnDelete.addActionListener(e -> actionDelete());

        panelBtn.add(btnPrint);
        panelBtn.add(btnUpdate);
        panelBtn.add(btnDelete);

        add(panelBtn, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<CertificatDTO> list = controller.getCertificatsByPatient(idPatient);
        for (CertificatDTO dto : list) {
            tableModel.addRow(new Object[]{
                    dto.getIdCertif(), dto.getPeriode(), dto.getDuree() + " jours", dto.getNote()
            });
        }
        updateStatus();
    }

    private void updateStatus() {
        boolean isSick = controller.checkArretMaladie(idPatient);
        if (isSick) {
            lblStatus.setText("⚠ EN ARRÊT MALADIE");
            lblStatus.setForeground(Color.RED);
        } else {
            lblStatus.setText("✔ Actif");
            lblStatus.setForeground(new Color(0, 100, 0));
        }
    }

    private void actionGenerate() {
        try {
            int duree = Integer.parseInt(txtDuree.getText());
            Certificat c = Certificat.builder()
                    .idPatient(idPatient)
                    .duree(duree)
                    .noteMedecin(txtNote.getText())
                    .dateDebut(LocalDate.now())
                    .idMedecin(1L) // Simulé
                    .build();

            controller.createCertificat(c);
            txtDuree.setText(""); txtNote.setText("");
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durée invalide (entier requis).");
        }
    }

    private void actionPrint() {
        int row = tableCertifs.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        controller.previewPrint(id);
    }

    private void actionDelete() {
        int row = tableCertifs.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        controller.deleteCertificat(id);
        refreshTable();
    }

    private void actionUpdate() {
        int row = tableCertifs.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);

        String dureeStr = JOptionPane.showInputDialog(this, "Nouvelle durée (laisser vide pour ne pas changer):");
        String noteStr = JOptionPane.showInputDialog(this, "Nouvelle note (laisser vide pour ne pas changer):");

        Integer d = (dureeStr != null && !dureeStr.isBlank()) ? Integer.parseInt(dureeStr) : null;
        String n = (noteStr != null && !noteStr.isBlank()) ? noteStr : null;

        if (d != null || n != null) {
            controller.updateCertificat(id, d, n);
            refreshTable();
        }
    }
}