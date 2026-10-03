package ma.dentalTech.mvc.ui.modules.referentiel;

import ma.dentalTech.mvc.controllers.modules.referentiel.api.ReferentielController;
import ma.dentalTech.mvc.palette.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
//ROBAII IKRAM
public class ReferentielView extends JFrame {

    private final ReferentielController controller;

    public ReferentielView(ReferentielController controller) {
        this.controller = controller;
        setTitle("📚 Données Référentielles");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_LABEL);

        // Récupération des données
        List<String> assurances = Arrays.stream(controller.getAssurancesForCombo())
                .map(Object::toString).collect(Collectors.toList());

        List<String> categories = Arrays.stream(controller.getCategoriesForCombo())
                .map(Object::toString).collect(Collectors.toList());

        List<String> maladies = controller.getMaladiesSuggestions();

        // Création des onglets avec recherche
        tabs.addTab("Assurances", createSearchablePanel(assurances));
        tabs.addTab("Catégories Risques", createSearchablePanel(categories));
        tabs.addTab("Catalogue Maladies", createSearchablePanel(maladies));

        add(tabs);
    }

    /**
     * Crée un panneau avec une barre de recherche et une liste filtrable
     */
    private JPanel createSearchablePanel(List<String> dataList) {
        JPanel panel = new JPanel(new BorderLayout());

        // Barre de recherche
        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("🔍 Rechercher..."));
        txtSearch.setFont(Theme.FONT_FIELD);

        // Modèle et Liste
        DefaultListModel<String> model = new DefaultListModel<>();
        dataList.forEach(model::addElement);

        JList<String> list = new JList<>(model);
        list.setFont(Theme.FONT_FIELD);
        list.setFixedCellHeight(30);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Logique de filtrage instantané
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String val = txtSearch.getText().toLowerCase();
                model.clear();
                for (String s : dataList) {
                    if (s.toLowerCase().contains(val)) {
                        model.addElement(s);
                    }
                }
            }
        });

        panel.add(txtSearch, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }
}