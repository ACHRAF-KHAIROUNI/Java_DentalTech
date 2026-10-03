package ma.dentalTech.repository.modules.agenda.api;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaRepository extends CrudRepository<AgendaMensuel, Long> {
    List<AgendaMensuel> findByMedecin(Long idMedecin);
    List<LocalDate> getJoursNonDisponibles(Long idAgenda);
    void addJourNonDisponible(Long idAgenda, LocalDate jour);
}