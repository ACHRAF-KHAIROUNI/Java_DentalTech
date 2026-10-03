/* Author : Rahali Zakaria */
package ma.dentalTech.entities.caisse;

import java.time.LocalDateTime;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Facture {
    private Long idFacture;
    private Double totaleFacture;
    private Double totalePaye; // totalePayé
    private Double reste;
    private String statut;
    private LocalDateTime dateFacture;

    private Long idConsultation;
}