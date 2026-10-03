/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.dossierMedical.Consultation;
import java.util.List;

public interface ConsultationService {
    List<Consultation> getAllConsultations();
    List<Consultation> getConsultationsByPatient(Long idPatient);
    void createConsultation(Consultation c) throws ServiceException;
    void updateConsultation(Consultation c) throws ServiceException;
    void deleteConsultation(Long id) throws ServiceException;
}
