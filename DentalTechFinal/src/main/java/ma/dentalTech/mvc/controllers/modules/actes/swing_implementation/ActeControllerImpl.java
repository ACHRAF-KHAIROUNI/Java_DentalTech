/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.actes.swing_implementation;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.mvc.controllers.modules.actes.api.ActeController;
import ma.dentalTech.mvc.dto.ActeDTO;
import ma.dentalTech.service.modules.actes.api.ActeService;
import ma.dentalTech.service.modules.actes.impl.ActeServiceImpl;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.stream.Collectors;

public class ActeControllerImpl implements ActeController {

    private final ActeService service = new ActeServiceImpl();

    @Override
    public void create(Acte acte) {
        try {
            service.ajouterActe(acte);
            JOptionPane.showMessageDialog(null, "Acte ajouté !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void update(Acte acte) {
        try {
            service.modifierActe(acte);
            JOptionPane.showMessageDialog(null, "Acte modifié.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try {
            service.supprimerActe(id);
            JOptionPane.showMessageDialog(null, "Acte supprimé.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public List<ActeDTO> listAll() {
        return service.getAllAsDTO();
    }

    // --- IMPLEMENTATION DU FILTRE ---
    @Override
    public List<ActeDTO> filter(String text, String categorie) {
        return service.getAllAsDTO().stream()
                .filter(a -> a.getLibelle().toLowerCase().contains(text.toLowerCase()))
                .filter(a -> {
                    if (categorie.equals("Toutes")) return true;
                    return a.getCategorie().equals(categorie);
                })
                .collect(Collectors.toList());
    }
}