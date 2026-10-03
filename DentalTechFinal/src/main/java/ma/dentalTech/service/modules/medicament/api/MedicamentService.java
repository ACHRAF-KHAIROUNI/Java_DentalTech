package ma.dentalTech.service.modules.medicament.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.mvc.dto.MedicamentDTO;

import java.util.List;

public interface MedicamentService {

    void ajouterMedicament(Medicament m) throws ServiceException;

    void modifierMedicament(Medicament m) throws ServiceException;

    void supprimerMedicament(Long id) throws ServiceException;

    List<Medicament> getAllMedicaments();

    List<Medicament> rechercherParNom(String keyword);

    // Pour l'affichage UI
    List<MedicamentDTO> getAllAsDTO();
}