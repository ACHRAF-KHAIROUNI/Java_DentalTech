package ma.dentalTech.mvc.ui.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.mvc.palette.Theme;

// --- DTOs & SERVICES ---
import ma.dentalTech.mvc.controllers.modules.backup.BackupController;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.mvc.dto.StatistiqueDTO;
import ma.dentalTech.mvc.ui.modules.rdvPatient.RDVCalendarView;
import ma.dentalTech.service.modules.dashboard.impl.DashboardServiceImpl;
import ma.dentalTech.service.modules.statistiques.impl.StatistiqueServiceImpl;

// --- VUES MODULES ---
import ma.dentalTech.mvc.ui.modules.actes.ActeView;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaView;
import ma.dentalTech.mvc.ui.modules.certificat.CertificatView;
import ma.dentalTech.mvc.ui.modules.medicament.MedicamentView;
import ma.dentalTech.mvc.ui.modules.ordonnance.OrdonnanceView;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.mvc.ui.modules.rdvPatient.RDVView;
import ma.dentalTech.mvc.ui.modules.referentiel.ReferentielView;
import ma.dentalTech.mvc.ui.modules.statistiques.StatistiqueView;
import ma.dentalTech.mvc.ui.modules.userManager.UtilisateurView;
import ma.dentalTech.mvc.ui.modules.userManager.MedecinView;

// --- CONTROLEURS ---
import ma.dentalTech.mvc.controllers.modules.actes.swing_implementation.ActeControllerImpl;
import ma.dentalTech.mvc.controllers.modules.agenda.swing_implementation.AgendaControllerImpl;
import ma.dentalTech.mvc.controllers.modules.certificat.swing_implementation.CertificatControllerImpl;
import ma.dentalTech.mvc.controllers.modules.medicament.swing_implementation.MedicamentControllerImpl;
import ma.dentalTech.mvc.controllers.modules.patient.swing_implementation.PatientControllerImpl;
import ma.dentalTech.mvc.controllers.modules.referentiel.swing_implementation.ReferentielControllerImpl;

// --- CAISSE ---
import ma.dentalTech.mvc.ui.modules.caisse.CaisseView;
import ma.dentalTech.mvc.controllers.modules.caisse.swing_implementation.CaisseControllerImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DashboardView extends JFrame {

    // --- COULEURS PERSONNALISÉES ---
    private final Color SIDEBAR_BG = new Color(33, 47, 61); // Bleu Nuit Profond
    private final Color ACTIVE_BTN = new Color(52, 152, 219); // Bleu clair
    private final Color HOVER_BTN = new Color(44, 62, 80); // Gris bleuté
    private final Color MAIN_BG = new Color(245, 247, 250); // Gris très clair (Google Style)
    private final Font FONT_MENU = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);

    private final Utilisateur utilisateur;
    private JPanel sidebar;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private ButtonGroup menuGroup; // Pour gérer l'état "actif" des boutons

    // --- CORRECTION : DÉCLARATION DES LABELS POUR L'ACCUEIL ---
    private JLabel lblTotalPatients, lblRdvToday, lblCA;
    private DefaultTableModel tableModelAccueil;

    public DashboardView(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.menuGroup = new ButtonGroup();
        initUI();
    }

    private void initUI() {
        setTitle("DentalTech - Espace " + getRoleName());
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- SIDEBAR (Menu Gauche) ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Profil Utilisateur en haut de la sidebar
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setOpaque(false);
        profilePanel.setMaximumSize(new Dimension(260, 80));

        JLabel iconUser = new JLabel("👤");
        iconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconUser.setForeground(Color.WHITE);

        JPanel textUser = new JPanel(new GridLayout(2, 1));
        textUser.setOpaque(false);
        JLabel lblName = new JLabel(utilisateur.getNom());
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblRole = new JLabel(getRoleName().toUpperCase());
        lblRole.setForeground(new Color(180, 180, 180));
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        textUser.add(lblName);
        textUser.add(lblRole);

        profilePanel.add(iconUser);
        profilePanel.add(Box.createHorizontalStrut(10));
        profilePanel.add(textUser);

        sidebar.add(profilePanel);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(createSectionTitle("NAVIGATION"));
        sidebar.add(Box.createVerticalStrut(10));

        // --- CONTENT AREA (Zone Centrale) ---
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(MAIN_BG);

        // Construction du menu
        buildMenuBasedOnRole();

        // Footer Sidebar
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL));
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnNotifs = createFooterButton("🔔 Notifications", new Color(41, 128, 185)); // Bleu
        btnNotifs.addActionListener(
                e -> new ma.dentalTech.mvc.ui.modules.notification.NotificationView(this, utilisateur.getIdUser())
                        .setVisible(true));
        sidebar.add(btnNotifs);
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnLogout = createFooterButton("Déconnexion", new Color(231, 76, 60));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private void buildMenuBasedOnRole() {
        long roleId = utilisateur.getIdRole();

        // 1. ACCUEIL (Pour tout le monde)
        // Note: On utilise un Unicode pour l'icône
        addModule("Accueil", "🏠", createHomePanel());

        // 2. STATISTIQUES
        if (roleId == 1 || roleId == 2) {
            addModule("Statistiques", "📊", createStatsPanel());
        }

        // 3. SECRÉTARIAT
        if (roleId == 1 || roleId == 2 || roleId == 3) {
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(createSectionTitle("SECRETARIAT"));
            sidebar.add(Box.createVerticalStrut(5));

            addModule("Agenda", "📅", new AgendaView(new AgendaControllerImpl(), 1L).getContentPane());
            addModule("Patients", "👥", new PatientView(new PatientControllerImpl()).getContentPane());
            addModule("Salle d'Attente", "⏳", new RDVCalendarView());
            addModule("Caisse", "💰", new CaisseView(new CaisseControllerImpl()).getContentPane());
        }

        // 4. MÉDICAL
        if (roleId == 1 || roleId == 2) {
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(createSectionTitle("MEDICAL"));
            sidebar.add(Box.createVerticalStrut(5));

            addModule("Ordonnances", "💊", new OrdonnanceView());
            addModule("Certificats", "📝", new CertificatView(new CertificatControllerImpl(), null).getContentPane());
            addModule("Médicaments", "🧪", new MedicamentView(new MedicamentControllerImpl()).getContentPane());
            addModule("Actes Médicaux", "💉", new ActeView(new ActeControllerImpl()).getContentPane());
            addModule("Référentiels", "📚", new ReferentielView(new ReferentielControllerImpl()).getContentPane());
        }

        // 5. ADMIN
        if (roleId == 1) {
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(createSectionTitle("ADMINISTRATION"));
            sidebar.add(Box.createVerticalStrut(5));

            addModule("Gestion Users", "🔑", new UtilisateurView().getContentPane());
            addModule("Gestion Médecins", "👨‍⚕️", new MedecinView().getContentPane());

            // Bouton Backup spécial
            JButton btnBackup = createFooterButton("💾 Sauvegarde BDD", new Color(230, 126, 34));
            btnBackup.addActionListener(e -> new BackupController().performBackup(this));
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(btnBackup);
        }
    }

    // --- DESIGN VUE ACCUEIL ---
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(MAIN_BG);
        homePanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Titre Page
        JLabel lblTitre = new JLabel("Vue d'ensemble du cabinet");
        lblTitre.setFont(FONT_TITLE);
        lblTitre.setForeground(new Color(50, 50, 50));
        homePanel.add(lblTitre, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setOpaque(false);

        // 1. Widgets (Cartes)
        JPanel panelStats = new JPanel(new GridLayout(1, 3, 25, 0));
        panelStats.setOpaque(false);
        panelStats.setPreferredSize(new Dimension(0, 140)); // Hauteur fixe pour les cartes

        // Initialisation des labels
        lblTotalPatients = new JLabel("...", SwingConstants.RIGHT);
        lblRdvToday = new JLabel("...", SwingConstants.RIGHT);
        lblCA = new JLabel("...", SwingConstants.RIGHT);

        panelStats.add(createModernWidget("Patients Total", lblTotalPatients, new Color(41, 128, 185), "👥"));
        panelStats.add(createModernWidget("RDV Aujourd'hui", lblRdvToday, new Color(39, 174, 96), "📅"));
        panelStats.add(createModernWidget("CA du Mois", lblCA, new Color(243, 156, 18), "💰"));

        centerContainer.add(panelStats, BorderLayout.NORTH);

        // 2. File d'attente (Tableau stylisé)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        // Arrondir le panneau du tableau
        tablePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1, #E0E0E0, 1, 15");

        JLabel lblTable = new JLabel("  File d'attente (RDV du Jour)");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setBorder(new EmptyBorder(15, 10, 15, 10));
        tablePanel.add(lblTable, BorderLayout.NORTH);

        String[] cols = { "Heure", "Motif", "Statut", "Patient ID" };
        tableModelAccueil = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModelAccueil);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton Rafraîchir
        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnRefresh.addActionListener(e -> refreshHomeData(lblTotalPatients, lblRdvToday, lblCA));

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(Color.WHITE);
        pBtn.setBorder(new EmptyBorder(10, 10, 10, 10));
        pBtn.add(btnRefresh);
        tablePanel.add(pBtn, BorderLayout.SOUTH);

        centerContainer.add(tablePanel, BorderLayout.CENTER);
        homePanel.add(centerContainer, BorderLayout.CENTER);

        // Chargement initial
        refreshHomeData(lblTotalPatients, lblRdvToday, lblCA);

        return homePanel;
    }

    // --- COMPOSANTS UI MODERNES ---

    private JPanel createModernWidget(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Effet d'ombre et bordure gauche colorée
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                new EmptyBorder(15, 20, 15, 20)));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15");

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(50, 50, 50));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50)); // Transparent

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblIcon, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JToggleButton createMenuButton(String text, String icon) {
        JToggleButton btn = new JToggleButton(" " + icon + "   " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setFont(FONT_MENU);
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet visuel lors de la sélection
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(ACTIVE_BTN); // Bleu actif
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(new Color(200, 200, 200));
            }
        });

        // Effet Hover simple
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.isSelected())
                    btn.setBackground(HOVER_BTN);
            }

            public void mouseExited(MouseEvent e) {
                if (!btn.isSelected())
                    btn.setBackground(SIDEBAR_BG);
            }
        });

        return btn;
    }

    private JButton createFooterButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(240, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 110, 120));
        lbl.setBorder(new EmptyBorder(0, 15, 0, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(70, 70, 70));
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    // --- LOGIQUE METIER ---

    private void addModule(String title, String icon, Component viewContent) {
        JToggleButton btn = createMenuButton(title, icon);
        btn.addActionListener(e -> cardLayout.show(contentArea, title));

        // Si c'est le premier bouton (Accueil), on le sélectionne par défaut
        if (menuGroup.getButtonCount() == 0) {
            btn.setSelected(true);
        }

        menuGroup.add(btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(5));
        contentArea.add(viewContent, title);
    }

    private Container createStatsPanel() {
        try {
            StatistiqueServiceImpl service = new StatistiqueServiceImpl();
            StatistiqueDTO data = service.getStatistiquesGlobales();
            return new StatistiqueView(data).getContentPane();
        } catch (Exception e) {
            return new JLabel("Erreur chargement statistiques : " + e.getMessage());
        }
    }

    private void refreshHomeData(JLabel lblTotal, JLabel lblToday, JLabel lblCa) {
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardServiceImpl service = new DashboardServiceImpl();
                DashboardDTO data = service.getDashboardData();

                lblTotal.setText(String.valueOf(data.getNombreTotalPatients()));
                lblToday.setText(String.valueOf(data.getNombreRdvAujourdhui()));
                lblCa.setText(String.format("%.2f DH", data.getChiffreAffairesGlobal()));

                tableModelAccueil.setRowCount(0);
                if (data.getFileAttente() != null) {
                    for (RDV r : data.getFileAttente()) {
                        tableModelAccueil
                                .addRow(new Object[] { r.getHeure(), r.getMotif(), r.getStatut(), r.getIdPatient() });
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement accueil : " + e.getMessage());
            }
        });
    }

    private void logout() {
        this.dispose();
        new ma.dentalTech.mvc.ui.login.LoginView().setVisible(true);
    }

    private String getRoleName() {
        if (utilisateur.getIdRole() == 1)
            return "Administrateur";
        if (utilisateur.getIdRole() == 2)
            return "Médecin";
        if (utilisateur.getIdRole() == 3)
            return "Secrétaire";
        return "Utilisateur";
    }
}