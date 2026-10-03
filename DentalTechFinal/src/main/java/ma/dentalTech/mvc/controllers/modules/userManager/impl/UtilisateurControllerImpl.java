package ma.dentalTech.mvc.controllers.modules.userManager.impl;

import ma.dentalTech.entities.userManager.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.userManager.api.UtilisateurController;
import ma.dentalTech.service.modules.userManager.api.UtilisateurService;
import ma.dentalTech.service.modules.userManager.impl.UtilisateurServiceImpl;

import javax.swing.*;
import java.util.List;

public class UtilisateurControllerImpl implements UtilisateurController {

    private final UtilisateurService service = new UtilisateurServiceImpl();

    @Override
    public void addUser(Utilisateur u) {
        try {
            service.save(u);
            JOptionPane.showMessageDialog(null, "Utilisateur créé avec succès !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage());
        }
    }

    @Override
    public void updateUser(Utilisateur u) {
        service.update(u);
        JOptionPane.showMessageDialog(null, "Utilisateur modifié !");
    }

    @Override
    public void deleteUser(Long id) {
        int confirm = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cet utilisateur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.delete(id);
        }
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        return service.findAll();
    }
}