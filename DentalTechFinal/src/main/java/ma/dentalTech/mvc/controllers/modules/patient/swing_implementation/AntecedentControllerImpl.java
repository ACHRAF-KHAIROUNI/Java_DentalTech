package ma.dentalTech.mvc.controllers.modules.patient.swing_implementation;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.dto.AntecedentDTO;
import ma.dentalTech.mvc.ui.modules.patient.AntecedentView; // On va créer cette vue juste après
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.impl.AntecedentServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class AntecedentControllerImpl implements AntecedentController {

    private AntecedentService service = new AntecedentServiceImpl();

    @Override
    public void openAntecedentManager(Long idPatient) {
        try {
            AntecedentView view = new AntecedentView(this, idPatient);
            view.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAntecedent(Antecedent antecedent) {
        try {
            service.ajouterAntecedent(antecedent);
            JOptionPane.showMessageDialog(null, "Antécédent ajouté avec succès !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void updateAntecedent(Antecedent antecedent) {
        try {
            service.modifierAntecedent(antecedent);
            JOptionPane.showMessageDialog(null, "Antécédent modifié.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void deleteAntecedent(Long idAntecedent) {
        try {
            service.supprimerAntecedent(idAntecedent);
            JOptionPane.showMessageDialog(null, "Antécédent supprimé.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public List<AntecedentDTO> getAntecedentsByPatient(Long idPatient) {
        try {
            List<Antecedent> list = service.getAntecedentsByPatient(idPatient);

            return list.stream().map(a -> AntecedentDTO.builder()
                    .id(a.getIdAntecedent())
                    .nom(a.getNom())
                    .categorie(a.getCategorie().name())
                    .niveauRisque(a.getNiveauRisque().name())
                    .idPatient(a.getIdPatient())
                    .build()
            ).collect(Collectors.toList());

        } catch (Exception e) {
            return List.of();
        }
    }
}