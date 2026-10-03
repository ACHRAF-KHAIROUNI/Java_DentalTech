package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//ROBAII IKRAM
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class MedicamentDTO {
    private Long id;
    private String nom;
    private String laboratoire;
    private String type;
    private String prixAffiche; // ex: "45.00 DH"
    private String remboursableTexte; // "Oui" ou "Non"
}