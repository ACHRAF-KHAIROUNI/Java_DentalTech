package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.dashboard.swing_implementation.DashboardControllerImpl;

import javax.swing.*;

public class TestDashboardGUI {

    public static void main(String[] args) {
        // On utilise SwingUtilities pour respecter le Thread graphique
        SwingUtilities.invokeLater(() -> {
            try {
                // Mettre le look & feel du système (Windows/Mac) pour que ce soit joli
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                System.out.println("--- 🚀 Démarrage du Module Dashboard ---");

                // 1. Instancier le contrôleur
                DashboardControllerImpl controller = new DashboardControllerImpl();

                // 2. Ouvrir le Dashboard
                controller.openDashboard(1L);

                System.out.println("--- ✅ Fenêtre ouverte ---");

            } catch (Exception e) {
                System.err.println("❌ Erreur lors du lancement : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}