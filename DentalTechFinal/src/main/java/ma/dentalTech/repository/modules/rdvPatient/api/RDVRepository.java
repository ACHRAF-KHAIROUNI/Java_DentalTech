package ma.dentalTech.repository.modules.rdvPatient.api;

import ma.dentalTech.entities.rdvPatient.RDV;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RDVRepository extends CrudRepository<RDV, Long> {
    List<RDV> findByPatientId(Long idPatient);
    List<RDV> findByDate(LocalDate date);
    List<RDV> findByDateBetween(LocalDate start, LocalDate end);
}