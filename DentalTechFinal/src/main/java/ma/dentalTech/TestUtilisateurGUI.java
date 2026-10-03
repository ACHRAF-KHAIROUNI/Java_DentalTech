package ma.dentalTech;

import com.formdev.flatlaf.FlatLightLaf; // Pour le style moderne
import ma.dentalTech.mvc.ui.modules.userManager.UtilisateurView;

import javax.swing.*;
//khairouni achraf
public class TestUtilisateurGUI {
    public static void main(String[] args) {
        // Appliquer le thème FlatLaf pour un rendu moderne
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("FlatLaf non disponible");
        }

        SwingUtilities.invokeLater(() -> {
            System.out.println("--- Lancement du module GESTION UTILISATEUR ---");
            UtilisateurView view = new UtilisateurView();
            view.setVisible(true);
        });
    }
}