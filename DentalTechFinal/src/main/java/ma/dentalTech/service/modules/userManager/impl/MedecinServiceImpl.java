package ma.dentalTech.service.modules.userManager.impl;

import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.repository.modules.userManager.api.MedecinRepository;
import ma.dentalTech.repository.modules.userManager.impl.mySQL.MedecinRepositoryImpl;
import ma.dentalTech.service.modules.userManager.api.UtilisateurService;

import java.util.List;
//khairouni achraf
public class MedecinServiceImpl {

    private final MedecinRepository medecinRepo = new MedecinRepositoryImpl();
    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    public List<Medecin> findAll() {
        return medecinRepo.findAll();
    }

    public void save(Medecin medecin) {
        // 1. Créer d'abord l'Utilisateur
        Utilisateur u = medecin.getUtilisateur();
        u.setIdRole(2L); // 2 = MEDECIN (Selon votre seed.sql corrigé)

        // Sauvegarde de l'utilisateur (hashage mdp géré par le service utilisateur)
        Utilisateur savedUser = utilisateurService.save(u);

        // 2. Lier le Médecin à cet Utilisateur
        medecin.setIdUser(savedUser.getIdUser());

        // 3. Créer le Médecin
        medecinRepo.create(medecin);
    }

    public void update(Medecin medecin) {
        // Mise à jour de la spécialité
        medecinRepo.update(medecin);
        // Mise à jour des infos perso (Nom, Email...) via le service utilisateur
        utilisateurService.update(medecin.getUtilisateur());
    }

    public void delete(Long idMedecin, Long idUser) {
        // Suppression du médecin d'abord (FK)
        medecinRepo.delete(idMedecin);
        // Suppression de l'utilisateur ensuite (Optionnel, selon votre règle métier)
        if(idUser != null) utilisateurService.delete(idUser);
    }
}