package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.DashboardDTO;

public interface DashboardService {

    /**
     * Récupère toutes les données nécessaires au tableau de bord
     * (Stats + Liste d'attente) en un seul appel.
     */
    DashboardDTO getDashboardData() throws ServiceException;
}