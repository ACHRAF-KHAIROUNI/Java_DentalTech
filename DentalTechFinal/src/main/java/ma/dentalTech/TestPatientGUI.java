package ma.dentalTech;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.controllers.modules.patient.swing_implementation.PatientControllerImpl;
import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.impl.PatientServiceImpl;

import java.time.LocalDate;

public class TestPatientGUI {

    public static void main(String[] args) {
        // 1. Initialisation
        PatientService service = new PatientServiceImpl();
        PatientController controller = new PatientControllerImpl();

        // 2. Tentative d'ajout (Protégée pour ne pas planter chez le Guest)
        try {
            System.out.println("--- Initialisation Test ---");
            String tel = "06" + System.currentTimeMillis()/1000;

            Patient p = Patient.builder()
                    .nom("VISUEL_TEST")
                    .prenom("Guest")
                    .dateDeNaissance(LocalDate.of(1990, 1, 1))
                    .sexe(Sexe.HOMME)
                    .assurance(Assurance.CNSS)
                    .telephone(tel)
                    .dateCreation(LocalDate.now())
                    .build();

            service.ajouterPatient(p);
            System.out.println("Patient ajouté.");

        } catch (Exception e) {
            System.err.println("Info: Impossible d'ajouter (Doublon ou DB Host). On continue.");
        }

        // 3. Lancement de la Vue Complète
        System.out.println("--- Ouverture de la fenêtre de Gestion ---");
        try {
            // Ceci ouvre la PatientView complète
            controller.openManagementWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}