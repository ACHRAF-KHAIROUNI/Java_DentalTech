package ma.dentalTech.mvc.ui.modules.referentiel;

import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
//ROBAII IKRAM
public class CatalogueAntecedentView extends JFrame {

    // Simulation de données (car pas de table SQL dédiée)
    private final List<String> maladies = new ArrayList<>(List.of(
            "Diabète Type 1", "Diabète Type 2", "Hypertension", "Allergie Pénicilline", "Asthme"
    ));

    private JTextField txtLibelle;
    private JTable table;
    private DefaultTableModel model;

    public CatalogueAntecedentView() {
        setTitle("Gestion du Catalogue Antécédents");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(Theme.MAIN_BLUE);
        JLabel title = new JLabel("Catalogue des Maladies / Risques");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Gauche : Formulaire
        JPanel left = new JPanel(new GridLayout(6, 1, 10, 10));
        left.setBorder(new EmptyBorder(20, 20, 20, 20));
        left.setPreferredSize(new Dimension(250, 0));

        txtLibelle = new JTextField();
        left.add(new JLabel("Libellé Maladie :"));
        left.add(txtLibelle);
        left.add(new JLabel("")); // Espace

        // Boutons Arrondis et Petits
        JButton btnAdd = createRoundedButton("Ajouter", new Color(46, 204, 113));
        JButton btnDel = createRoundedButton("Supprimer", new Color(231, 76, 60));

        btnAdd.addActionListener(e -> addMaladie());
        btnDel.addActionListener(e -> deleteMaladie());

        left.add(btnAdd);
        left.add(btnDel);

        add(left, BorderLayout.WEST);

        // Centre : Table
        String[] cols = {"Libellé Maladie"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(Theme.FONT_FIELD);
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if(r != -1) txtLibelle.setText(model.getValueAt(r, 0).toString());
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for(String m : maladies) model.addRow(new Object[]{m});
    }

    private void addMaladie() {
        if(!txtLibelle.getText().isEmpty() && !maladies.contains(txtLibelle.getText())) {
            maladies.add(txtLibelle.getText());
            refresh();
            txtLibelle.setText("");
        }
    }

    private void deleteMaladie() {
        if(!txtLibelle.getText().isEmpty()) {
            maladies.remove(txtLibelle.getText());
            refresh();
            txtLibelle.setText("");
        }
    }

    // --- BOUTON ARRONDIT ---
    private JButton createRoundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                // Rayon de 20px pour l'arrondi
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Police plus petite
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // Important pour la transparence des coins
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 30)); // Taille petite fixe
        return btn;
    }
}