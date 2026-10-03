package ma.dentalTech.mvc.controllers.modules.backup;

import ma.dentalTech.service.modules.backup.BackupService;

import javax.swing.*;
import java.io.File;
//khairouni achraf
public class BackupController {

    private final BackupService service = new BackupService();

    public void performBackup(JFrame parentFrame) {
        // 1. Demander à l'utilisateur où sauvegarder
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choisir le dossier de sauvegarde");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();

            // 2. Lancer le backup dans un Thread séparé pour ne pas figer l'interface
            new Thread(() -> {
                try {
                    service.exportAllDataToCSV(directory.getAbsolutePath());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(parentFrame,
                                    "Sauvegarde terminée avec succès dans :\n" + directory.getAbsolutePath(),
                                    "Succès", JOptionPane.INFORMATION_MESSAGE)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(parentFrame,
                                    "Erreur lors de la sauvegarde : " + e.getMessage(),
                                    "Erreur", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }).start();
        }
    }
}