package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.actes.swing_implementation.ActeControllerImpl;
import ma.dentalTech.mvc.controllers.modules.medicament.swing_implementation.MedicamentControllerImpl;
import ma.dentalTech.mvc.controllers.modules.referentiel.swing_implementation.ReferentielControllerImpl;
import ma.dentalTech.mvc.ui.modules.actes.ActeView;
import ma.dentalTech.mvc.ui.modules.medicament.MedicamentView;
import ma.dentalTech.mvc.ui.modules.referentiel.CatalogueAntecedentView;
import ma.dentalTech.mvc.ui.modules.referentiel.ReferentielView;

import javax.swing.*;
//ROBAII IKRAM

public class TestReferentielGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // 1. Fenêtre Médicaments
                new MedicamentView(new MedicamentControllerImpl()).setVisible(true);

                // 2. Fenêtre Actes
                new ActeView(new ActeControllerImpl()).setVisible(true);

                // 3. Fenêtre Référentiel
                new ReferentielView(new ReferentielControllerImpl()).setVisible(true);

                // fenetre antecedent
                new CatalogueAntecedentView().setVisible(true);

            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}