/* Author : Rahali Zakaria */
package ma.dentalTech.entities.caisse;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SituationFinanciere {
    private Long idSF;
    private Double totaleDesActes;
    private Double totalePaye; // totalePayé
    private Double credit; // crédit
    private String statut;
    private String enPromo;

    private Long idFacture;
}