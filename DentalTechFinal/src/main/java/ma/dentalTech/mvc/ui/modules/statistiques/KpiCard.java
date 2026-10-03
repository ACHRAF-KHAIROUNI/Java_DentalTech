package ma.dentalTech.mvc.ui.modules.statistiques;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class KpiCard extends JPanel {

    private final Color bgColor = Color.WHITE;

    public KpiCard(String title, double amount, Color accent) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(String.format("%,.2f DH", amount));
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVal.setForeground(new Color(44, 62, 80));

        // Petite ligne de couleur
        JPanel line = new JPanel();
        line.setBackground(accent);
        line.setPreferredSize(new Dimension(50, 4));

        add(lblTitle, BorderLayout.NORTH);
        add(lblVal, BorderLayout.CENTER);
        add(line, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
        super.paintComponent(g);
    }
}