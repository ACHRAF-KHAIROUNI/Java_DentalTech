/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.ui.modules.actes;

import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.mvc.controllers.modules.actes.api.ActeController;
import ma.dentalTech.mvc.dto.ActeDTO;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ActeView extends JFrame {

    private final ActeController controller;
    private JTextField txtId, txtLibelle, txtCode, txtPrix;
    private JComboBox<String> cbCategorie;
    private JTextField txtSearchActe;
    private JComboBox<String> cbFilterCat;
    private JTable table;
    private DefaultTableModel model;

    public ActeView(ActeController controller) {
        this.controller = controller;
        setTitle("Gestion des Actes Médicaux");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Section
        JPanel header = new JPanel();
        header.setBackground(Theme.MAIN_BLUE);
        JLabel title = new JLabel("Référentiel des Actes (NGAP)");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Left Form Section
        initForm();

        // Center Section (Filter + Table)
        initCenter();

        refreshWithFilter();
    }

    private void initForm() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(320, 0));

        // Fields
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 15));
        form.setBorder(BorderFactory.createTitledBorder("Détails Acte"));
        form.setBackground(new Color(240, 248, 255));

        txtId = new JTextField(); txtId.setEditable(false);
        txtLibelle = new JTextField();
        txtCode = new JTextField();
        txtPrix = new JTextField();
        // Liste complète des catégories
        String[] cats = {"Soins Conservateurs", "Chirurgie", "Prothèse", "Orthodontie", "Parodontologie", "Radiologie", "Implantologie", "Prévention", "Consultation", "Autre"};
        cbCategorie = new JComboBox<>(cats);

        form.add(new JLabel("ID :")); form.add(txtId);
        form.add(new JLabel("Libellé :")); form.add(txtLibelle);
        form.add(new JLabel("Catégorie :")); form.add(cbCategorie);
        form.add(new JLabel("Code :")); form.add(txtCode);
        form.add(new JLabel("Prix :")); form.add(txtPrix);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton btnAdd = createRoundedButton("Enregistrer", new Color(0, 128, 0));
        JButton btnDel = createRoundedButton("Supprimer", new Color(128, 0, 0));

        btnAdd.addActionListener(e -> save());
        btnDel.addActionListener(e -> delete());

        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        leftPanel.add(form, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
    }

    private void initCenter() {
        JPanel pCenter = new JPanel(new BorderLayout());

        // Filter Panel
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pSearch.setBackground(new Color(230, 230, 230));
        pSearch.setBorder(BorderFactory.createEtchedBorder());

        txtSearchActe = new JTextField(15);
        String[] catsFilter = {"Toutes", "Soins Conservateurs", "Chirurgie", "Prothèse", "Orthodontie", "Parodontologie", "Radiologie", "Implantologie", "Consultation"};
        cbFilterCat = new JComboBox<>(catsFilter);

        JButton btnChercher = createRoundedButton("Filtrer", new Color(70, 130, 180));
        btnChercher.setPreferredSize(new Dimension(100, 30));
        btnChercher.addActionListener(e -> refreshWithFilter());

        pSearch.add(new JLabel("Acte :")); pSearch.add(txtSearchActe);
        pSearch.add(new JLabel("Catégorie :")); pSearch.add(cbFilterCat);
        pSearch.add(btnChercher);

        pCenter.add(pSearch, BorderLayout.NORTH);

        // Table Configuration
        String[] cols = {"ID", "Libellé", "Catégorie", "Code", "Prix Base"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { fill(); }
        });

        pCenter.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pCenter, BorderLayout.CENTER);
    }

    private void refreshWithFilter() {
        model.setRowCount(0);
        List<ActeDTO> list = controller.filter(txtSearchActe.getText(), (String) cbFilterCat.getSelectedItem());
        for(ActeDTO a : list) model.addRow(new Object[]{a.getId(), a.getLibelle(), a.getCategorie(), a.getCode(), a.getPrix()});
    }

    private void save() {
        try {
            if (txtLibelle.getText().isEmpty()) return;
            Acte a = Acte.builder()
                    .libelle(txtLibelle.getText())
                    .categorie((String) cbCategorie.getSelectedItem())
                    .code(Integer.parseInt(txtCode.getText()))
                    .prixDeBase(Double.parseDouble(txtPrix.getText().replace(",",".")))
                    .description("")
                    .build();

            if(txtId.getText().isEmpty()) controller.create(a);
            else {
                a.setIdActe(Long.parseLong(txtId.getText()));
                controller.update(a);
            }
            refreshWithFilter();
            txtId.setText(""); txtLibelle.setText(""); txtCode.setText(""); txtPrix.setText("");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur données (Format Prix ou Code incorrect)"); }
    }

    private void delete() {
        if(!txtId.getText().isEmpty()) {
            controller.delete(Long.parseLong(txtId.getText()));
            refreshWithFilter();
        }
    }

    private void fill() {
        int r = table.getSelectedRow();
        if(r != -1) {
            txtId.setText(model.getValueAt(r, 0).toString());
            txtLibelle.setText(model.getValueAt(r, 1).toString());
            cbCategorie.setSelectedItem(model.getValueAt(r, 2).toString());
            txtCode.setText(model.getValueAt(r, 3).toString());
            txtPrix.setText(model.getValueAt(r, 4).toString().replace(" DH", "").replace(",", "."));
        }
    }

    private JButton createRoundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Rounded Corners (20px)
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30));
        return btn;
    }
}