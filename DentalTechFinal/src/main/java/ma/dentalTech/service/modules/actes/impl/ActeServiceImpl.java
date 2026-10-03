/* Author : Rahali Zakaria */
package ma.dentalTech.service.modules.actes.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.mvc.dto.ActeDTO;
import ma.dentalTech.repository.modules.actes.api.ActeRepository;
import ma.dentalTech.repository.modules.actes.impl.mySQL.ActeRepositoryImpl;
import ma.dentalTech.service.modules.actes.api.ActeService;

import java.util.List;
import java.util.stream.Collectors;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository repo = new ActeRepositoryImpl();

    @Override
    public void ajouterActe(Acte a) throws ServiceException {
        /* Validation */
        if (a.getLibelle() == null || a.getLibelle().isEmpty())
            throw new ServiceException("Le libellé est obligatoire.");
        if (a.getPrixDeBase() == null || a.getPrixDeBase() < 0)
            throw new ServiceException("Prix invalide.");
        if (a.getCode() == null)
            throw new ServiceException("Code acte obligatoire.");

        /* Business Rule: Unique Code check (Java-side filtering) */
        /* Note: Ideally implement findByCode in repository */
        boolean codeExiste = repo.findAll().stream()
                .anyMatch(ex -> ex.getCode().equals(a.getCode()));

        if (codeExiste) {
            throw new ServiceException("Un acte avec le code " + a.getCode() + " existe déjà.");
        }

        try {
            repo.create(a);
        } catch (Exception e) {
            throw new ServiceException("Erreur technique : " + e.getMessage());
        }
    }

    @Override
    public void modifierActe(Acte a) throws ServiceException {
        if (a.getIdActe() == null) throw new ServiceException("ID manquant.");
        repo.update(a);
    }

    @Override
    public void supprimerActe(Long id) throws ServiceException {
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Impossible de supprimer (utilisé dans des consultations).");
        }
    }

    @Override
    public List<Acte> getAllActes() {
        return repo.findAll();
    }

    @Override
    public List<ActeDTO> getAllAsDTO() {
        return repo.findAll().stream().map(a ->
                ActeDTO.builder()
                        .id(a.getIdActe())
                        .libelle(a.getLibelle())
                        .categorie(a.getCategorie())
                        .code(a.getCode())
                        .prix(String.format("%.2f DH", a.getPrixDeBase()))
                        .build()
        ).collect(Collectors.toList());
    }
}