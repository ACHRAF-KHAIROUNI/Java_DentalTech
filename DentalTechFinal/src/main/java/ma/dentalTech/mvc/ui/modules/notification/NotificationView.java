package ma.dentalTech.mvc.ui.modules.notification;

import ma.dentalTech.entities.userManager.Notification;

import ma.dentalTech.service.common.NotificationService;
import ma.dentalTech.service.common.NotificationServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationView extends JDialog {

    private final NotificationService service = new NotificationServiceImpl();
    private JTable table;
    private DefaultTableModel model;
    private final Long currentUserId;

    public NotificationView(Frame parent, Long userId) {
        super(parent, "Centre de Notifications", true); // Modal
        this.currentUserId = userId;
        setSize(800, 500);
        setLocationRelativeTo(parent);

        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setBackground(new Color(60, 63, 65));
        JLabel lblTitle = new JLabel("🔔 Vos Notifications");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelHeader.add(lblTitle);
        add(panelHeader, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Date", "Heure", "Titre", "Message", "Lu" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // Cache ID

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnMarkRead = new JButton("Marquer comme lu");
        JButton btnRefresh = new JButton("Actualiser");
        JButton btnClose = new JButton("Fermer");

        btnMarkRead.addActionListener(e -> markAsRead());
        btnRefresh.addActionListener(e -> loadNotifications());
        btnClose.addActionListener(e -> dispose());

        panelBtn.add(btnRefresh);
        panelBtn.add(btnMarkRead);
        panelBtn.add(btnClose);
        add(panelBtn, BorderLayout.SOUTH);
    }

    private void loadNotifications() {
        model.setRowCount(0);
        List<Notification> list = service.getNotifications(currentUserId);

        for (Notification n : list) {
            model.addRow(new Object[] {
                    n.getIdNotification(),
                    n.getDate(),
                    n.getTime(),
                    n.getTitre(),
                    n.getMessage(),
                    false
            });
        }
    }

    private void markAsRead() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une notification.");
            return;
        }
        Long id = (Long) model.getValueAt(row, 0);
        service.markAsRead(id);
        loadNotifications();
    }
}
