package ma.dentalTech.mvc.ui.modules.rdvPatient;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.api.RDVController;
import ma.dentalTech.mvc.dto.RDVDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
//ROBAII IKRAM
public class RDVView extends JPanel {

    // Composants graphiques
    private JTable tableRDV;
    private DefaultTableModel tableModel;
    private JButton btnAjouter, btnModifier, btnAnnuler, btnConfirmer;
    private JTextField txtDate; // Format YYYY-MM-DD
    private JButton btnRechercher;

    // Contrôleur
    private final RDVController controller;

    // Données en mémoire (pour retrouver l'ID lors du clic)
    private List<RDVDTO> currentList;

    public RDVView() {
        // 1. Récupération du contrôleur via le Contexte
        this.controller = ApplicationContext.getInstance().getRdvController();

        // 2. Initialisation de l'interface
        initUI();

        // 3. Chargement initial (Aujourd'hui)
        chargerDonnees(LocalDate.now());
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- HAUT : Barre d'outils ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Filtres & Actions"));

        txtDate = new JTextField(LocalDate.now().toString(), 10);
        btnRechercher = new JButton("📅 Changer Date");

        btnAjouter = new JButton("➕ Nouveau RDV");
        btnAjouter.setBackground(new Color(144, 238, 144)); // Vert clair

        btnModifier = new JButton("✏️ Modifier");
        btnConfirmer = new JButton("✅ Confirmer");
        btnConfirmer.setBackground(new Color(173, 216, 230)); // Bleu clair

        btnAnnuler = new JButton("❌ Annuler");
        btnAnnuler.setBackground(new Color(255, 182, 193)); // Rouge clair

        topPanel.add(new JLabel("Date :"));
        topPanel.add(txtDate);
        topPanel.add(btnRechercher);
        topPanel.add(Box.createHorizontalStrut(20)); // Espace
        topPanel.add(btnAjouter);
        topPanel.add(btnModifier);
        topPanel.add(btnConfirmer);
        topPanel.add(btnAnnuler);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTRE : Tableau ---
        String[] colonnes = {"ID", "Heure", "Patient", "Médecin", "Motif", "Statut"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tableau en lecture seule
            }
        };
        tableRDV = new JTable(tableModel);
        tableRDV.setRowHeight(30);
        tableRDV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Cacher la colonne ID (utile pour la logique mais moche à voir)
        tableRDV.getColumnModel().getColumn(0).setMinWidth(0);
        tableRDV.getColumnModel().getColumn(0).setMaxWidth(0);
        tableRDV.getColumnModel().getColumn(0).setWidth(0);

        // Style pour le statut (Couleur)
        tableRDV.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        add(new JScrollPane(tableRDV), BorderLayout.CENTER);

        // --- EVENTS : Gestion des clics ---
        initEvents();
    }

    private void initEvents() {
        // Clic sur "Rechercher"
        btnRechercher.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(txtDate.getText());
                chargerDonnees(date);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide (YYYY-MM-DD)", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clic sur "Ajouter"
        btnAjouter.addActionListener(e -> {
            // Ouvrir la fenêtre en mode CRÉATION (null)
            RDVFormDialog dialog = new RDVFormDialog(SwingUtilities.getWindowAncestor(this), controller, null);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                refresh(); // Recharger le tableau si succès
                JOptionPane.showMessageDialog(this, "Rendez-vous créé avec succès !");
            }
        });

        // Clic sur "Confirmer"
        btnConfirmer.addActionListener(e -> {
            Long id = getSelectedId();
            if (id != null) {
                try {
                    controller.confirmerRDV(id);
                    refresh(); // Recharger le tableau
                    JOptionPane.showMessageDialog(this, "RDV Confirmé !");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Clic sur "Annuler"
        btnAnnuler.addActionListener(e -> {
            Long id = getSelectedId();
            if (id != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler ce RDV ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        controller.annulerRDV(id);
                        refresh();
                        JOptionPane.showMessageDialog(this, "RDV Annulé.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Clic sur "Modifier"
        btnModifier.addActionListener(e -> {
            Long id = getSelectedId();
            if (id != null) {
                // 1. Récupérer l'objet RDV complet via le contrôleur
                ma.dentalTech.entities.rdvPatient.RDV rdvAModifier = controller.getRDVById(id);

                if (rdvAModifier != null) {
                    // 2. Ouvrir le formulaire en mode MODIFICATION (on passe l'objet)
                    RDVFormDialog dialog = new RDVFormDialog(SwingUtilities.getWindowAncestor(this), controller, rdvAModifier);
                    dialog.setVisible(true);

                    // 3. Rafraîchir si modification enregistrée
                    if (dialog.isSaved()) {
                        refresh();
                        JOptionPane.showMessageDialog(this, "Rendez-vous modifié avec succès !");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur : RDV introuvable en base.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void chargerDonnees(LocalDate date) {
        // Appel au contrôleur
        currentList = controller.chargerAgenda(date);

        // Vider le tableau
        tableModel.setRowCount(0);

        // Remplir
        for (RDVDTO dto : currentList) {
            tableModel.addRow(new Object[]{
                    dto.getIdRDV(),
                    dto.getHeure(),
                    dto.getNomPatient(),
                    dto.getNomMedecin(),
                    dto.getMotif(),
                    dto.getStatut()
            });
        }
    }

    private void refresh() {
        try {
            LocalDate date = LocalDate.parse(txtDate.getText());
            chargerDonnees(date);
        } catch (Exception e) {
            chargerDonnees(LocalDate.now());
        }
    }

    private Long getSelectedId() {
        int row = tableRDV.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rendez-vous.", "Attention", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Long) tableModel.getValueAt(row, 0); // Colonne 0 = ID caché
    }

    // --- Renderer interne pour colorer les statuts ---
    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;

            if ("CONFIRME".equals(status)) {
                c.setForeground(new Color(0, 100, 0)); // Vert foncé
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else if ("ANNULE".equals(status)) {
                c.setForeground(Color.RED);
                c.setFont(c.getFont().deriveFont(Font.ITALIC));
            } else {
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}