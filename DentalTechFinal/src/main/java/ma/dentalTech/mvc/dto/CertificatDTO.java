package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CertificatDTO {
    private Long idCertif;
    private String patientNom; // Pour savoir à qui c'est
    private String periode;    // ex: "Du 01/01 au 05/01"
    private int duree;
    private String note;
    private Long idPatient;    // Utile pour les actions
}