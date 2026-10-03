package ma.dentalTech.mvc.ui.modules.dashboard;

// --- CORRECTION IMPORT ---
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.DashboardDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardView extends JFrame {

    private final DashboardController controller;
    private final Long currentUserId;

    private JLabel lblTotalPatients, lblRdvToday, lblCA;
    private JTable tableFileAttente;
    private DefaultTableModel tableModel;

    public DashboardView(DashboardController controller, DashboardDTO initialData, Long userId) {
        this.controller = controller;
        this.currentUserId = userId;
        initComponents();
        updateData(initialData);
    }

    private void initComponents() {
        setTitle("Dashboard - DentalTech");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // --- 1. HEADER (Widgets) ---
        JPanel panelStats = new JPanel(new GridLayout(1, 3, 20, 0));
        panelStats.setOpaque(false);
        panelStats.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblTotalPatients = new JLabel("0", SwingConstants.CENTER);
        lblRdvToday = new JLabel("0", SwingConstants.CENTER);
        lblCA = new JLabel("0.00 DH", SwingConstants.CENTER);

        panelStats.add(createWidget("Patients Total", lblTotalPatients, new Color(0, 102, 204)));
        panelStats.add(createWidget("RDV Aujourd'hui", lblRdvToday, new Color(40, 167, 69)));
        panelStats.add(createWidget("CA du Mois", lblCA, new Color(255, 193, 7)));

        add(panelStats, BorderLayout.NORTH);

        // --- 2. CENTRE (File d'attente) ---
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(BorderFactory.createTitledBorder("File d'attente (RDV du Jour)"));
        panelCenter.setBackground(Color.WHITE);

        String[] cols = { "Heure", "Motif", "Statut", "Patient ID" };
        tableModel = new DefaultTableModel(cols, 0);
        tableFileAttente = new JTable(tableModel);
        tableFileAttente.setRowHeight(30);

        panelCenter.add(new JScrollPane(tableFileAttente), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.addActionListener(e -> controller.refreshData());

        JButton btnNotifs = new JButton("🔔 Notifications");
        btnNotifs.addActionListener(
                e -> new ma.dentalTech.mvc.ui.modules.notification.NotificationView(this, currentUserId)
                        .setVisible(true));

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.add(btnNotifs);
        panelBtn.add(btnRefresh);
        panelCenter.add(panelBtn, BorderLayout.SOUTH);

        add(panelCenter, BorderLayout.CENTER);
    }

    private JPanel createWidget(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void updateData(DashboardDTO data) {
        lblTotalPatients.setText(String.valueOf(data.getNombreTotalPatients()));
        lblRdvToday.setText(String.valueOf(data.getNombreRdvAujourdhui()));
        lblCA.setText(String.format("%.2f DH", data.getChiffreAffairesGlobal()));

        tableModel.setRowCount(0);
        List<RDV> rdvs = data.getFileAttente();
        if (rdvs != null) {
            for (RDV r : rdvs) {
                tableModel.addRow(new Object[] {
                        r.getHeure(), r.getMotif(), r.getStatut(), r.getIdPatient()
                });
            }
        }
    }
}