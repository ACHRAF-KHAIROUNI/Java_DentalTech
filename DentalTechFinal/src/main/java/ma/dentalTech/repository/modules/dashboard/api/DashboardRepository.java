package ma.dentalTech.repository.modules.dashboard.api;

// --- CORRECTION IMPORT ---
import ma.dentalTech.entities.rdvPatient.RDV;
import java.util.List;

public interface DashboardRepository {
    long getNombrePatients();
    long getRDVToday();
    double getChiffreAffaireMois();
    long getAlertesStock();

    List<RDV> getListRDVToday();
}