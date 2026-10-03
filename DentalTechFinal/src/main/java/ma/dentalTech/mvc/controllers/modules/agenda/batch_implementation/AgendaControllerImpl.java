package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.AgendaDTO;
import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.impl.AgendaServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class AgendaControllerImpl implements AgendaController {

    private AgendaService service = new AgendaServiceImpl();

    @Override
    public void openManagementWindow(Long idMedecin) {
        System.out.println(">>> BATCH: Gestion Agenda Médecin " + idMedecin);
    }

    @Override
    public void createAgenda(AgendaMensuel agenda) {
        try {
            service.creerAgendaMensuel(agenda);
            System.out.println("[SUCCESS] Agenda créé: " + agenda.getMois());
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public void deleteAgenda(Long idAgenda) {
        try {
            service.supprimerAgenda(idAgenda);
            System.out.println("[SUCCESS] Agenda supprimé ID: " + idAgenda);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public void toggleAgendaStatus(Long idAgenda, boolean currentStatus) {
        try {
            service.modifierEtatAgenda(idAgenda, !currentStatus);
            System.out.println("[SUCCESS] État modifié.");
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public void markDayUnavailable(Long idAgenda, LocalDate jour) {
        try {
            service.marquerJourIndisponible(idAgenda, jour);
            System.out.println("[SUCCESS] Jour marqué indisponible : " + jour);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    @Override
    public List<AgendaDTO> getAgendasByMedecin(Long idMedecin) {
        return List.of();
    }

    @Override
    public List<LocalDate> getUnavailableDatesRaw(Long idAgenda) {
        return List.of();
    }
}