package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//khairouni achraf
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FactureDTO {
    private Long id;
    private String date;
    private String montantTotal;
    private String montantPaye;
    private String reste;
    private String statut;
    private Long idConsultation;
}
