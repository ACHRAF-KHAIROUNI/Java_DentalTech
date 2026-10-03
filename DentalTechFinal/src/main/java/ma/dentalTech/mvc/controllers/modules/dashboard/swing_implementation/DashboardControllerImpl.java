package ma.dentalTech.mvc.controllers.modules.dashboard.swing_implementation;

import javax.swing.JOptionPane;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.mvc.ui.modules.dashboard.DashboardView;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;
import ma.dentalTech.service.modules.dashboard.impl.DashboardServiceImpl;

public class DashboardControllerImpl implements DashboardController {

    private final DashboardService service = new DashboardServiceImpl();
    private DashboardView view;

    @Override
    public void openDashboard(Long userId) {
        try {
            // 1. Récupérer les données via le Service
            DashboardDTO data = service.getDashboardData();

            // 2. Créer la Vue et lui passer les données
            view = new DashboardView(this, data, userId);
            view.setVisible(true);

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Impossible d'ouvrir le Dashboard : " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refreshData() {
        try {
            // 1. Récupérer les nouvelles données
            DashboardDTO data = service.getDashboardData();

            // 2. Mettre à jour la Vue existante
            if (view != null) {
                view.updateData(data);
            }
        } catch (ServiceException e) {
            System.err.println("Erreur rafraîchissement Dashboard : " + e.getMessage());
        }
    }
}