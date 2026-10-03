package ma.dentalTech.mvc.controllers.modules.agenda.swing_implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.AgendaDTO;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaView;
import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.impl.AgendaServiceImpl;

@Data @AllArgsConstructor @NoArgsConstructor
public class AgendaControllerImpl implements AgendaController {

    private AgendaService service = new AgendaServiceImpl();

    @Override
    public void openManagementWindow(Long idMedecin) {
        try {
            AgendaView view = new AgendaView(this, idMedecin);
            view.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createAgenda(AgendaMensuel agenda) {
        try {
            service.creerAgendaMensuel(agenda);
            JOptionPane.showMessageDialog(null, "Agenda créé avec succès !");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAgenda(Long idAgenda) {
        try {
            service.supprimerAgenda(idAgenda);
            JOptionPane.showMessageDialog(null, "Agenda supprimé.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void toggleAgendaStatus(Long idAgenda, boolean currentStatus) {
        try {
            // Si c'est Ouvert (true), on veut Fermer (false) et inversement
            service.modifierEtatAgenda(idAgenda, !currentStatus);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur modif état : " + e.getMessage());
        }
    }

    @Override
    public void markDayUnavailable(Long idAgenda, LocalDate jour) {
        try {
            service.marquerJourIndisponible(idAgenda, jour);
            JOptionPane.showMessageDialog(null, "Le " + jour + " est marqué indisponible.");
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(null, "Erreur : " + e.getMessage());
        }
    }

    @Override
    public List<AgendaDTO> getAgendasByMedecin(Long idMedecin) {
        try {
            List<AgendaMensuel> entities = service.consulterAgendasMedecin(idMedecin);

            return entities.stream().map(a -> {
                // On récupère les jours indisponibles pour cet agenda
                String joursStr = "";
                try {
                    List<LocalDate> jours = service.getJoursIndisponibles(a.getIdAgenda());
                    if (jours.isEmpty()) {
                        joursStr = "Aucun";
                    } else {
                        // Formate la liste : "12/01, 14/01"
                        joursStr = jours.stream()
                                .map(d -> d.getDayOfMonth() + "/" + d.getMonthValue())
                                .collect(Collectors.joining(", "));
                    }
                } catch (Exception e) { joursStr = "Erreur"; }

                return AgendaDTO.builder()
                        .idAgenda(a.getIdAgenda())
                        .idMedecin(a.getIdMedecin())
                        .mois(a.getMois())
                        .statut(a.getEtat() ? "OUVERT" : "FERMÉ")
                        .joursIndisponibles(joursStr) // <--- ICI
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            return List.of();
        }
    }
    @Override
    public List<LocalDate> getUnavailableDatesRaw(Long idAgenda) {
        try {
            return service.getJoursIndisponibles(idAgenda);
        } catch (Exception e) {
            return List.of();
        }
    }
}