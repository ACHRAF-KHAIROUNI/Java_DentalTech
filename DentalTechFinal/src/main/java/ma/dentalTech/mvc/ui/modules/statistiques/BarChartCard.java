package ma.dentalTech.mvc.ui.modules.statistiques;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BarChartCard extends JPanel {
    private final double val1, val2;
    private final String title;

    private final Color GREEN_ACCENT = new Color(46, 204, 113);
    private final Color RED_ACCENT = new Color(231, 76, 60);
    private final Color TEXT_DARK = new Color(44, 62, 80);

    public BarChartCard(String title, double v1, double v2) {
        this.title = title;
        this.val1 = v1;
        this.val2 = v2;

        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- CORRECTION MAJEURE ICI ---
        // On force une taille minimale pour que le layout ne l'écrase pas
        this.setPreferredSize(new Dimension(300, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Initialisation graphique haute qualité
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fond Carte (Blanc avec coins arrondis)
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));

        // Titre
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString(title, 20, 30);

        // --- CALCULS DES BARRES ---
        // Marges de sécurité pour ne pas dessiner sur le texte
        int paddingBottom = 40;
        int paddingTop = 50;
        int drawHeight = h - paddingBottom - paddingTop; // Espace dispo pour les barres

        if (drawHeight < 10) return; // Sécurité si la fenêtre est trop petite

        double max = Math.max(val1, val2);
        if (max == 0) max = 1;

        // Hauteur des barres
        int h1 = (int) ((val1 / max) * drawHeight);
        int h2 = (int) ((val2 / max) * drawHeight);

        // Largeur et positions
        int barWidth = 50;
        int x1 = (w / 4) - (barWidth / 2); // Centré gauche
        int x2 = (3 * w / 4) - (barWidth / 2); // Centré droite
        int bottomY = h - paddingBottom;

        // --- DESSIN ---

        // Barre 1 (Verte - Revenus)
        g2.setColor(GREEN_ACCENT);
        g2.fillRoundRect(x1, bottomY - h1, barWidth, h1, 10, 10);

        // Valeur 1 (Au dessus)
        g2.setColor(TEXT_DARK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String s1 = String.format("%.0f", val1);
        g2.drawString(s1, x1 + (barWidth - g2.getFontMetrics().stringWidth(s1))/2, bottomY - h1 - 5);

        // Légende 1 (En dessous)
        g2.setColor(Color.GRAY);
        g2.drawString("REV", x1 + (barWidth - g2.getFontMetrics().stringWidth("REV"))/2, bottomY + 15);


        // Barre 2 (Rouge - Charges)
        g2.setColor(RED_ACCENT);
        g2.fillRoundRect(x2, bottomY - h2, barWidth, h2, 10, 10);

        // Valeur 2 (Au dessus)
        g2.setColor(TEXT_DARK);
        String s2 = String.format("%.0f", val2);
        g2.drawString(s2, x2 + (barWidth - g2.getFontMetrics().stringWidth(s2))/2, bottomY - h2 - 5);

        // Légende 2 (En dessous)
        g2.setColor(Color.GRAY);
        g2.drawString("CHRG", x2 + (barWidth - g2.getFontMetrics().stringWidth("CHRG"))/2, bottomY + 15);
    }
}