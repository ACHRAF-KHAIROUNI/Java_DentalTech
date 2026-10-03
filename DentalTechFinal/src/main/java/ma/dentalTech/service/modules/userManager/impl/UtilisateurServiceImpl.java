package ma.dentalTech.service.modules.userManager.impl;

import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.repository.modules.userManager.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.userManager.impl.mySQL.UtilisateurRepositoryImpl;
import ma.dentalTech.service.modules.userManager.api.UtilisateurService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
//khairouni achraf
public class UtilisateurServiceImpl implements UtilisateurService {

    // Injection du repository
    private final UtilisateurRepository repo = new UtilisateurRepositoryImpl();

    @Override
    public Utilisateur save(Utilisateur u) {
        // 1. On ne hache PLUS le mot de passe, on le garde tel quel
        // (L'ancien code BCrypt est supprimé ici)

        u.setDateCreation(LocalDate.now());
        repo.create(u);
        return u;
    }

    @Override
    public Utilisateur update(Utilisateur u) {
        // Mise à jour standard
        repo.update(u);
        return u;
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Utilisateur> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Utilisateur> search(String keyword) {
        return findAll(); // Ou implémentation spécifique de recherche
    }

    /**
     * Méthode de connexion SIMPLE (Sans hachage)
     */
    public Utilisateur login(String login, String password) {
        // 1. Chercher l'utilisateur par son login
        Optional<Utilisateur> userOpt = repo.findByLogin(login);

        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();

            // 2. Vérification simple : Texte contre Texte
            // Attention aux espaces vides éventuels en base de données (.trim())
            String dbPass = user.getMotDePasse();

            if (dbPass != null && dbPass.equals(password)) {
                return user; // Succès !
            }
        }
        return null; // Échec (Login introuvable ou mauvais mot de passe)
    }
}