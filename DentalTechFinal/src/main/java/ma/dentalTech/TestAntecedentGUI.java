package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.patient.swing_implementation.AntecedentControllerImpl;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestAntecedentGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("--- Test Antécédents GUI ---");

                // On utilise le Patient ID 1 (qui existe dans ta base)
                Long idPatientCible = 1L;

                AntecedentControllerImpl controller = new AntecedentControllerImpl();
                controller.openAntecedentManager(idPatientCible);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}