package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class AgendaDTO {
    private Long idAgenda;
    private String mois;
    private String statut; // "OUVERT" ou "FERMÉ"
    private Long idMedecin;
    private String joursIndisponibles;
}