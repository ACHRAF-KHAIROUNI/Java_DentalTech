package ma.dentalTech.service.modules.medicament.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.mvc.dto.MedicamentDTO;
import ma.dentalTech.repository.modules.medicament.api.MedicamentRepository;
import ma.dentalTech.repository.modules.medicament.impl.mySQL.MedicamentRepositoryImpl;
import ma.dentalTech.service.modules.medicament.api.MedicamentService;

import java.util.List;
import java.util.stream.Collectors;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository repo = new MedicamentRepositoryImpl();

    @Override
    public void ajouterMedicament(Medicament m) throws ServiceException {
        // 1. Validation de base
        if (m.getNom() == null || m.getNom().trim().isEmpty()) {
            throw new ServiceException("Le nom du médicament est obligatoire.");
        }
        if (m.getPrixUnitaire() == null || m.getPrixUnitaire() < 0) {
            throw new ServiceException("Le prix doit être positif.");
        }

        // 2. Vérification Doublon (Règle métier)
        List<Medicament> existants = repo.findByNomLike(m.getNom());
        // On vérifie l'égalité exacte car findByNomLike est flou
        boolean existeDeja = existants.stream()
                .anyMatch(ex -> ex.getNom().equalsIgnoreCase(m.getNom()));

        if (existeDeja) {
            throw new ServiceException("Le médicament '" + m.getNom() + "' existe déjà.");
        }

        try {
            repo.create(m);
        } catch (Exception e) {
            throw new ServiceException("Erreur technique : " + e.getMessage());
        }
    }

    @Override
    public void modifierMedicament(Medicament m) throws ServiceException {
        if (m.getIdMct() == null) throw new ServiceException("ID manquant.");
        if (m.getPrixUnitaire() < 0) throw new ServiceException("Prix invalide.");

        try {
            repo.update(m);
        } catch (Exception e) {
            throw new ServiceException("Erreur modification.");
        }
    }

    @Override
    public void supprimerMedicament(Long id) throws ServiceException {
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Impossible de supprimer (peut-être utilisé dans une ordonnance).");
        }
    }

    @Override
    public List<Medicament> getAllMedicaments() {
        return repo.findAll();
    }

    @Override
    public List<Medicament> rechercherParNom(String keyword) {
        if (keyword == null || keyword.isBlank()) return repo.findAll();
        return repo.findByNomLike(keyword);
    }

    @Override
    public List<MedicamentDTO> getAllAsDTO() {
        return repo.findAll().stream().map(m ->
                MedicamentDTO.builder()
                        .id(m.getIdMct())
                        .nom(m.getNom())
                        .laboratoire(m.getLaboratoire())
                        .type(m.getType())
                        .prixAffiche(String.format("%.2f DH", m.getPrixUnitaire()))
                        .remboursableTexte((m.getRemboursable() != null && m.getRemboursable()) ? "Oui" : "Non")
                        .build()
        ).collect(Collectors.toList());
    }
}