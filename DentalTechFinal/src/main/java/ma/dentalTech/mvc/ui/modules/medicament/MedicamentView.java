package ma.dentalTech.mvc.ui.modules.medicament;

import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.mvc.controllers.modules.medicament.api.MedicamentController;
import ma.dentalTech.mvc.dto.MedicamentDTO;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
//ROBAII IKRAM
public class MedicamentView extends JFrame {

    private final MedicamentController controller;

    // Composants UI
    private JTextField txtId, txtNom, txtLabo, txtPrix;
    private JComboBox<String> cbType;
    private JCheckBox chkRemboursable;
    private JTextField txtSearch;
    private JComboBox<String> cbFilterRemb;
    private JTable table;
    private DefaultTableModel model;

    public MedicamentView(MedicamentController controller) {
        this.controller = controller;
        setTitle("💊 Gestion des Médicaments");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(Theme.MAIN_BLUE);
        JLabel title = new JLabel("Catalogue Médicaments");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // --- GAUCHE : FORMULAIRE ---
        initLeftPanel();

        // --- CENTRE : TABLEAU ---
        initCenterPanel();

        // Chargement initial
        refreshTableWithFilter();
    }

    private void initLeftPanel() {
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setPreferredSize(new Dimension(320, 0));
        leftContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulaire
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations"));
        formPanel.setBackground(new Color(240, 248, 255));

        txtId = new JTextField(); txtId.setEditable(false);
        txtNom = new JTextField();
        txtLabo = new JTextField();
        txtPrix = new JTextField();
        String[] types = {"Antibiotique", "Antalgique", "Anti-inflammatoire", "Anesthésique", "Autre"};
        cbType = new JComboBox<>(types);
        chkRemboursable = new JCheckBox("Remboursable");
        chkRemboursable.setBackground(new Color(240, 248, 255));

        addToForm(formPanel, "ID :", txtId);
        addToForm(formPanel, "Nom :", txtNom);
        addToForm(formPanel, "Labo :", txtLabo);
        addToForm(formPanel, "Prix (DH) :", txtPrix);
        addToForm(formPanel, "Type :", cbType);
        formPanel.add(new JLabel(""));
        formPanel.add(chkRemboursable);

        // Boutons CRUD stylisés
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        JButton btnAdd = createRoundedButton("Ajouter", new Color(0, 128, 0)); // Vert Foncé
        JButton btnUpd = createRoundedButton("Modifier", new Color(0, 0, 128)); // Bleu Marine
        JButton btnDel = createRoundedButton("Supprimer", new Color(128, 0, 0)); // Rouge Foncé

        btnAdd.addActionListener(e -> save());
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpd);
        btnPanel.add(btnDel);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        leftContainer.add(formPanel, BorderLayout.NORTH);
        leftContainer.add(btnPanel, BorderLayout.CENTER);

        add(leftContainer, BorderLayout.WEST);
    }

    private void initCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        // --- FILTRES ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(230, 230, 230));
        filterPanel.setBorder(BorderFactory.createEtchedBorder());

        txtSearch = new JTextField(15);
        cbFilterRemb = new JComboBox<>(new String[]{"Tout", "Remboursable", "Non"});

        JButton btnSearch = createRoundedButton("Rechercher", new Color(70, 130, 180));
        btnSearch.setPreferredSize(new Dimension(120, 35));
        btnSearch.addActionListener(e -> refreshTableWithFilter());

        filterPanel.add(new JLabel("Nom :")); filterPanel.add(txtSearch);
        filterPanel.add(new JLabel(" Filtre :")); filterPanel.add(cbFilterRemb);
        filterPanel.add(btnSearch);

        // --- TABLEAU ---
        String[] cols = {"ID", "Nom", "Labo", "Type", "Prix", "Remb."};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(220, 220, 220));

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillForm(); }
        });

        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    // --- LOGIQUE ---

    private void refreshTableWithFilter() {
        model.setRowCount(0);
        List<MedicamentDTO> list = controller.filter(txtSearch.getText(), (String) cbFilterRemb.getSelectedItem());
        for (MedicamentDTO m : list) {
            model.addRow(new Object[]{m.getId(), m.getNom(), m.getLaboratoire(), m.getType(), m.getPrixAffiche(), m.getRemboursableTexte()});
        }
    }

    private void save() {
        try {
            controller.save(build());
            refreshTableWithFilter();
            clear();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur de saisie (Prix ?)"); }
    }

    private void update() {
        if(txtId.getText().isEmpty()) return;
        try {
            Medicament m = build();
            m.setIdMct(Long.parseLong(txtId.getText()));
            controller.update(m);
            refreshTableWithFilter();
            clear();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur"); }
    }

    private void delete() {
        if(!txtId.getText().isEmpty()) {
            controller.delete(Long.parseLong(txtId.getText()));
            refreshTableWithFilter();
            clear();
        }
    }

    private Medicament build() {
        return Medicament.builder()
                .nom(txtNom.getText())
                .laboratoire(txtLabo.getText())
                .type((String) cbType.getSelectedItem())
                .prixUnitaire(Double.parseDouble(txtPrix.getText().replace(",",".")))
                .remboursable(chkRemboursable.isSelected())
                .description("RAS")
                .build();
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if(r != -1) {
            txtId.setText(model.getValueAt(r, 0).toString());
            txtNom.setText(model.getValueAt(r, 1).toString());
            txtLabo.setText(model.getValueAt(r, 2).toString());
            cbType.setSelectedItem(model.getValueAt(r, 3).toString());
            txtPrix.setText(model.getValueAt(r, 4).toString().replace(" DH", "").replace(",", "."));
            chkRemboursable.setSelected(model.getValueAt(r, 5).toString().equals("Oui"));
        }
    }

    private void clear() {
        txtId.setText(""); txtNom.setText(""); txtLabo.setText(""); txtPrix.setText("");
        table.clearSelection();
    }

    // --- OUTILS GRAPHIQUES ---

    private void addToForm(JPanel p, String text, JComponent comp) {
        p.add(new JLabel(text));
        p.add(comp);
    }

    private JButton createRoundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Coins arrondis (20px)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Police petite
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30)); // Taille compacte
        return btn;
    }
}