package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.statistiques.swing_implementation.StatistiqueControllerImpl;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestStatistiqueGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("--- Stats Financières ---");

                StatistiqueControllerImpl controller = new StatistiqueControllerImpl();
                controller.openStatistics();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}