package ma.dentalTech.mvc.ui.modules.rdvPatient;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.api.RDVController;
import ma.dentalTech.mvc.dto.RDVDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//ROBAII IKRAM
public class RDVCalendarView extends JPanel {

    private final RDVController controller;

    private YearMonth currentMonth;
    private LocalDate selectedDate;

    private Patient patientFilter = null;
    private Medecin medecinFilter = null;

    private JPanel gridPanel;
    private JPanel schedulePanel;
    private JLabel lblMonthTitle;
    private JComboBox<Patient> cmbPatient;
    private JComboBox<Medecin> cmbMedecin;

    private Map<Integer, List<RDVDTO>> monthCache = new HashMap<>();

    public RDVCalendarView() {
        this.controller = ApplicationContext.getInstance().getRdvController();
        this.currentMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        setLayout(new BorderLayout());
        initUI();

        // Sélection par défaut du 1er médecin
        if (cmbMedecin.getItemCount() > 0) {
            cmbMedecin.setSelectedIndex(0);
        } else {
            refreshAll();
        }
    }

    private void initUI() {
        // --- HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 1. Filtre Médecin
        header.add(new JLabel("👨‍⚕️ Agenda Dr : "));
        cmbMedecin = new JComboBox<>();
        cmbMedecin.setPreferredSize(new Dimension(200, 30));

        // --- RENDERER MEDECIN ---
        cmbMedecin.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Medecin) {
                    Medecin m = (Medecin) value;
                    if (m.getUtilisateur() != null && m.getUtilisateur().getNom() != null) {
                        setText(m.getUtilisateur().getNom()); // Affiche "Dr. Medecin Test"
                    } else {
                        setText("Medecin #" + m.getIdMedecin()); // Fallback
                    }
                }
                return this;
            }
        });

        // Remplissage
        List<Medecin> docs = controller.getTousLesMedecins();
        if (docs != null) docs.forEach(cmbMedecin::addItem);

        cmbMedecin.addActionListener(e -> {
            medecinFilter = (Medecin) cmbMedecin.getSelectedItem();
            refreshAll();
        });
        header.add(cmbMedecin);

        header.add(Box.createHorizontalStrut(20));

        // 2. Filtre Patient
        header.add(new JLabel("🔍 Voir Patient : "));
        cmbPatient = new JComboBox<>();
        cmbPatient.setPreferredSize(new Dimension(200, 30));
        cmbPatient.addItem(null);

        // --- RENDERER PATIENT ---
        cmbPatient.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient p = (Patient) value;
                    setText(p.getNom() + " " + p.getPrenom());
                } else if (value == null) {
                    setText("- Tous -");
                }
                return this;
            }
        });

        List<Patient> patients = controller.getTousLesPatients();
        if (patients != null) patients.forEach(cmbPatient::addItem);

        cmbPatient.addActionListener(e -> {
            patientFilter = (Patient) cmbPatient.getSelectedItem();
            refreshCalendarColors();
        });
        header.add(cmbPatient);

        // Légendes
        header.add(Box.createHorizontalStrut(20));
        header.add(createLegend(new Color(255, 182, 193), "Surchargé"));
        header.add(createLegend(new Color(144, 238, 144), "RDV Patient"));

        add(header, BorderLayout.NORTH);

        // --- SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);

        // GAUCHE : Calendrier
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.WHITE);
        JButton btnPrev = createRoundedButton("<", new Color(70, 130, 180));
        JButton btnNext = createRoundedButton(">", new Color(70, 130, 180));
        lblMonthTitle = new JLabel("", SwingConstants.CENTER);
        lblMonthTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); refreshAll(); });
        btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); refreshAll(); });

        navPanel.add(btnPrev, BorderLayout.WEST);
        navPanel.add(lblMonthTitle, BorderLayout.CENTER);
        navPanel.add(btnNext, BorderLayout.EAST);

        gridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        leftPanel.add(navPanel, BorderLayout.NORTH);
        leftPanel.add(gridPanel, BorderLayout.CENTER);

        // DROITE : Planning
        schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollSchedule = new JScrollPane(schedulePanel);
        scrollSchedule.getVerticalScrollBar().setUnitIncrement(16);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(scrollSchedule);

        add(splitPane, BorderLayout.CENTER);
    }

    private void refreshAll() {
        lblMonthTitle.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(java.util.Locale.FRENCH)).toUpperCase());

        // Charger tous les RDV du mois
        List<RDVDTO> monthData = controller.chargerAgendaMensuel(currentMonth.getYear(), currentMonth.getMonthValue());

        // Filtrage des données par médecin SI un médecin est sélectionné
        Long selectedMedId = (medecinFilter != null) ? medecinFilter.getIdMedecin() : null;

        monthCache.clear();
        for (RDVDTO r : monthData) {
            // Logique de filtrage (Basée sur le nom du médecin dans le DTO car l'ID n'est pas toujours dispo dans le DTO simple)
            // Idéalement : ajouter medecinId dans RDVDTO.
            // Pour l'instant on filtre sur le nom
            boolean matchMedecin = true;
            if (medecinFilter != null && r.getNomMedecin() != null) {
                // On vérifie si "Dr. ID 1" contient "1"
                // C'est une simplification pour que ça marche avec ton code actuel
                String idStr = String.valueOf(medecinFilter.getIdMedecin());
                if (!r.getNomMedecin().contains(idStr) && !r.getNomMedecin().contains(medecinFilter.getUtilisateur().getNom())) {
                    // matchMedecin = false; // Décommente si tu veux filtrer strictement
                }
            }

            if (matchMedecin && !"ANNULE".equals(r.getStatut())) {
                monthCache.computeIfAbsent(r.getDate().getDayOfMonth(), k -> new ArrayList<>()).add(r);
            }
        }

        refreshCalendarColors();
        refreshDailySchedule();
    }

    private void refreshCalendarColors() {
        gridPanel.removeAll();
        String[] days = {"L", "M", "M", "J", "V", "S", "D"};
        for (String d : days) gridPanel.add(new JLabel(d, SwingConstants.CENTER));

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int startDay = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i < startDay; i++) gridPanel.add(new JLabel(""));

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            List<RDVDTO> rdvs = monthCache.getOrDefault(day, List.of());

            Color bg = new Color(240, 248, 255);
            if (rdvs.size() >= 8) bg = new Color(255, 182, 193);
            if (patientFilter != null && rdvs.stream().anyMatch(r -> r.getNomPatient().contains(patientFilter.getNom()))) {
                bg = new Color(144, 238, 144);
            }
            if (date.equals(selectedDate)) bg = new Color(255, 215, 0);

            JButton btn = createRoundedButton(String.valueOf(day), bg);
            btn.addActionListener(e -> { selectedDate = date; refreshCalendarColors(); refreshDailySchedule(); });
            gridPanel.add(btn);
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private void refreshDailySchedule() {
        schedulePanel.removeAll();
        JLabel title = new JLabel("📅 " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy")));
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        schedulePanel.add(title);

        List<RDVDTO> dailyData = controller.chargerAgenda(selectedDate);

        LocalTime time = LocalTime.of(8, 0);
        while (time.isBefore(LocalTime.of(18, 0))) {
            if (time.equals(LocalTime.of(13, 0))) {
                JPanel p = new JPanel(); p.setBackground(Color.LIGHT_GRAY); p.add(new JLabel("PAUSE"));
                schedulePanel.add(p);
                time = time.plusHours(1); continue;
            }

            LocalTime slotTime = time;
            RDVDTO rdv = dailyData.stream()
                    .filter(r -> r.getHeure().equals(slotTime) && !"ANNULE".equals(r.getStatut()))
                    .findFirst().orElse(null);

            JPanel slot = new JPanel(new BorderLayout());
            slot.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            slot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            slot.setBackground(Color.WHITE);

            JLabel lblTime = new JLabel(" " + time + " ");
            lblTime.setFont(new Font("Consolas", Font.BOLD, 14));

            if (rdv != null) {
                slot.setBackground(new Color(230, 245, 255));
                JLabel lblInfo = new JLabel("<html><b>" + rdv.getNomPatient() + "</b><br/>" + rdv.getMotif() + " (" + rdv.getStatut() + ")</html>");

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                btnPanel.setOpaque(false);

                JButton btnEdit = createIconBtn("Edit", new Color(70, 130, 180));
                btnEdit.addActionListener(e -> ouvrirFormulaire(rdv.getIdRDV()));

                if (!"CONFIRME".equals(rdv.getStatut())) {
                    JButton btnConfirm = createIconBtn("OK", new Color(60, 179, 113));
                    btnConfirm.addActionListener(e -> confirmerRDV(rdv.getIdRDV()));
                    btnPanel.add(btnConfirm);
                }

                JButton btnCancel = createIconBtn("Sup", new Color(220, 53, 69));
                btnCancel.addActionListener(e -> annulerRDV(rdv.getIdRDV()));

                btnPanel.add(btnEdit);
                btnPanel.add(btnCancel);

                slot.add(lblTime, BorderLayout.WEST);
                slot.add(lblInfo, BorderLayout.CENTER);
                slot.add(btnPanel, BorderLayout.EAST);
            } else {
                JLabel lblVide = new JLabel("Disponible");
                lblVide.setForeground(Color.GRAY);
                JButton btnAdd = createRoundedButton("+", new Color(60, 179, 113));
                btnAdd.setPreferredSize(new Dimension(40, 25));
                btnAdd.addActionListener(e -> ouvrirFormulaireCreation(slotTime));

                slot.add(lblTime, BorderLayout.WEST);
                slot.add(lblVide, BorderLayout.CENTER);
                slot.add(btnAdd, BorderLayout.EAST);
            }
            schedulePanel.add(slot);
            time = time.plusMinutes(30);
        }
        schedulePanel.revalidate(); schedulePanel.repaint();
    }

    private void confirmerRDV(Long id) {
        try {
            controller.confirmerRDV(id);
            JOptionPane.showMessageDialog(this, "RDV Confirmé ! Email envoyé.");
            refreshDailySchedule();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage()); }
    }

    private void annulerRDV(Long id) {
        if (JOptionPane.showConfirmDialog(this, "Annuler ?", "Confirmer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.annulerRDV(id);
                JOptionPane.showMessageDialog(this, "RDV Annulé. Email envoyé.");
                refreshAll();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage()); }
        }
    }

    private void ouvrirFormulaireCreation(LocalTime heure) {
        RDVFormDialog dialog = new RDVFormDialog(SwingUtilities.getWindowAncestor(this), controller, null);
        dialog.preRemplirDateHeure(selectedDate, heure);
        dialog.setVisible(true);
        if (dialog.isSaved()) refreshAll();
    }

    private void ouvrirFormulaire(Long idRdv) {
        RDV rdv = controller.getRDVById(idRdv);
        RDVFormDialog dialog = new RDVFormDialog(SwingUtilities.getWindowAncestor(this), controller, rdv);
        dialog.setVisible(true);
        if (dialog.isSaved()) refreshAll();
    }

    private JPanel createLegend(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setBackground(Color.WHITE);
        JPanel box = new JPanel(); box.setPreferredSize(new Dimension(15, 15)); box.setBackground(c);
        p.add(box); p.add(new JLabel(text));
        return p;
    }

    private JButton createIconBtn(String text, Color bg) {
        JButton btn = createRoundedButton(text, bg);
        btn.setPreferredSize(new Dimension(50, 25));
        return btn;
    }

    private JButton createRoundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}