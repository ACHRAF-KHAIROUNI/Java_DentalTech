package ma.dentalTech;

import ma.dentalTech.mvc.ui.modules.ordonnance.OrdonnanceView;
import ma.dentalTech.mvc.ui.modules.rdvPatient.RDVCalendarView;
import ma.dentalTech.mvc.ui.modules.rdvPatient.RDVView;

import javax.swing.*;
import java.awt.*;

//ROBAII IKRAM

public class TestGUI {

    public static void main(String[] args) {
        // Appliquer le look système pour que ce soit joli (Windows/Mac/Linux style)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // Fenêtre principale
            JFrame frame = new JFrame("DENTAL TECH - MODULE TESTEUR");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null); // Centrer

            // --- MENU DE NAVIGATION (Haut) ---
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

            // --- ONGLET 1 : RDV ---
            // On crée l'instance de la vue RDV
            JPanel panelRDV = new RDVCalendarView();
            tabbedPane.addTab("📅 Gestion des Rendez-vous", null, panelRDV, "Gérer l'agenda et les patients");

            // --- ONGLET 2 : ORDONNANCE ---
            // On crée l'instance de la vue Ordonnance
            JPanel panelOrdonnance = new OrdonnanceView();
            tabbedPane.addTab("💊 Gestion des Ordonnances", null, panelOrdonnance, "Prescrire des médicaments");

            // Ajout du tout à la fenêtre
            frame.add(tabbedPane);

            frame.setVisible(true);
        });
    }
}