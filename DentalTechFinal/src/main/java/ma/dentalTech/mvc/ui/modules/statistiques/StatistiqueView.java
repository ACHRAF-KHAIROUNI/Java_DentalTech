package ma.dentalTech.mvc.ui.modules.statistiques;

import ma.dentalTech.entities.caisse.Charge;
import ma.dentalTech.entities.caisse.Revenue;
import ma.dentalTech.mvc.dto.StatistiqueDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class StatistiqueView extends JFrame {

    private final Color BG_COLOR = new Color(240, 242, 245);
    private final Color GREEN_ACCENT = new Color(46, 204, 113);
    private final Color RED_ACCENT = new Color(231, 76, 60);
    private final Color BLUE_ACCENT = new Color(52, 152, 219);
    private final Color ORANGE_ACCENT = new Color(243, 156, 18); // Nouvelle couleur pour l'annuel
    private final Color TEXT_DARK = new Color(44, 62, 80);

    public StatistiqueView(StatistiqueDTO data) {
        setTitle("Finance & Analytics - DentalTech");
        setSize(1200, 900); // Un peu plus haut pour tout loger
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(20, 20));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 0, 30));
        JLabel title = new JLabel("Tableau de Bord Financier");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // CONTENU SCROLLABLE
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 30, 30, 30));

        // --- BARRE 1 : MENSUEL ---
        mainContent.add(createSectionTitle("📅 Ce Mois-ci"));

        JPanel kpiMois = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiMois.setOpaque(false);
        kpiMois.setPreferredSize(new Dimension(0, 130)); // Hauteur compacte
        kpiMois.setMaximumSize(new Dimension(3000, 130));

        kpiMois.add(new KpiCard("Bénéfice (Mois)", data.getBeneficeMensuel(), BLUE_ACCENT));
        kpiMois.add(new KpiCard("Revenus (Mois)", data.getRevenusMensuel(), GREEN_ACCENT));
        kpiMois.add(new KpiCard("Charges (Mois)", data.getChargesMensuel(), RED_ACCENT));

        mainContent.add(kpiMois);
        mainContent.add(Box.createVerticalStrut(25)); // Espace

        // --- BARRE 2 : ANNUEL ---
        mainContent.add(createSectionTitle("📆 Cette Année (" + java.time.LocalDate.now().getYear() + ")"));

        JPanel kpiAn = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiAn.setOpaque(false);
        kpiAn.setPreferredSize(new Dimension(0, 130));
        kpiAn.setMaximumSize(new Dimension(3000, 130));

        kpiAn.add(new KpiCard("Bénéfice (Annuel)", data.getBeneficeAnnuel(), ORANGE_ACCENT));
        kpiAn.add(new KpiCard("Revenus (Annuel)", data.getRevenusAnnuel(), GREEN_ACCENT));
        kpiAn.add(new KpiCard("Charges (Annuel)", data.getChargesAnnuel(), RED_ACCENT));

        mainContent.add(kpiAn);
        mainContent.add(Box.createVerticalStrut(30)); // Plus d'espace avant les graphiques

        // --- SECTION GRAPHIQUES ---
        mainContent.add(createSectionTitle("📊 Analyse Comparative"));

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setPreferredSize(new Dimension(0, 220));
        chartsPanel.setMaximumSize(new Dimension(3000, 220));

        chartsPanel.add(new BarChartCard("Comparatif Global",
                data.getTotalRevenus(), data.getTotalCharges()));

        chartsPanel.add(new BarChartCard("Performance Annuelle",
                data.getRevenusAnnuel(), data.getChargesAnnuel()));

        mainContent.add(chartsPanel);
        mainContent.add(Box.createVerticalStrut(30));

        // --- TABLEAUX ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Détail Revenus", createTableScroll(data.getListeRevenus(), true));
        tabs.addTab("Détail Charges", createTableScroll(data.getListeCharges(), false));

        mainContent.add(tabs);

        // Ajout du scroll global
        JScrollPane globalScroll = new JScrollPane(mainContent);
        globalScroll.setBorder(BorderFactory.createEmptyBorder());
        globalScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(globalScroll, BorderLayout.CENTER);
    }

    private JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(100, 100, 120));
        lbl.setBorder(new EmptyBorder(0, 5, 10, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JScrollPane createTableScroll(List<?> list, boolean isRevenue) {
        String[] cols = isRevenue ? new String[]{"Date", "Titre", "Montant"}
                : new String[]{"Date", "Titre", "Desc", "Montant"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        if (isRevenue) {
            for (Object o : list) {
                Revenue r = (Revenue) o;
                model.addRow(new Object[]{r.getDate().toLocalDate(), r.getTitre(), r.getMontant() + " MAD"});
            }
        } else {
            for (Object o : list) {
                Charge c = (Charge) o;
                model.addRow(new Object[]{c.getDate().toLocalDate(), c.getTitre(), c.getDescription(), c.getMontant() + " MAD"});
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 240, 254));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }
}