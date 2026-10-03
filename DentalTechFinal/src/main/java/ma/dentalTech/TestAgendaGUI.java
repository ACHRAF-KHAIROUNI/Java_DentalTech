package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.agenda.swing_implementation.AgendaControllerImpl;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestAgendaGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("--- Test GUI Agenda ---");

                // On utilise le médecin 1 (qui existe dans ta base)
                Long idMedecin = 1L;

                AgendaControllerImpl controller = new AgendaControllerImpl();
                controller.openManagementWindow(idMedecin);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}