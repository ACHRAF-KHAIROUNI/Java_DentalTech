package ma.dentalTech.mvc.controllers;

import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.mvc.ui.dashboard.DashboardView;
import ma.dentalTech.mvc.ui.login.LoginView;
import ma.dentalTech.service.modules.userManager.impl.UtilisateurServiceImpl;

import javax.swing.*;

public class LoginController {

    private final LoginView view;
    private final UtilisateurServiceImpl service;

    public LoginController(LoginView view) {
        this.view = view;
        this.service = new UtilisateurServiceImpl();
    }

    public void authenticate(String login, String password) {
        try {
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Utilisateur user = service.login(login, password);

            if (user != null) {
                // Succès : Fermer le login et ouvrir le Dashboard
                view.dispose();
                SwingUtilities.invokeLater(() -> new DashboardView(user).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(view, "Identifiant ou mot de passe incorrect.", "Erreur d'authentification", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Erreur système : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}