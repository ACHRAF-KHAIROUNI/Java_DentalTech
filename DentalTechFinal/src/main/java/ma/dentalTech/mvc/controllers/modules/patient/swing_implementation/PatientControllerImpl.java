package ma.dentalTech.mvc.controllers.modules.patient.swing_implementation;

import java.util.List;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.impl.PatientServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientControllerImpl implements PatientController {

    private PatientService service = new PatientServiceImpl();

    @Override
    public void showRecentPatients() {
        // Redirige vers la fenêtre principale pour simplifier
        openManagementWindow();
    }

    @Override
    public void openManagementWindow() {
        try {
            PatientView view = new PatientView(this);
            view.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePatient(Patient patient) {
        try {
            service.ajouterPatient(patient);
            JOptionPane.showMessageDialog(null, "Patient ajouté avec succès !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur technique : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void updatePatient(Patient patient) {
        try {
            service.modifierPatient(patient);
            JOptionPane.showMessageDialog(null, "Patient modifié avec succès.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void deletePatient(Long idPatient) {
        int confirm = JOptionPane.showConfirmDialog(null, "Confirmer la suppression ?", "Suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.supprimerPatient(idPatient);
                JOptionPane.showMessageDialog(null, "Patient supprimé.");
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(null, "Impossible de supprimer : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public List<Patient> getAllPatients() {
        try {
            return service.getAllPatients();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur de chargement : " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        try {
            return service.rechercherPatients(keyword);
        } catch (Exception e) {
            return List.of();
        }
    }
}