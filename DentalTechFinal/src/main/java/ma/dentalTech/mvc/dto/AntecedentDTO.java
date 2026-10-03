package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AntecedentDTO {
    private Long id;
    private String nom;
    private String categorie;    // Ex: "ALLERGIE"
    private String niveauRisque; // Ex: "CRITIQUE"
    private Long idPatient;
}