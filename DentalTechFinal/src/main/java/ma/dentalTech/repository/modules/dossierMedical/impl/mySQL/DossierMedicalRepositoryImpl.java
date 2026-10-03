/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.dossierMedical.impl.mySQL;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.patient.impl.mySQL.AntecedentRepositoryImpl;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;
import ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository;
import ma.dentalTech.repository.modules.rdvPatient.impl.mySQL.RDVRepositoryImpl;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {

    private final PatientRepository patientRepo = new PatientRepositoryImpl();
    private final AntecedentRepository antecedentRepo = new AntecedentRepositoryImpl();
    private final RDVRepository rdvRepo = new RDVRepositoryImpl();

    @Override
    public DossierMedical getDossierComplet(Long idPatient) {
        // 1. Récupérer le patient
        Patient p = patientRepo.findById(idPatient);
        if (p == null) return null;

        // 2. Construire l'objet composite
        return DossierMedical.builder()
                .patient(p)
                .antecedents(antecedentRepo.findByPatientId(idPatient))
                .rdvs(rdvRepo.findByPatientId(idPatient))
                // On peut ajouter ici consultationRepo.findByPatientId(idPatient) quand créé
                .build();
    }
}