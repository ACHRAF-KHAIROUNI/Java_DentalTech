/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.impl.mySQL.ConsultationRepositoryImpl;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;

import java.time.LocalDate;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository repo;

    public ConsultationServiceImpl() {
        this.repo = new ConsultationRepositoryImpl();
    }

    @Override
    public List<Consultation> getAllConsultations() {
        return repo.findAll();
    }

    @Override
    public List<Consultation> getConsultationsByPatient(Long idPatient) {
        return repo.findByPatientId(idPatient);
    }

    @Override
    public void createConsultation(Consultation c) throws ServiceException {
        if(c.getDate() == null) c.setDate(LocalDate.now());
        if(c.getIdRDV() == null) throw new ServiceException("Une consultation doit être liée à un RDV.");
        repo.create(c);
    }

    @Override
    public void updateConsultation(Consultation c) throws ServiceException {
        repo.update(c);
    }

    @Override
    public void deleteConsultation(Long id) throws ServiceException {
        repo.deleteById(id);
    }
}
