package ma.dentalTech.mvc.controllers.modules.userManager.impl;

import ma.dentalTech.entities.userManager.Medecin;
import ma.dentalTech.service.modules.userManager.impl.MedecinServiceImpl;

import javax.swing.*;
import java.util.List;

public class MedecinControllerImpl {

    private final MedecinServiceImpl service = new MedecinServiceImpl();

    public List<Medecin> getAllMedecins() {
        return service.findAll();
    }

    public void addMedecin(Medecin m) {
        try {
            service.save(m);
            JOptionPane.showMessageDialog(null, "Médecin ajouté avec succès !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void updateMedecin(Medecin m) {
        try {
            service.update(m);
            JOptionPane.showMessageDialog(null, "Médecin modifié avec succès !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMedecin(Long idMedecin, Long idUser) {
        int confirm = JOptionPane.showConfirmDialog(null, "Supprimer ce médecin et son compte utilisateur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.delete(idMedecin, idUser);
        }
    }
}