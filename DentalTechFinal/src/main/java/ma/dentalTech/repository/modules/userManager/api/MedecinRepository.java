package ma.dentalTech.repository.modules.userManager.api;

import ma.dentalTech.entities.userManager.Medecin;
import java.util.List;

public interface MedecinRepository {
    List<Medecin> findAll();
    void create(Medecin medecin);
    void update(Medecin medecin);
    void delete(Long idMedecin);
}