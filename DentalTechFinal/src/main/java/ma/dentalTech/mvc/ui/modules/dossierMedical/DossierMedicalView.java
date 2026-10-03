/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DossierMedicalView extends JFrame {

    private final DossierMedicalController controller;
    private final Long idPatient;
    private DossierMedical currentDossier;

    /* UI Components */
    private JLabel lblNomPatient;
    private JTabbedPane tabbedPane;
    
    /* General Info Tab */
    private JTextArea textAreaInfo;
    
    /* Antecedents Tab */
    private JTable tableAntecedents;
    private DefaultTableModel modelAntecedents;
    
    /* Consultations Tab */
    private JTable tableConsultations;
    private DefaultTableModel modelConsultations;

    /* Documents Tab */
    private DefaultListModel<File> listModelDocs;
    private JList<File> listDocs;

    public DossierMedicalView(DossierMedicalController controller, Long idPatient) {
        this.controller = controller;
        this.idPatient = idPatient;
        
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Dossier Médical Numérique");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        /* Header Configuration */
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.MAIN_BLUE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblNomPatient = new JLabel("Chargement...");
        lblNomPatient.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNomPatient.setForeground(Color.WHITE);
        header.add(lblNomPatient, BorderLayout.WEST);
        
        add(header, BorderLayout.NORTH);

        /* Tabs Integration */
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Informations Générales", createTabInfos());
        tabbedPane.addTab("Antécédents & Risques", createTabAntecedents());
        tabbedPane.addTab("Historique Consultations", createTabConsultations());
        tabbedPane.addTab("Ordonnances", createTabOrdonnances());
        tabbedPane.addTab("Certificats", createTabCertificats());
        tabbedPane.addTab("Documents & Radios", createTabDocuments());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTabInfos() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        textAreaInfo = new JTextArea();
        textAreaInfo.setFont(new Font("Consolas", Font.PLAIN, 14));
        textAreaInfo.setEditable(false);
        textAreaInfo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        p.add(new JScrollPane(textAreaInfo), BorderLayout.CENTER);
        return p;
    }

    private JPanel createTabAntecedents() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        modelAntecedents = new DefaultTableModel(new String[]{"Nom", "Catégorie", "Risque"}, 0);
        tableAntecedents = new JTable(modelAntecedents);
        tableAntecedents.setRowHeight(25);
        
        p.add(new JScrollPane(tableAntecedents), BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("Ajouter Antécédent");
        btnAdd.setBackground(Theme.SUCCESS_GREEN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> actionAddAntecedent());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);
        p.add(btnPanel, BorderLayout.SOUTH);
        
        return p;
    }

    private JPanel createTabConsultations() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        modelConsultations = new DefaultTableModel(new String[]{"ID", "Date", "Statut", "Observation"}, 0);
        tableConsultations = new JTable(modelConsultations);
        tableConsultations.setRowHeight(30);
        
        p.add(new JScrollPane(tableConsultations), BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("Nouvelle Consultation (Immédiate)");
        btnAdd.setBackground(Theme.MAIN_BLUE);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> actionNewConsultation());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);
        p.add(btnPanel, BorderLayout.SOUTH);
        
        return p;
    }

    private JPanel createTabDocuments() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        listModelDocs = new DefaultListModel<>();
        listDocs = new JList<>(listModelDocs);
        /* Custom Renderer for File Names */
        listDocs.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File) {
                    setText(((File) value).getName());
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });
        
        p.add(new JScrollPane(listDocs), BorderLayout.CENTER);
        
        /* Document Actions */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnUpload = new JButton("Charger Document (Radio/PDF...)");
        JButton btnOpen = new JButton("Ouvrir");
        JButton btnDelete = new JButton("Supprimer");
        
        btnUpload.setBackground(new Color(52, 152, 219)); btnUpload.setForeground(Color.WHITE);
        btnOpen.setBackground(new Color(46, 204, 113));
        btnDelete.setBackground(new Color(231, 76, 60));
        
        btnUpload.addActionListener(e -> actionUpload());
        btnOpen.addActionListener(e -> actionOpenDoc());
        btnDelete.addActionListener(e -> actionDeleteDoc());
        
        btnPanel.add(btnUpload);
        btnPanel.add(btnOpen);
        btnPanel.add(btnDelete);
        
        p.add(btnPanel, BorderLayout.SOUTH);
        
        return p;
    }

    private void loadData() {
        this.currentDossier = controller.getDossierComplet(idPatient);
        if(currentDossier.getPatient() == null) {
            JOptionPane.showMessageDialog(this, "Erreur chargement dossier.");
            dispose();
            return;
        }
        
        /* 1. General Info Loading */
        Patient p = currentDossier.getPatient();
        lblNomPatient.setText((p.getSexe().toString().startsWith("M") ? "M. " : "Mme ") + p.getNom().toUpperCase() + " " + p.getPrenom());
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- IDENTITÉ ---\n");
        sb.append("Nom : ").append(p.getNom()).append("\n");
        sb.append("Prénom : ").append(p.getPrenom()).append("\n");
        sb.append("Date Naissance : ").append(p.getDateDeNaissance()).append("\n");
        sb.append("Téléphone : ").append(p.getTelephone()).append("\n");
        sb.append("Assurance : ").append(p.getAssurance()).append("\n");
        sb.append("Adresse : ").append(p.getAdresse()).append("\n");
        textAreaInfo.setText(sb.toString());
        
        /* 2. Antecedents Loading */
        modelAntecedents.setRowCount(0);
        for(Antecedent a : currentDossier.getAntecedents()) {
            modelAntecedents.addRow(new Object[]{a.getNom(), a.getCategorie(), a.getNiveauRisque()});
        }
        
        /* 3. Consultations Loading */
        modelConsultations.setRowCount(0);
        for(Consultation c : currentDossier.getConsultations()) {
            modelConsultations.addRow(new Object[]{c.getIdConsultation(), c.getDate(), c.getStatut(), c.getObservationMedecin()});
        }
        
        /* 4. Documents Loading */
        refreshDocuments();
    }
    
    private void refreshDocuments() {
        listModelDocs.clear();
        List<File> files = controller.getDocuments(idPatient);
        for(File f : files) listModelDocs.addElement(f);
    }

    private void actionAddAntecedent() {
        JTextField txtNom = new JTextField();
        JComboBox<ma.dentalTech.entities.enums.CategorieAntecedent> cbCat = new JComboBox<>(ma.dentalTech.entities.enums.CategorieAntecedent.values());
        JComboBox<ma.dentalTech.entities.enums.NiveauRisque> cbRisque = new JComboBox<>(ma.dentalTech.entities.enums.NiveauRisque.values());
        
        Object[] msg = {
            "Nom de l'affection :", txtNom,
            "Catégorie :", cbCat,
            "Niveau de Risque :", cbRisque
        };
        
        int opt = JOptionPane.showConfirmDialog(this, msg, "Ajouter Antécédent", JOptionPane.OK_CANCEL_OPTION);
        if(opt == JOptionPane.OK_OPTION && !txtNom.getText().isEmpty()) {
            ma.dentalTech.entities.patient.Antecedent a = new ma.dentalTech.entities.patient.Antecedent();
            a.setNom(txtNom.getText());
            a.setCategorie((ma.dentalTech.entities.enums.CategorieAntecedent) cbCat.getSelectedItem());
            a.setNiveauRisque((ma.dentalTech.entities.enums.NiveauRisque) cbRisque.getSelectedItem());
            
            controller.addAntecedent(idPatient, a);
            loadData(); /* Refresh Data */
        }
    }
    
    private void actionNewConsultation() {
        JTextArea txtObs = new JTextArea(5, 20);
        txtObs.setText("Patient reçu ce jour. Examen clinique : RAS.");
        
        int opt = JOptionPane.showConfirmDialog(this, new JScrollPane(txtObs), "Notes Consultation", JOptionPane.OK_CANCEL_OPTION);
        if(opt == JOptionPane.OK_OPTION) {
            controller.createConsultation(idPatient, txtObs.getText());
            loadData(); // Rafraîchir
        }
    }

    private JPanel createTabOrdonnances() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Date", "Médicaments"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        /* Load Data from Controller */
        List<ma.dentalTech.entities.ordonnance.Ordonnance> ords = controller.getOrdonnances(idPatient);
        for(ma.dentalTech.entities.ordonnance.Ordonnance o : ords) {
            model.addRow(new Object[]{o.getIdOrd(), o.getDate(), "Voir détails..."});
        }
        
        JButton btnOpen = new JButton("Gérer Ordonnances");
        btnOpen.addActionListener(e -> {
            /* Open Ordonnance Module in Modal Dialog */
            JDialog d = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Gestion Ordonnances", true);
            d.setContentPane(new ma.dentalTech.mvc.ui.modules.ordonnance.OrdonnanceView(idPatient));
            d.setSize(900, 600);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
            
            /* Note: Data refresh handled by view reload if needed */

        });
        p.add(btnOpen, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createTabCertificats() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Date", "Notes", "Durée (jours)"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        /* Load Certificates */
        List<ma.dentalTech.entities.certificat.Certificat> certs = controller.getCertificats(idPatient);
        for(ma.dentalTech.entities.certificat.Certificat c : certs) {
            model.addRow(new Object[]{c.getIdCertif(), c.getDateDebut(), c.getNoteMedecin(), c.getDuree()});
        }

        JButton btnOpen = new JButton("Nouveau Certificat");
        btnOpen.addActionListener(e -> new ma.dentalTech.mvc.ui.modules.certificat.CertificatView(
                new ma.dentalTech.mvc.controllers.modules.certificat.swing_implementation.CertificatControllerImpl(), 
                idPatient /* Pass current Patient ID */
        ).setVisible(true));
        p.add(btnOpen, BorderLayout.SOUTH);
        return p;
    }

    private void actionUpload() {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            controller.uploadDocument(idPatient, f);
            refreshDocuments();
        }
    }
    
    private void actionOpenDoc() {
        File f = listDocs.getSelectedValue();
        if(f != null && f.exists()) {
            try {
                Desktop.getDesktop().open(f);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Impossible d'ouvrir le fichier : " + e.getMessage());
            }
        }
    }
    
    private void actionDeleteDoc() {
         File f = listDocs.getSelectedValue();
         if(f != null) {
             controller.deleteDocument(f);
             refreshDocuments();
         }
    }
}
