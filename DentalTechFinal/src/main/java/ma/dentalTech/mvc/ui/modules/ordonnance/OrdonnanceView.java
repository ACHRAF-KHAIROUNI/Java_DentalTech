package ma.dentalTech.mvc.ui.modules.ordonnance;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.ordonnance.api.OrdonnanceController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
//ROBAII IKRAM
public class OrdonnanceView extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<Patient> cmbPatientFilter;
    private JButton btnNew, btnDelete, btnPrint;

    private final OrdonnanceController controller;

    public OrdonnanceView() {
        this.controller = ApplicationContext.getInstance().getOrdController();
        initUI();
    }
    
    // Constructeur pour pré-filtrer (Integration Dossier Medical)
    public OrdonnanceView(Long idPatientToSelect) {
        this();
        if(idPatientToSelect != null) {
            for(int i=0; i<cmbPatientFilter.getItemCount(); i++) {
                Patient p = cmbPatientFilter.getItemAt(i);
                if(p != null && p.getIdPatient().equals(idPatientToSelect)) {
                    cmbPatientFilter.setSelectedIndex(i);
                    refreshTable(); // Auto-refresh
                    break;
                }
            }
            // Disable filter to lock context if needed, or leave open. Let's leave open for flexibility.
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- HEADER (Filtres & Actions) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Filtre Patient
        cmbPatientFilter = new JComboBox<>();
        cmbPatientFilter.setPreferredSize(new Dimension(200, 30));
        chargerPatients();

        JButton btnSearch = new JButton("🔍 Filtrer");

        // Boutons Actions (Design Flat)
        btnNew = createStyledButton("➕ Nouvelle Ordonnance", new Color(60, 179, 113)); // SeaGreen
        btnDelete = createStyledButton("🗑️ Supprimer", new Color(220, 53, 69)); // Red
        btnPrint = createStyledButton("🖨️ Imprimer", new Color(23, 162, 184)); // Cyan

        headerPanel.add(new JLabel("Patient :"));
        headerPanel.add(cmbPatientFilter);
        headerPanel.add(btnSearch);
        headerPanel.add(Box.createHorizontalStrut(30)); // Spacer
        headerPanel.add(btnNew);
        headerPanel.add(btnPrint);
        headerPanel.add(btnDelete);

        add(headerPanel, BorderLayout.NORTH);

        // --- TABLEAU ---
        String[] cols = {"ID", "Date", "Médecin", "Nb Médicaments"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35); // Lignes plus hautes = plus lisible
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        // Cacher la colonne ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- EVENTS ---
        btnSearch.addActionListener(e -> refreshTable());

        btnNew.addActionListener(e -> {
            // Ouvrir le formulaire (Code à venir à l'étape suivante)
            OrdonnanceFormDialog dialog = new OrdonnanceFormDialog(
                    SwingUtilities.getWindowAncestor(this), controller
            );
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshTable();
        });

        btnDelete.addActionListener(e -> deleteSelected());

        btnPrint.addActionListener(e -> printSelected());
    }

    private void chargerPatients() {
        cmbPatientFilter.addItem(null); // Option vide
        List<Patient> patients = controller.getTousLesPatients();
        for (Patient p : patients) cmbPatientFilter.addItem(p);
    }

    private void refreshTable() {
        Patient selected = (Patient) cmbPatientFilter.getSelectedItem();
        if (selected == null) return;

        List<Ordonnance> list = controller.chargerHistoriquePatient(selected.getIdPatient());
        model.setRowCount(0);

        for (Ordonnance o : list) {
            model.addRow(new Object[]{
                    o.getIdOrd(),
                    o.getDate(),
                    "Dr. " + o.getIdMedecin(), // Idéalement le nom
                    "Détails..." // On pourrait compter les prescriptions ici
            });
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette ordonnance ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) model.getValueAt(row, 0);
            try {
                controller.supprimerOrdonnance(id);
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
            }
        }
    }

    private void printSelected() {
        int row = table.getSelectedRow();
        if (row != -1) {
            Long id = (Long) model.getValueAt(row, 0);


            Ordonnance ord = controller.getOrdonnanceById(id);
            Patient p = controller.getPatientById(ord.getIdPatient());
            List<Prescription> details = controller.getDetailsOrdonnance(id);

            // 2. Lancer l'impression graphique
            ma.dentalTech.mvc.ui.modules.ordonnance.OrdonnancePrinter.imprimer(ord, p, details);
        }
    }

    // Helper pour des boutons jolis
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }
}