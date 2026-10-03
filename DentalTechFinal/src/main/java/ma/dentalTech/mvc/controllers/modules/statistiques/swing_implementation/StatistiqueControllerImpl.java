package ma.dentalTech.mvc.controllers.modules.statistiques.swing_implementation;

import javax.swing.JOptionPane;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiqueController;
import ma.dentalTech.mvc.dto.StatistiqueDTO;
import ma.dentalTech.mvc.ui.modules.statistiques.StatistiqueView;
import ma.dentalTech.service.modules.statistiques.api.StatistiqueService;
import ma.dentalTech.service.modules.statistiques.impl.StatistiqueServiceImpl;

public class StatistiqueControllerImpl implements StatistiqueController {

    private final StatistiqueService service = new StatistiqueServiceImpl();

    @Override
    public void openStatistics() {
        try {
            StatistiqueDTO data = service.getStatistiquesGlobales();
            StatistiqueView view = new StatistiqueView(data);
            view.setVisible(true);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage());
        }
    }
}