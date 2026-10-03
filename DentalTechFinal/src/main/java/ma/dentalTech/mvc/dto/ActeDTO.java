package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ActeDTO {
    private Long id;
    private String libelle;
    private String categorie;
    private String prix;
    private Integer code;
}