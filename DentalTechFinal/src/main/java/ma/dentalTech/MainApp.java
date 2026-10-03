package ma.dentalTech;

import com.formdev.flatlaf.FlatLightLaf;
import ma.dentalTech.mvc.ui.login.LoginView;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Appliquer le thème FlatLaf globalement
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            System.err.println("Erreur chargement thème");
        }

        // Lancer l'application
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });

        interface test{
            int somme(int a, int b) ;
        }
    }
}