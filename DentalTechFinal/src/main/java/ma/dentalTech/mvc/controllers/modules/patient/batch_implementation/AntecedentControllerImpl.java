package ma.dentalTech.mvc.controllers.modules.patient.batch_implementation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.dto.AntecedentDTO;
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.impl.AntecedentServiceImpl;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AntecedentControllerImpl implements AntecedentController {

    private AntecedentService service = new AntecedentServiceImpl();

    @Override
    public void openAntecedentManager(Long idPatient) {
        System.out.println(">>> BATCH: Gestion Antécédents pour Patient ID " + idPatient);
    }

    @Override
    public void addAntecedent(Antecedent antecedent) {
        try {
            service.ajouterAntecedent(antecedent);
            System.out.println("[OK] Antécédent ajouté : " + antecedent.getNom());
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }

    @Override
    public void updateAntecedent(Antecedent antecedent) {
        try {
            service.modifierAntecedent(antecedent);
            System.out.println("[OK] Antécédent modifié.");
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }

    @Override
    public void deleteAntecedent(Long idAntecedent) {
        try {
            service.supprimerAntecedent(idAntecedent);
            System.out.println("[OK] Antécédent supprimé.");
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }

    @Override
    public List<AntecedentDTO> getAntecedentsByPatient(Long idPatient) {
        return List.of();
    }
}