package ma.dentalTech.mvc.controllers.modules.userManager.api;

import ma.dentalTech.entities.userManager.Utilisateur;
import java.util.List;

public interface UtilisateurController {
    void addUser(Utilisateur u);
    void updateUser(Utilisateur u);
    void deleteUser(Long id);
    List<Utilisateur> getAllUsers();
}