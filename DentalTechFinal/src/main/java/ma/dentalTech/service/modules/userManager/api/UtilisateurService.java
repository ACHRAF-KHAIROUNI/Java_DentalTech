package ma.dentalTech.service.modules.userManager.api;

import ma.dentalTech.entities.userManager.Utilisateur;
import java.util.List;
//khairouni achraf
public interface UtilisateurService {
    Utilisateur save(Utilisateur utilisateur); // Création + Hashage mot de passe
    Utilisateur update(Utilisateur utilisateur);
    void delete(Long id);
    List<Utilisateur> findAll();
    List<Utilisateur> search(String keyword);
}