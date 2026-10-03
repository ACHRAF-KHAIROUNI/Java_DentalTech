package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agenda.AgendaMensuel;

import java.time.LocalDate;
import java.util.List;

public interface AgendaService {

    // --- GESTION ADMINISTRATIVE


    void creerAgendaMensuel(AgendaMensuel agenda) throws ServiceException;


    void supprimerAgenda(Long idAgenda) throws ServiceException;


    List<AgendaMensuel> consulterAgendasMedecin(Long idMedecin) throws ServiceException;


    void modifierEtatAgenda(Long idAgenda, boolean estActif) throws ServiceException;


    void marquerJourIndisponible(Long idAgenda, LocalDate jour) throws ServiceException;
    // Ajoute ceci :
    List<LocalDate> getJoursIndisponibles(Long idAgenda) throws ServiceException;
}