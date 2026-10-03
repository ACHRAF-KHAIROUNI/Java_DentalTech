package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.repository.modules.agenda.api.AgendaRepository;
import ma.dentalTech.repository.modules.agenda.impl.mySQL.AgendaRepositoryImpl;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.util.List;

public class AgendaServiceImpl implements AgendaService {

    private final AgendaRepository agendaRepo = new AgendaRepositoryImpl();

    @Override
    public void creerAgendaMensuel(AgendaMensuel agenda) throws ServiceException {
        // 1. Validation des champs obligatoires
        if (agenda.getMois() == null || agenda.getMois().trim().isEmpty()) {
            throw new ServiceException("Le nom du mois est obligatoire (ex: 'Octobre 2023').");
        }
        if (agenda.getIdMedecin() == null) {
            throw new ServiceException("L'agenda doit être rattaché à un médecin.");
        }

        // 2. Vérification de doublon (On ne crée pas deux fois 'Octobre 2023' pour le même médecin)
        List<AgendaMensuel> existants = agendaRepo.findByMedecin(agenda.getIdMedecin());
        for (AgendaMensuel existant : existants) {
            if (existant.getMois().equalsIgnoreCase(agenda.getMois())) {
                throw new ServiceException("Un agenda pour le mois '" + agenda.getMois() + "' existe déjà.");
            }
        }

        // 3. Initialisation par défaut
        if (agenda.getEtat() == null) {
            agenda.setEtat(true); // Ouvert par défaut
        }

        try {
            agendaRepo.create(agenda);
        } catch (Exception e) {
            throw new ServiceException("Erreur technique lors de la création de l'agenda : " + e.getMessage());
        }
    }

    @Override
    public void supprimerAgenda(Long idAgenda) throws ServiceException {
        if (idAgenda == null) throw new ServiceException("ID Agenda manquant.");

        AgendaMensuel a = agendaRepo.findById(idAgenda);
        if (a == null) throw new ServiceException("Agenda introuvable.");

        try {
            agendaRepo.deleteById(idAgenda);
        } catch (Exception e) {
            throw new ServiceException("Impossible de supprimer l'agenda. Vérifiez qu'il ne contient pas de données liées.");
        }
    }

    @Override
    public List<AgendaMensuel> consulterAgendasMedecin(Long idMedecin) throws ServiceException {
        if (idMedecin == null) throw new ServiceException("ID Médecin obligatoire.");
        return agendaRepo.findByMedecin(idMedecin);
    }

    @Override
    public void modifierEtatAgenda(Long idAgenda, boolean estActif) throws ServiceException {
        AgendaMensuel agenda = agendaRepo.findById(idAgenda);
        if (agenda == null) throw new ServiceException("Agenda introuvable.");

        // Modification de l'état
        agenda.setEtat(estActif);

        try {
            agendaRepo.update(agenda);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification de l'état de l'agenda.");
        }
    }

    @Override
    public void marquerJourIndisponible(Long idAgenda, LocalDate jour) throws ServiceException {
        // Assurez-vous que cette ligne est bien présente :
        if (jour.isBefore(LocalDate.now())) {
            throw new ServiceException("Impossible de marquer une indisponibilité dans le passé.");
        }
        if (idAgenda == null || jour == null) throw new ServiceException("ID Agenda et Date sont obligatoires.");

        AgendaMensuel agenda = agendaRepo.findById(idAgenda);
        if (agenda == null) throw new ServiceException("Agenda introuvable.");

        // Optionnel : Vérification de date passée
        // if (jour.isBefore(LocalDate.now())) throw new ServiceException("Impossible dans le passé.");

        try {
            // --- C'EST ICI LA CORRECTION : Appel réel au repository ---
            agendaRepo.addJourNonDisponible(idAgenda, jour);

        } catch (Exception e) {
            throw new ServiceException("Erreur technique lors de l'ajout du jour indisponible : " + e.getMessage());
        }
    }
    @Override
    public List<LocalDate> getJoursIndisponibles(Long idAgenda) throws ServiceException {
        // Appelle la méthode qu'on vient de créer dans le repo
        return agendaRepo.getJoursNonDisponibles(idAgenda);

    }

}