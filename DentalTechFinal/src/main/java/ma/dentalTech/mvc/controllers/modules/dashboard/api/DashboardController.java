package ma.dentalTech.mvc.controllers.modules.dashboard.api;

public interface DashboardController {
    /**
     * Ouvre la fenêtre principale du Dashboard
     */
    void openDashboard(Long userId);

    /**
     * Rafraîchit les données affichées (appelé par le bouton "Actualiser")
     */
    void refreshData();
}