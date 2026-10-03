package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// --- CORRECTION IMPORT ---
import ma.dentalTech.entities.rdvPatient.RDV;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardDTO {
    private long nombreTotalPatients;
    private long nombreRdvAujourdhui;
    private double chiffreAffairesGlobal;
    private List<RDV> fileAttente;
}