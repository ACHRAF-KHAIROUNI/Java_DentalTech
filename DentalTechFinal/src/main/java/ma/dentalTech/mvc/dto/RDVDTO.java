package ma.dentalTech.mvc.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
//ROBAII IKRAM
@Data @Builder
public class RDVDTO {
    private Long idRDV;
    private LocalDate date;
    private LocalTime heure;
    private String nomPatient;
    private String nomMedecin;
    private String motif;
    private String statut;

    // Utile pour l'affichage dans le tableau Swing
    public Object[] toRow() {
        return new Object[]{idRDV, date, heure, nomPatient, nomMedecin, motif, statut};
    }
}