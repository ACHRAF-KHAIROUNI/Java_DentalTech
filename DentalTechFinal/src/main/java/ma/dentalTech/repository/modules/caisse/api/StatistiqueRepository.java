/* Author : Rahali Zakaria */
package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.entities.caisse.Statistique;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface StatistiqueRepository extends CrudRepository<Statistique, Long> {
    List<Statistique> findByDate(LocalDate date);
}
