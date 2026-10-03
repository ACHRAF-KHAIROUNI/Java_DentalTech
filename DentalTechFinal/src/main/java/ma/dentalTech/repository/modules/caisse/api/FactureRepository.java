/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.caisse.Facture;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {
    List<Facture> findByStatut(String statut);
}
