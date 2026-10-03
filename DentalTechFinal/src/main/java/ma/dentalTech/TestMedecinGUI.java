package ma.dentalTech;

import com.formdev.flatlaf.FlatLightLaf;
import ma.dentalTech.mvc.ui.modules.userManager.MedecinView;

import javax.swing.*;
//khairouni achraf
public class TestMedecinGUI {
    public static void main(String[] args) {
        // 1. Appliquer le thème moderne (FlatLaf)
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10); // Arrondir les boutons
            UIManager.put("Component.arc", 10); // Arrondir les champs
        } catch (Exception e) {
            System.err.println("FlatLaf non disponible, utilisation du thème par défaut.");
        }

        // 2. Lancer l'interface dans le Thread Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            System.out.println("--- Lancement du module GESTION MEDECINS ---");

            // Instanciation de la vue Médecin
            MedecinView view = new MedecinView();

            // Affichage
            view.setVisible(true);
        });
    }
}