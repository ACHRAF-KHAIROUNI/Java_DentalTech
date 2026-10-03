/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.actes.api;

import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface ActeRepository extends CrudRepository<Acte, Long> {

    // Recherche par catégorie (ex: Chirurgie, Soins, Prothèse...)
    List<Acte> findByCategorie(String categorie);

    // Recherche par nom pour l'autocomplétion
    List<Acte> findByLibelleLike(String keyword);
}