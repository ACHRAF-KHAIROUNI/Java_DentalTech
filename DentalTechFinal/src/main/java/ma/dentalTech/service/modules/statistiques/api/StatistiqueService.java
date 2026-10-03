package ma.dentalTech.service.modules.statistiques.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.StatistiqueDTO;

public interface StatistiqueService {
    StatistiqueDTO getStatistiquesGlobales() throws ServiceException;
}