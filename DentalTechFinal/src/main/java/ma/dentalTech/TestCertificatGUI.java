package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.certificat.swing_implementation.CertificatControllerImpl;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestCertificatGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Look & Feel Système (plus joli)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                System.out.println("--- Test GUI Certificat ---");

                // On simule qu'on veut gérer les certifs du Patient ID 1
                // (Assure-toi que le Patient ID 1 existe, sinon ça marchera mais sans nom)
                Long idPatientCible = 1L;

                CertificatControllerImpl controller = new CertificatControllerImpl();
                controller.openCertificatManager(idPatientCible);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}