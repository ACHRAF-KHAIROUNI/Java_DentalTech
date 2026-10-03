/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.actes.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.mvc.dto.ActeDTO;
import java.util.List;

public interface ActeService {
    void ajouterActe(Acte acte) throws ServiceException;
    void modifierActe(Acte acte) throws ServiceException;
    void supprimerActe(Long id) throws ServiceException;

    List<Acte> getAllActes();
    List<ActeDTO> getAllAsDTO();
}