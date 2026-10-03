package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.mvc.dto.AgendaDTO;
import java.time.LocalDate;
import java.util.List;

public interface AgendaController {

    // Navigation
    void openManagementWindow(Long idMedecin);

    // Actions qui appellent tes services
    void createAgenda(AgendaMensuel agenda);
    void deleteAgenda(Long idAgenda);
    void toggleAgendaStatus(Long idAgenda, boolean currentStatus);

    // Utilise ta fonction 'marquerJourIndisponible'
    void markDayUnavailable(Long idAgenda, LocalDate jour);

    // Récupération (utilise 'consulterAgendasMedecin')
    List<AgendaDTO> getAgendasByMedecin(Long idMedecin);
    // ... existant
    List<LocalDate> getUnavailableDatesRaw(Long idAgenda);
}