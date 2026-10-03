/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.controllers.modules.actes.api;

import ma.dentalTech.entities.actes.Acte;
import ma.dentalTech.mvc.dto.ActeDTO;
import java.util.List;

public interface ActeController {
    void create(Acte acte);
    void update(Acte acte);
    void delete(Long id);
    List<ActeDTO> listAll();

    List<ActeDTO> filter(String text, String categorie);
}