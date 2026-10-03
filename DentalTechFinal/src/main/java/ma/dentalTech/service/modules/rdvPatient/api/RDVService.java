package ma.dentalTech.service.modules.rdvPatient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.mvc.dto.RDVDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface RDVService {
    void planifierRDV(RDV rdv) throws ServiceException, ValidationException;
    void annulerRDV(Long idRDV) throws ServiceException;
    List<RDVDTO> getAgendaDuJour(LocalDate date);
    List<RDVDTO> getHistoriquePatient(Long idPatient);

    void modifierRDV(RDV rdvModifie) throws ServiceException, ValidationException;
    void confirmerRDV(Long idRDV) throws ServiceException;
    RDV getRDVById(Long id);

    Arrays getAllRDVs();
    List<RDVDTO> getAgendaMensuel(int annee, int mois);
}