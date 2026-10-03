package ma.dentalTech.service.modules.ordonnance.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;

import java.util.List;

public interface OrdonnanceService {

    // Créer une ordonnance avec ses médicaments en une seule fois
    void creerOrdonnanceComplete(Ordonnance ordonnance, List<Prescription> prescriptions) throws ServiceException, ValidationException;

    // Récupérer l'historique
    List<Ordonnance> getHistoriquePatient(Long idPatient);

    // Récupérer le détail d'une ordonnance
    List<Prescription> getDetailsOrdonnance(Long idOrdonnance);

    // Supprimer (si erreur de saisie)
    void supprimerOrdonnance(Long idOrdonnance) throws ServiceException;

    Ordonnance getOrdonnanceById(Long id);
}