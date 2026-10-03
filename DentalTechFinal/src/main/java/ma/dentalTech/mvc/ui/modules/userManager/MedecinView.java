package ma.dentalTech.mvc.ui.modules.userManager;

import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.userManager.impl.MedecinControllerImpl;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
//khairouni achraf
public class MedecinView extends JFrame {

    private final MedecinControllerImpl controller;

    // Composants
    private JTextField txtId, txtNom, txtEmail, txtLogin, txtTel, txtCin;
    private JTextField txtSpecialite; // SPÉCIFIQUE MÉDECIN
    private JPasswordField txtPassword;
    private JComboBox<Sexe> cbSexe;
    private JTable table;
    private DefaultTableModel model;

    // Pour stocker l'ID User caché lors de la sélection
    private Long selectedUserId = null;

    public MedecinView() {
        this.controller = new MedecinControllerImpl();
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Gestion des Médecins");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185)); // Bleu un peu différent
        JLabel lblTitle = new JLabel("Gestion du Corps Médical");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // --- FORMULAIRE ---
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10)); // 9 lignes
        formPanel.setBorder(BorderFactory.createTitledBorder("Fiche Médecin"));
        formPanel.setPreferredSize(new Dimension(350, 0));
        formPanel.setBackground(Theme.PANEL_BG);

        txtId = new JTextField(); txtId.setEditable(false);
        txtNom = new JTextField();
        txtCin = new JTextField();
        txtTel = new JTextField();
        txtEmail = new JTextField();
        txtSpecialite = new JTextField(); // Nouveau champ
        txtLogin = new JTextField();
        txtPassword = new JPasswordField();
        cbSexe = new JComboBox<>(Sexe.values());

        addFormItem(formPanel, "ID Médecin:", txtId);
        addFormItem(formPanel, "Nom Dr (*):", txtNom);
        addFormItem(formPanel, "Spécialité (*):", txtSpecialite);
        addFormItem(formPanel, "C.I.N:", txtCin);
        addFormItem(formPanel, "Téléphone:", txtTel);
        addFormItem(formPanel, "Email:", txtEmail);
        addFormItem(formPanel, "Sexe:", cbSexe);
        addFormItem(formPanel, "Login (*):", txtLogin);
        addFormItem(formPanel, "Mot de Passe:", txtPassword);

        // --- BOUTONS ---
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton btnAdd = new JButton("Ajouter");
        JButton btnUpd = new JButton("Modifier");
        JButton btnDel = new JButton("Supprimer");
        JButton btnClr = new JButton("Nouveau");

        styleBtn(btnAdd, new Color(46, 204, 113));
        styleBtn(btnUpd, new Color(52, 152, 219));
        styleBtn(btnDel, new Color(231, 76, 60));
        styleBtn(btnClr, Color.GRAY);

        btnAdd.addActionListener(e -> save());
        btnUpd.addActionListener(e -> update());
        btnDel.addActionListener(e -> delete());
        btnClr.addActionListener(e -> clear());

        btnPanel.add(btnAdd); btnPanel.add(btnUpd);
        btnPanel.add(btnDel); btnPanel.add(btnClr);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);

        // --- TABLEAU ---
        String[] cols = {"ID", "Nom", "Spécialité", "Tél", "Email", "Login"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { fillForm(); }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addFormItem(JPanel p, String label, JComponent c) {
        p.add(new JLabel(label));
        p.add(c);
    }

    private void styleBtn(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
    }

    // --- LOGIQUE ---

    private void refreshTable() {
        model.setRowCount(0);
        List<Medecin> list = controller.getAllMedecins();
        for (Medecin m : list) {
            Utilisateur u = m.getUtilisateur();
            // On combine les infos du médecin et de son utilisateur
            model.addRow(new Object[]{
                    m.getIdMedecin(),
                    (u != null) ? u.getNom() : "?",
                    m.getSpecialite(),
                    (u != null) ? u.getTel() : "",
                    (u != null) ? u.getEmail() : "",
                    (u != null) ? u.getLogin() : ""
            });
        }
    }

    private void save() {
        if(txtNom.getText().isEmpty() || txtLogin.getText().isEmpty() || txtSpecialite.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants");
            return;
        }

        // Création de l'objet Utilisateur
        Utilisateur u = new Utilisateur();
        u.setNom(txtNom.getText());
        u.setCin(txtCin.getText());
        u.setTel(txtTel.getText());
        u.setEmail(txtEmail.getText());
        u.setLogin(txtLogin.getText());
        u.setMotDePasse(new String(txtPassword.getPassword()));
        u.setSexe((Sexe) cbSexe.getSelectedItem());
        u.setIdCabinet(1L);

        // Création de l'objet Médecin
        Medecin m = new Medecin();
        m.setSpecialite(txtSpecialite.getText());
        m.setUtilisateur(u); // Liaison

        controller.addMedecin(m);
        refreshTable();
        clear();
    }

    private void update() {
        if(txtId.getText().isEmpty()) return;

        Medecin m = new Medecin();
        m.setIdMedecin(Long.parseLong(txtId.getText()));
        m.setSpecialite(txtSpecialite.getText());

        Utilisateur u = new Utilisateur();
        u.setIdUser(selectedUserId); // Important pour l'update
        u.setNom(txtNom.getText());
        u.setCin(txtCin.getText());
        u.setTel(txtTel.getText());
        u.setEmail(txtEmail.getText());
        u.setLogin(txtLogin.getText());
        String pwd = new String(txtPassword.getPassword());
        if(!pwd.isEmpty()) u.setMotDePasse(pwd);
        u.setSexe((Sexe) cbSexe.getSelectedItem());

        m.setUtilisateur(u);

        controller.updateMedecin(m);
        refreshTable();
        clear();
    }

    private void delete() {
        if(txtId.getText().isEmpty()) return;
        controller.deleteMedecin(Long.parseLong(txtId.getText()), selectedUserId);
        refreshTable();
        clear();
    }

    private void clear() {
        txtId.setText(""); txtNom.setText(""); txtCin.setText("");
        txtTel.setText(""); txtEmail.setText(""); txtSpecialite.setText("");
        txtLogin.setText(""); txtPassword.setText("");
        selectedUserId = null;
        table.clearSelection();
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if(row == -1) return;

        Long idMed = (Long) model.getValueAt(row, 0);
        // Récupérer l'objet complet depuis le contrôleur pour avoir toutes les infos
        Medecin m = controller.getAllMedecins().stream()
                .filter(med -> med.getIdMedecin().equals(idMed))
                .findFirst().orElse(null);

        if (m != null && m.getUtilisateur() != null) {
            Utilisateur u = m.getUtilisateur();
            selectedUserId = u.getIdUser(); // Sauvegarde de l'ID User caché

            txtId.setText(String.valueOf(m.getIdMedecin()));
            txtSpecialite.setText(m.getSpecialite());

            txtNom.setText(u.getNom());
            txtCin.setText(u.getCin());
            txtTel.setText(u.getTel());
            txtEmail.setText(u.getEmail());
            txtLogin.setText(u.getLogin());
            if(u.getSexe() != null) cbSexe.setSelectedItem(u.getSexe());
        }
    }
}