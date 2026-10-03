/* Author : Rahali Zakaria */
package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.entities.caisse.Facture;
import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController;
import ma.dentalTech.mvc.dto.FactureDTO;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CaisseView extends JFrame {

    private final CaisseController controller;
    private JTabbedPane tabbedPane;

    /* Tab: Invoices Management */
    private JTable tableFactures;
    private DefaultTableModel modelFactures;

    /* Tab: Journal (Expenses/Revenues) */
    private JTable tableCharges, tableRevenus;
    private DefaultTableModel modelCharges, modelRevenus;

    /* Tab: Financial Dashboard */
    private JLabel lblCaGlobal, lblRecettesMois, lblDepensesMois;

    public CaisseView(CaisseController controller) {
        this.controller = controller;
        setTitle("💰 Gestion de la Caisse & Facturation");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Section
        JPanel header = new JPanel();
        header.setBackground(Theme.MAIN_BLUE);
        JLabel title = new JLabel("Module Financier");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Tabs Integration
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("Factures & Paiements", createTabFactures());
        tabbedPane.addTab("Journal (Charges/Revenus)", createTabJournal());


        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel createTabFactures() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Toolbar with Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPayer = createButton("Enregistrer Paiement", new Color(46, 204, 113));
        JButton btnDelete = createButton("Supprimer Facture", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Actualiser", new Color(52, 152, 219));

        btnPayer.addActionListener(e -> actionPayer());
        btnDelete.addActionListener(e -> actionDeleteFacture());
        btnRefresh.addActionListener(e -> refreshAll());

        toolbar.add(btnPayer);
        
        // Complementary Action Buttons
        JButton btnRemise = createButton("Appliquer Remise", new Color(243, 156, 18));
        JButton btnPrint  = createButton("Imprimer", new Color(52, 152, 219));
        
        btnRemise.addActionListener(e -> actionRemise());
        btnPrint.addActionListener(e -> actionPrintFacture());
        
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnRemise);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnPrint);
        toolbar.add(Box.createHorizontalStrut(10));

        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        
        panel.add(toolbar, BorderLayout.NORTH);

        // Invoices Table Configuration
        String[] cols = {"ID", "Date", "Consultation", "Total", "Déjà Payé", "Reste", "Statut"};
        modelFactures = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableFactures = new JTable(modelFactures);
        styleTable(tableFactures);
        
        panel.add(new JScrollPane(tableFactures), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabJournal() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left Section: Expenses (Charges)
        JPanel pCharges = new JPanel(new BorderLayout(5, 5));
        pCharges.setBorder(BorderFactory.createTitledBorder("📉 Dépenses / Charges"));
        
        JButton btnAddCharge = createButton("+ Charge", new Color(231, 76, 60));
        btnAddCharge.addActionListener(e -> actionAddCharge());
        JPanel pBtnC = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pBtnC.add(btnAddCharge);
        pCharges.add(pBtnC, BorderLayout.NORTH);

        modelCharges = new DefaultTableModel(new String[]{"Date", "Titre", "Montant"}, 0);
        tableCharges = new JTable(modelCharges);
        styleTable(tableCharges);
        pCharges.add(new JScrollPane(tableCharges), BorderLayout.CENTER);


        // Right Section: Revenues
        JPanel pRevenus = new JPanel(new BorderLayout(5, 5));
        pRevenus.setBorder(BorderFactory.createTitledBorder("📈 Autres Revenus"));

        JButton btnAddRev = createButton("+ Revenu", new Color(46, 204, 113));
        JButton btnExport = createButton("Exporter CSV", new Color(52, 152, 219));
        
        btnAddRev.addActionListener(e -> actionAddRevenue());
        btnExport.addActionListener(e -> actionExportJournal());
        
        JPanel pBtnR = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        pBtnR.add(btnExport);
        pBtnR.add(Box.createHorizontalStrut(10));
        pBtnR.add(btnAddRev);
        pRevenus.add(pBtnR, BorderLayout.NORTH);

        modelRevenus = new DefaultTableModel(new String[]{"Date", "Titre", "Montant"}, 0);
        tableRevenus = new JTable(modelRevenus);
        styleTable(tableRevenus);
        pRevenus.add(new JScrollPane(tableRevenus), BorderLayout.CENTER);

        panel.add(pCharges);
        panel.add(pRevenus);
        return panel;
    }

    /* Actions Implementation */

    private void actionPayer() {
        int row = tableFactures.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture.");
            return;
        }

        Long id = (Long) modelFactures.getValueAt(row, 0);
        String resteStr = (String) modelFactures.getValueAt(row, 5); // "100.00 DH"
        
        String input = JOptionPane.showInputDialog(this, "Montant du paiement (Reste: " + resteStr + ") :");
        if(input != null && !input.isEmpty()) {
            try {
                Double montant = Double.parseDouble(input.replace(",", "."));
                controller.payerFacture(id, montant);
                refreshAll();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Montant invalide.");
            }
        }
    }

    private void actionRemise() {
        int row = tableFactures.getSelectedRow();
        if (row != -1) {
            Long id = (Long) modelFactures.getValueAt(row, 0);
            
            String[] options = {"Montant Fixe (DH)", "Pourcentage (%)"};
            int choice = JOptionPane.showOptionDialog(this, 
                    "Voulez-vous appliquer une remise en montant ou en pourcentage ?", 
                    "Type de Remise",
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, options, options[0]);

            if (choice == -1) return; // Annulé

            String label = (choice == 0) ? "Montant de la remise (DH) :" : "Pourcentage de remise (%) :";
            String input = JOptionPane.showInputDialog(this, label);
            
            if (input != null && !input.isEmpty()) {
                try {
                    Double valeur = Double.parseDouble(input.replace(",", "."));
                    boolean isPercent = (choice == 1);
                    
                    controller.appliquerRemise(id, valeur, isPercent);
                    refreshAll();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Valeur invalide.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture.");
        }
    }

    private void actionPrintFacture() {
        int row = tableFactures.getSelectedRow();
        if (row != -1) {
            Long id = (Long) modelFactures.getValueAt(row, 0);
            String total = String.valueOf(modelFactures.getValueAt(row, 3));
            String paye = String.valueOf(modelFactures.getValueAt(row, 4));
            String reste = String.valueOf(modelFactures.getValueAt(row, 5));
            
            JTextPane receipt = new JTextPane();
            receipt.setText("\n" +
                    "      DENTAL TECH CABINET      \n" +
                    "--------------------------------\n" +
                    " FACTURE N° : " + id + "\n" +
                    "--------------------------------\n" +
                    " TOTAL      : " + total + "\n" +
                    " RECU       : " + paye + "\n" +
                    " RESTE      : " + reste + "\n" +
                    "--------------------------------\n" +
                    "   Merci de votre visite !   \n");
            
            try { receipt.print(); } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Erreur d'impression : " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture.");
        }
    }
    
    private void actionExportJournal() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Type;Date;Titre;Montant\n");
            
            for (int i = 0; i < modelCharges.getRowCount(); i++) {
                sb.append("CHARGE;" + modelCharges.getValueAt(i, 0) + ";" + modelCharges.getValueAt(i, 1) + ";" + modelCharges.getValueAt(i, 2) + "\n");
            }
            for (int i = 0; i < modelRevenus.getRowCount(); i++) {
                sb.append("REVENU;" + modelRevenus.getValueAt(i, 0) + ";" + modelRevenus.getValueAt(i, 1) + ";" + modelRevenus.getValueAt(i, 2) + "\n");
            }
            
            java.nio.file.Files.write(java.nio.file.Paths.get("Journal_Export.csv"), sb.toString().getBytes());
            JOptionPane.showMessageDialog(this, "Export réussi : Journal_Export.csv");
            
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur export : " + e.getMessage()); }
    }

    private void actionDeleteFacture() {
        int row = tableFactures.getSelectedRow();
        if (row != -1) {
            Long id = (Long) modelFactures.getValueAt(row, 0);
            controller.deleteFacture(id);
            refreshAll();
        }
    }

    private void actionAddCharge() {
        JTextField txtTitre = new JTextField();
        JTextField txtMontant = new JTextField();
        Object[] message = {"Titre:", txtTitre, "Montant (DH):", txtMontant};

        int option = JOptionPane.showConfirmDialog(this, message, "Nouvelle Charge", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Charge c = Charge.builder()
                        .titre(txtTitre.getText())
                        .description("Saisie manuelle")
                        .montant(Double.parseDouble(txtMontant.getText().replace(",", ".")))
                        .idCabinet(1L) // Default Cabinet ID
                        .build();
                controller.addCharge(c);
                refreshAll();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur saisie."); }
        }
    }

    private void actionAddRevenue() {
        JTextField txtTitre = new JTextField();
        JTextField txtMontant = new JTextField();
        Object[] message = {"Titre:", txtTitre, "Montant (DH):", txtMontant};

        int option = JOptionPane.showConfirmDialog(this, message, "Nouveau Revenu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Revenue r = Revenue.builder()
                        .titre(txtTitre.getText())
                        .description("Saisie manuelle")
                        .montant(Double.parseDouble(txtMontant.getText().replace(",", ".")))
                        .idCabinet(1L)
                        .build();
                controller.addRevenue(r);
                refreshAll();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur saisie."); }
        }
    }

    private void refreshAll() {
        // Refresh Invoices
        modelFactures.setRowCount(0);
        List<FactureDTO> listF = controller.getAllFactures();
        for(FactureDTO f : listF) {
            modelFactures.addRow(new Object[]{f.getId(), f.getDate(), f.getIdConsultation(), f.getMontantTotal(), f.getMontantPaye(), f.getReste(), f.getStatut()});
        }

        // Refresh Expenses
        modelCharges.setRowCount(0);
        List<Charge> charges = controller.getAllCharges();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for(Charge c : charges) {
            modelCharges.addRow(new Object[]{c.getDate().format(dtf), c.getTitre(), String.format("%.2f DH", c.getMontant())});
        }

        // Refresh Revenues
        modelRevenus.setRowCount(0);
        List<Revenue> revenues = controller.getAllRevenues();
        for(Revenue r : revenues) {
            modelRevenus.addRow(new Object[]{r.getDate().format(dtf), r.getTitre(), String.format("%.2f DH", r.getMontant())});
        }
    }

    /* Utility Methods */
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
    }
}
