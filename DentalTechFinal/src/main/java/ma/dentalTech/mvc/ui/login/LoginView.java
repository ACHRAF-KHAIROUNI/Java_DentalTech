package ma.dentalTech.mvc.ui.login;

import com.formdev.flatlaf.FlatClientProperties;
import ma.dentalTech.mvc.controllers.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//khairouni achraf
public class LoginView extends JFrame {

    // --- PALETTE DE COULEURS (Cohérente avec DashboardView) ---
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Bleu moderne
    private final Color GRADIENT_START = new Color(33, 47, 61);  // Bleu nuit
    private final Color GRADIENT_END = new Color(41, 128, 185);  // Bleu plus clair
    private final Color TEXT_COLOR = new Color(80, 80, 80);

    private final LoginController controller;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView() {
        // Initialisation du contrôleur
        this.controller = new LoginController(this);
        initUI();
    }

    private void initUI() {
        setTitle("DentalTech - Connexion Sécurisée");
        setSize(900, 550); // Format rectangulaire moderne
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- PARTIE GAUCHE : BANNIERE DECORATIVE ---
        JPanel sidePanel = new GradientPanel();
        sidePanel.setPreferredSize(new Dimension(400, 550));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Logo / Icone
        JLabel lblIcon = new JLabel("🦷");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre App
        JLabel lblTitleApp = new JLabel("DentalTech");
        lblTitleApp.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitleApp.setForeground(Color.WHITE);
        lblTitleApp.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sous-titre
        JLabel lblSlogan = new JLabel("<html><center>Gestion intelligente<br>de votre cabinet dentaire</center></html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSlogan.setForeground(new Color(220, 220, 220));
        lblSlogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajout avec espacement flexible
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(lblIcon);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(lblTitleApp);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(lblSlogan);
        sidePanel.add(Box.createVerticalGlue());

        // Copyright en bas à gauche
        JLabel lblVersion = new JLabel("ACHRAF,IKRAM,BAHA,KORCHI,Zakaria");
        lblVersion.setForeground(new Color(150, 180, 200));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(lblVersion);


        // --- PARTIE DROITE : FORMULAIRE ---
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Titre Formulaire
        JLabel lblWelcome = new JLabel("Bienvenue");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(GRADIENT_START);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(lblWelcome, gbc);

        JLabel lblInstruction = new JLabel("Veuillez vous identifier pour continuer");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruction.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        loginPanel.add(lblInstruction, gbc);

        // Champ Login
        JLabel lblUser = new JLabel("Identifiant / Email");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(TEXT_COLOR);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(lblUser, gbc);

        txtLogin = new JTextField();
        txtLogin.setPreferredSize(new Dimension(300, 40));
        txtLogin.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ex: admin");
        txtLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10"); // Arrondi + Padding interne
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        loginPanel.add(txtLogin, gbc);

        // Champ Mot de passe
        JLabel lblPass = new JLabel("Mot de passe");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(TEXT_COLOR);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10; showRevealButton: true");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 25, 0);
        loginPanel.add(txtPassword, gbc);

        // Bouton Connexion
        btnLogin = new JButton("SE CONNECTER");
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 20"); // Bouton bien arrondi

        // Effet Hover simple
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(PRIMARY_COLOR); }
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginPanel.add(btnLogin, gbc);

        // Assemblage
        add(sidePanel, BorderLayout.WEST);
        add(loginPanel, BorderLayout.CENTER);

        // Action
        btnLogin.addActionListener(e -> controller.authenticate(txtLogin.getText(), new String(txtPassword.getPassword())));
        getRootPane().setDefaultButton(btnLogin); // Touche Entrée
    }

    // --- CLASSE INTERNE POUR LE DÉGRADÉ ---
    // Cette classe crée le fond bleu dégradé à gauche
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            // Dégradé vertical du haut gauche vers le bas droite
            GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, w, h, GRADIENT_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}