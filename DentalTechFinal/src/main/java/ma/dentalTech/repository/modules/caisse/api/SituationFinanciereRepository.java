/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.caisse.SituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.Optional;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {
    Optional<SituationFinanciere> findByFactureId(Long idFacture);
    void deleteByFactureId(Long idFacture);
}
