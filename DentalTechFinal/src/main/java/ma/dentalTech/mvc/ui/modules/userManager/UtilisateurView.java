package ma.dentalTech.mvc.ui.modules.userManager;

import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.userManager.api.UtilisateurController;
import ma.dentalTech.mvc.controllers.modules.userManager.impl.UtilisateurControllerImpl;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
//khairouni achraf
public class UtilisateurView extends JFrame {

    private final UtilisateurController controller;

    // Composants
    private JTextField txtId, txtNom, txtEmail, txtLogin, txtCin, txtTel;
    private JTextArea txtAdresse;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JComboBox<Sexe> cbSexe;
    private JTable table;
    private DefaultTableModel model;

    public UtilisateurView() {
        this.controller = new UtilisateurControllerImpl();
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setTitle("Gestion Avancée des Utilisateurs");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel();
        header.setBackground(Theme.MAIN_BLUE);
        JLabel lblTitle = new JLabel("Administration RH & Utilisateurs");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // --- FORMULAIRE (Gauche) ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Theme.PANEL_BG);
        formPanel.setPreferredSize(new Dimension(350, 0));

        // Champs
        txtId = new JTextField(); txtId.setEditable(false);
        txtNom = new JTextField();
        txtCin = new JTextField();
        txtTel = new JTextField();
        txtEmail = new JTextField();
        txtAdresse = new JTextArea(3, 20); txtAdresse.setLineWrap(true);
        txtLogin = new JTextField();
        txtPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{"ADMINISTRATEUR", "SECRETAIRE", "MEDECIN"});
        cbSexe = new JComboBox<>(Sexe.values());

        // Ajout des champs
        addFormItem(formPanel, "ID", txtId);
        addFormItem(formPanel, "Nom Complet (*)", txtNom);
        addFormItem(formPanel, "C.I.N", txtCin);
        addFormItem(formPanel, "Téléphone", txtTel);
        addFormItem(formPanel, "Email (*)", txtEmail);
        addFormItem(formPanel, "Adresse", new JScrollPane(txtAdresse));
        addFormItem(formPanel, "Sexe", cbSexe);
        addFormItem(formPanel, "Rôle", cbRole);
        formPanel.add(new JSeparator());
        addFormItem(formPanel, "Login (*)", txtLogin);
        addFormItem(formPanel, "Mot de Passe", txtPassword);

        // --- BOUTONS ---
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton btnAdd = new JButton("Enregistrer");
        JButton btnUpdate = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnClear = new JButton("Nouveau");

        styleBtn(btnAdd, new Color(46, 204, 113));
        styleBtn(btnUpdate, new Color(52, 152, 219));
        styleBtn(btnDelete, new Color(231, 76, 60));
        styleBtn(btnClear, new Color(149, 165, 166));

        // Listeners
        btnAdd.addActionListener(e -> saveUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());

        // --- C'EST ICI QUE J'AVAIS OUBLIÉ L'AJOUT ! ---
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        // ----------------------------------------------

        // Conteneur Gauche (Formulaire + Boutons)
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        leftContainer.add(btnPanel, BorderLayout.SOUTH);
        leftContainer.setBorder(BorderFactory.createTitledBorder("Détails Utilisateur"));

        add(leftContainer, BorderLayout.WEST);

        // --- TABLEAU (Centre) ---
        String[] cols = {"ID", "Nom", "CIN", "Tél", "Email", "Rôle", "Login"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillForm();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addFormItem(JPanel p, String label, JComponent c) {
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Taille fixe pour éviter que les champs ne s'étirent trop verticalement
        Dimension dim = new Dimension(Integer.MAX_VALUE, 30);
        if (c instanceof JTextField || c instanceof JComboBox) {
            c.setMaximumSize(dim);
        }

        p.add(l);
        p.add(c);
        p.add(Box.createVerticalStrut(5));
    }

    private void styleBtn(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
    }

    // --- LOGIQUE METIER ---

    private void refreshTable() {
        model.setRowCount(0);
        List<Utilisateur> list = controller.getAllUsers();
        for (Utilisateur u : list) {
            model.addRow(new Object[]{
                    u.getIdUser(),
                    u.getNom(),
                    u.getCin(),
                    u.getTel(),
                    u.getEmail(),
                    getRoleNameById(u.getIdRole()),
                    u.getLogin()
            });
        }
    }

    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() || txtLogin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Les champs Nom, Email et Login sont obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Utilisateur buildUserFromForm() {
        Utilisateur u = new Utilisateur();
        if(!txtId.getText().isEmpty()) u.setIdUser(Long.parseLong(txtId.getText()));

        u.setNom(txtNom.getText());
        u.setCin(txtCin.getText());
        u.setTel(txtTel.getText());
        u.setEmail(txtEmail.getText());
        u.setAdresse(txtAdresse.getText());
        u.setLogin(txtLogin.getText());
        u.setSexe((Sexe) cbSexe.getSelectedItem());
        u.setIdRole(getRoleIdByName((String) cbRole.getSelectedItem()));
        u.setIdCabinet(1L);

        String pwd = new String(txtPassword.getPassword());
        if(!pwd.isEmpty()) u.setMotDePasse(pwd);

        return u;
    }

    private void saveUser() {
        if (!validateForm()) return;
        if (new String(txtPassword.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire pour la création.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.addUser(buildUserFromForm());
        refreshTable();
        clearForm();
    }

    private void updateUser() {
        if (txtId.getText().isEmpty()) return;
        if (!validateForm()) return;
        controller.updateUser(buildUserFromForm());
        refreshTable();
        clearForm();
    }

    private void deleteUser() {
        if(txtId.getText().isEmpty()) return;
        if(JOptionPane.showConfirmDialog(this, "Supprimer cet utilisateur ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteUser(Long.parseLong(txtId.getText()));
            refreshTable();
            clearForm();
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNom.setText(""); txtCin.setText(""); txtTel.setText("");
        txtEmail.setText(""); txtAdresse.setText(""); txtLogin.setText(""); txtPassword.setText("");
        table.clearSelection();
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if(row == -1) return;

        Long id = (Long) model.getValueAt(row, 0);
        Utilisateur u = controller.getAllUsers().stream().filter(user -> user.getIdUser().equals(id)).findFirst().orElse(null);

        if(u != null) {
            txtId.setText(String.valueOf(u.getIdUser()));
            txtNom.setText(u.getNom());
            txtCin.setText(u.getCin());
            txtTel.setText(u.getTel());
            txtEmail.setText(u.getEmail());
            txtAdresse.setText(u.getAdresse());
            txtLogin.setText(u.getLogin());
            if(u.getSexe() != null) cbSexe.setSelectedItem(u.getSexe());
            cbRole.setSelectedItem(getRoleNameById(u.getIdRole()));
        }
    }

    // --- HELPER METHODS ---
    private String getRoleNameById(Long id) {
        if(id == null) return "AUCUN";
        if(id == 1L) return "ADMINISTRATEUR";
        if(id == 2L) return "MEDECIN";
        if(id == 3L) return "SECRETAIRE";
        return "Role #" + id;
    }

    private Long getRoleIdByName(String name) {
        if(name == null) return 3L;
        switch (name) {
            case "ADMINISTRATEUR": return 1L;
            case "MEDECIN": return 2L;
            case "SECRETAIRE": return 3L;
            default: return 3L;
        }
    }
}