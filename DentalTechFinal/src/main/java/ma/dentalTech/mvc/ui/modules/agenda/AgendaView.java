package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.AgendaDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AgendaView extends JFrame {

    private final AgendaController controller;
    private final Long idMedecin;

    // Composants Création
    private JComboBox<String> comboMois;
    private JSpinner spinnerAnnee;

    // Composants Liste (Gauche)
    private JTable tableAgendas;
    private DefaultTableModel tableModel;

    // Composants Calendrier (Droite)
    private JPanel panelCalendarGrid;
    private JLabel lblSelectedMonth;

    public AgendaView(AgendaController controller, Long idMedecin) {
        this.controller = controller;
        this.idMedecin = idMedecin;
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Gestion Agenda Visuel - Médecin " + idMedecin);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. PANEL HAUT : Création propre (Combo + Spinner)
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelTop.setBorder(BorderFactory.createTitledBorder("Créer un nouvel Agenda"));
        panelTop.setBackground(new Color(240, 240, 240));

        String[] mois = {"Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"};
        comboMois = new JComboBox<>(mois);
        spinnerAnnee = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));

        JButton btnCreate = new JButton("Générer l'Agenda");
        btnCreate.setBackground(new Color(0, 120, 215));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.addActionListener(e -> actionCreate());

        panelTop.add(new JLabel("Mois :"));
        panelTop.add(comboMois);
        panelTop.add(new JLabel("Année :"));
        panelTop.add(spinnerAnnee);
        panelTop.add(btnCreate);

        add(panelTop, BorderLayout.NORTH);

        // 2. SPLIT PANE (Gauche: Liste, Droite: Calendrier)
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(400); // Taille panneau gauche

        // --- GAUCHE : La liste des agendas ---
        JPanel panelLeft = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Mois / Année", "Statut"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableAgendas = new JTable(tableModel);
        tableAgendas.setRowHeight(30);
        tableAgendas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadCalendarForSelectedRow();
        });

        panelLeft.add(new JScrollPane(tableAgendas), BorderLayout.CENTER);

        // Boutons actions bas gauche
        JPanel panelLeftActions = new JPanel(new GridLayout(1, 2));
        JButton btnDelete = new JButton("Supprimer");
        JButton btnToggle = new JButton("Ouvrir/Fermer");
        btnDelete.addActionListener(e -> actionDelete());
        btnToggle.addActionListener(e -> actionToggle());
        panelLeftActions.add(btnToggle);
        panelLeftActions.add(btnDelete);
        panelLeft.add(panelLeftActions, BorderLayout.SOUTH);

        // --- DROITE : Le Calendrier Visuel ---
        JPanel panelRight = new JPanel(new BorderLayout());
        lblSelectedMonth = new JLabel("Sélectionnez un agenda à gauche", SwingConstants.CENTER);
        lblSelectedMonth.setFont(new Font("Arial", Font.BOLD, 16));
        lblSelectedMonth.setBorder(new EmptyBorder(10,0,10,0));

        panelCalendarGrid = new JPanel();
        // On utilisera GridLayout dynamiquement

        panelRight.add(lblSelectedMonth, BorderLayout.NORTH);
        panelRight.add(new JScrollPane(panelCalendarGrid), BorderLayout.CENTER);

        // Légende
        JPanel panelLegend = new JPanel();
        JLabel l1 = new JLabel(" Vert = Disponible ");
        l1.setOpaque(true); l1.setBackground(new Color(144, 238, 144));
        JLabel l2 = new JLabel(" Rouge = Indisponible ");
        l2.setOpaque(true); l2.setBackground(new Color(255, 102, 102));
        panelLegend.add(l1);
        panelLegend.add(l2);
        panelRight.add(panelLegend, BorderLayout.SOUTH);

        splitPane.setLeftComponent(panelLeft);
        splitPane.setRightComponent(panelRight);
        add(splitPane, BorderLayout.CENTER);
    }

    // --- LOGIQUE METIER ---

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<AgendaDTO> list = controller.getAgendasByMedecin(idMedecin);
        for (AgendaDTO a : list) {
            tableModel.addRow(new Object[]{a.getIdAgenda(), a.getMois(), a.getStatut()});
        }
    }

    private void loadCalendarForSelectedRow() {
        int row = tableAgendas.getSelectedRow();
        if (row == -1) {
            panelCalendarGrid.removeAll();
            panelCalendarGrid.repaint();
            return;
        }

        Long idAgenda = (Long) tableModel.getValueAt(row, 0);
        String moisStr = (String) tableModel.getValueAt(row, 1);
        String statut = (String) tableModel.getValueAt(row, 2);

        lblSelectedMonth.setText("Calendrier : " + moisStr + " (" + statut + ")");

        // 1. Décomposer "Janvier 2025" pour savoir combien de jours afficher
        int[] dateParts = parseMonthYear(moisStr); // [Mois (1-12), Année]
        if (dateParts == null) {
            lblSelectedMonth.setText("Format de date invalide pour l'affichage calendrier");
            panelCalendarGrid.removeAll();
            return;
        }

        // 2. Récupérer les jours OFF depuis la BDD
        List<LocalDate> unavailableDates = controller.getUnavailableDatesRaw(idAgenda);

        // 3. Construire la Grille
        buildCalendarGrid(idAgenda, dateParts[0], dateParts[1], unavailableDates);
    }

    private void buildCalendarGrid(Long idAgenda, int month, int year, List<LocalDate> unavailableDates) {
        panelCalendarGrid.removeAll();

        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        // Layout: 7 colonnes pour les jours, lignes auto
        panelCalendarGrid.setLayout(new GridLayout(0, 7, 5, 5));
        panelCalendarGrid.setBorder(new EmptyBorder(10,10,10,10));

        // En-têtes jours
        String[] daysOfWeek = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (String d : daysOfWeek) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            panelCalendarGrid.add(lbl);
        }

        // Cases vides pour le début du mois (si le 1er est un Mercredi, on saute Lun/Mar)
        int startDayOfWeek = yearMonthObject.atDay(1).getDayOfWeek().getValue(); // 1=Lundi, 7=Dimanche
        for (int i = 1; i < startDayOfWeek; i++) {
            panelCalendarGrid.add(new JLabel("")); // Case vide
        }

        // Boutons des jours
        // Dans buildCalendarGrid(...)

// ... (début de la méthode inchangé)

// Boutons des jours
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(year, month, day);
            JButton btnDay = new JButton(String.valueOf(day));

            // Style par défaut
            btnDay.setFont(new Font("Arial", Font.BOLD, 14));
            btnDay.setFocusPainted(false);

            // Vérifier si indisponible (ROUGE)
            boolean isUnavailable = unavailableDates.contains(currentDate);
            if (isUnavailable) {
                btnDay.setBackground(new Color(255, 102, 102)); // Rouge
                btnDay.setForeground(Color.WHITE);
                btnDay.setToolTipText("Médecin absent");
            } else {
                btnDay.setBackground(new Color(144, 238, 144)); // Vert
                btnDay.setToolTipText("Disponible");
            }

            // --- CORRECTION ICI : Gestion du clic ---
            btnDay.addActionListener(e -> {

                // 1. Vérifier si c'est une date passée
                if (currentDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de modifier une date passée (" + currentDate + ").",
                            "Action refusée",
                            JOptionPane.WARNING_MESSAGE);
                    return; // On arrête tout ici
                }

                // 2. Si c'est déjà rouge, on ne fait rien (ou on pourrait gérer la suppression)
                if (isUnavailable) {
                    JOptionPane.showMessageDialog(this, "Ce jour est déjà marqué comme indisponible.");
                    return;
                }

                // 3. Demander confirmation
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Marquer le " + currentDate + " comme indisponible ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.markDayUnavailable(idAgenda, currentDate);
                    loadCalendarForSelectedRow();
                }
            });

            panelCalendarGrid.add(btnDay);
        }

// ... (fin de la méthode inchangée)

        panelCalendarGrid.revalidate();
        panelCalendarGrid.repaint();
    }

    // --- ACTIONS ---

    private void actionCreate() {
        String moisNom = (String) comboMois.getSelectedItem();
        int annee = (Integer) spinnerAnnee.getValue();
        // On formate "Janvier 2025" pour que ce soit propre
        String moisComplet = moisNom + " " + annee;

        AgendaMensuel a = AgendaMensuel.builder()
                .mois(moisComplet)
                .idMedecin(idMedecin)
                .etat(true)
                .build();
        controller.createAgenda(a);
        refreshTable();
    }

    private void actionDelete() {
        int row = tableAgendas.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        controller.deleteAgenda(id);
        refreshTable();
        panelCalendarGrid.removeAll();
        panelCalendarGrid.repaint();
    }

    private void actionToggle() {
        int row = tableAgendas.getSelectedRow();
        if (row == -1) return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        String statut = (String) tableModel.getValueAt(row, 2);
        controller.toggleAgendaStatus(id, "OUVERT".equals(statut));
        refreshTable();
    }

    // --- UTILITAIRE : Parsing "Janvier 2025" -> [1, 2025] ---
    private int[] parseMonthYear(String str) {
        try {
            String[] parts = str.split(" ");
            if (parts.length < 2) return null;

            String mName = parts[0].trim().toLowerCase();
            int y = Integer.parseInt(parts[1].trim());
            int m = switch (mName) {
                case "janvier" -> 1; case "fevrier", "février" -> 2; case "mars" -> 3;
                case "avril" -> 4; case "mai" -> 5; case "juin" -> 6;
                case "juillet" -> 7; case "aout", "août" -> 8; case "septembre" -> 9;
                case "octobre" -> 10; case "novembre" -> 11; case "decembre", "décembre" -> 12;
                default -> -1;
            };
            if (m == -1) return null;
            return new int[]{m, y};
        } catch (Exception e) {
            return null;
        }
    }

}