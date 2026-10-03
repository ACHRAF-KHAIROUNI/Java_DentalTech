/* Author : Rahali Zakaria */
package ma.dentalTech.entities.actes;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Acte {
    private Long idActe;
    private String libelle; // libellé
    private String categorie; // catégorie
    private Double prixDeBase;
    private String description;
    private Integer code;

    @Override
    public String toString() {
        return libelle + " (" + code + ")";
    }
}