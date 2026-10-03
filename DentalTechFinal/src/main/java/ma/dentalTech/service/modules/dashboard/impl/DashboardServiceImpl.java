package ma.dentalTech.service.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.repository.modules.dashboard.api.DashboardRepository;
import ma.dentalTech.repository.modules.dashboard.impl.mySQL.DashboardRepositoryImpl;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

public class DashboardServiceImpl implements DashboardService {

    // Connexion au Repository
    private final DashboardRepository repo = new DashboardRepositoryImpl();

    @Override
    public DashboardDTO getDashboardData() throws ServiceException {
        try {
            // On construit le DTO en récupérant chaque info via le Repository
            return DashboardDTO.builder()
                    .nombreTotalPatients(repo.getNombrePatients())
                    .nombreRdvAujourdhui(repo.getRDVToday())
                    .chiffreAffairesGlobal(repo.getChiffreAffaireMois())
                    .fileAttente(repo.getListRDVToday()) // La liste des RDV
                    .build();

        } catch (Exception e) {
            // En cas d'erreur SQL, on la transforme en exception de service
            throw new ServiceException("Erreur lors du chargement du Dashboard : " + e.getMessage());
        }
    }
}