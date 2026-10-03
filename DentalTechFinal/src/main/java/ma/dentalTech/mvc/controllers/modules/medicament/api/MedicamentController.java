package ma.dentalTech.mvc.controllers.modules.medicament.api;

import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.mvc.dto.MedicamentDTO;
import java.util.List;
//ROBAII IKRAM
public interface MedicamentController {

    // Actions CRUD
    void save(Medicament m);
    void update(Medicament m);
    void delete(Long id);

    // Affichage
    List<MedicamentDTO> listAll();
    List<Medicament> search(String keyword);

    List<MedicamentDTO> filter(String text, String typeRemboursement);
}