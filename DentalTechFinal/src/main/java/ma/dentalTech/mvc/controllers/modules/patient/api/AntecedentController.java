package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.dto.AntecedentDTO;
import java.util.List;

public interface AntecedentController {

    // Ouvre la fenêtre de gestion pour un patient spécifique
    void openAntecedentManager(Long idPatient);

    // Actions
    void addAntecedent(Antecedent antecedent);
    void updateAntecedent(Antecedent antecedent);
    void deleteAntecedent(Long idAntecedent);

    // Récupération
    List<AntecedentDTO> getAntecedentsByPatient(Long idPatient);
}