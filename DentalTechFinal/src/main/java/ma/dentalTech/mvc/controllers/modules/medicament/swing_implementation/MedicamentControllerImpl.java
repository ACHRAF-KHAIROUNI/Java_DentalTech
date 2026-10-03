package ma.dentalTech.mvc.controllers.modules.medicament.swing_implementation;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.mvc.controllers.modules.medicament.api.MedicamentController;
import ma.dentalTech.mvc.dto.MedicamentDTO;
import ma.dentalTech.service.modules.medicament.api.MedicamentService;
import ma.dentalTech.service.modules.medicament.impl.MedicamentServiceImpl;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.stream.Collectors;
//ROBAII IKRAM
public class MedicamentControllerImpl implements MedicamentController {

    private final MedicamentService service = new MedicamentServiceImpl();

    @Override
    public void save(Medicament m) {
        try {
            service.ajouterMedicament(m);
            JOptionPane.showMessageDialog(null, "Médicament ajouté !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void update(Medicament m) {
        try {
            service.modifierMedicament(m);
            JOptionPane.showMessageDialog(null, "Modifié avec succès.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void delete(Long id) {
        if (JOptionPane.showConfirmDialog(null, "Supprimer ?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                service.supprimerMedicament(id);
                JOptionPane.showMessageDialog(null, "Supprimé.");
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public List<MedicamentDTO> listAll() {
        return service.getAllAsDTO();
    }

    @Override
    public List<Medicament> search(String keyword) {
        return service.rechercherParNom(keyword);
    }

    // --- IMPLEMENTATION DU FILTRE ---
    @Override
    public List<MedicamentDTO> filter(String text, String typeRemboursement) {
        List<MedicamentDTO> all = service.getAllAsDTO();

        return all.stream()
                .filter(m -> m.getNom().toLowerCase().contains(text.toLowerCase()))
                .filter(m -> {
                    if (typeRemboursement.equals("Tout")) return true;
                    if (typeRemboursement.equals("Remboursable")) return m.getRemboursableTexte().equals("Oui");
                    return m.getRemboursableTexte().equals("Non");
                })
                .collect(Collectors.toList());
    }
}