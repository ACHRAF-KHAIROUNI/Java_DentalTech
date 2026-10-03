package ma.dentalTech.mvc.ui.modules.ordonnance;

import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.ordonnance.Prescription;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.medicament.impl.mySQL.MedicamentRepositoryImpl;
import ma.dentalTech.repository.modules.patient.impl.mySQL.PatientRepositoryImpl;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDate;
import java.util.List;
//ROBAII IKRAM
public class OrdonnancePrinter implements Printable {

    private final Ordonnance ordonnance;
    private final Patient patient;
    private final List<Prescription> prescriptions;

    // Repos pour récupérer les infos complètes (Noms médicaments...)
    private final MedicamentRepositoryImpl medRepo = new MedicamentRepositoryImpl();

    public OrdonnancePrinter(Ordonnance ordonnance, Patient patient, List<Prescription> prescriptions) {
        this.ordonnance = ordonnance;
        this.patient = patient;
        this.prescriptions = prescriptions;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // --- DESIGN DE L'ORDONNANCE ---
        int y = 50;
        int x = 50;

        // 1. En-tête Cabinet (Gauche)
        g2d.setFont(new Font("Serif", Font.BOLD, 16));
        g2d.setColor(new Color(0, 51, 102)); // Bleu foncé
        g2d.drawString("CABINET DENTAIRE DENTAL TECH", x, y);
        y += 20;
        g2d.setFont(new Font("Serif", Font.PLAIN, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Dr. Mohamed ALAMI", x, y);
        y += 15;
        g2d.drawString("Chirurgien Dentiste", x, y);
        y += 15;
        g2d.drawString("12 Av. Mohammed V, Rabat", x, y);
        y += 15;
        g2d.drawString("Tél: 05 37 00 00 00", x, y);

        // 2. Date et Lieu (Droite)
        y = 80;
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        g2d.drawString("Le " + LocalDate.now(), 400, y);

        // 3. Info Patient (Cadre)
        y = 150;
        g2d.drawRect(x, y, 500, 60);
        g2d.setFont(new Font("Serif", Font.BOLD, 12));
        g2d.drawString("Nom & Prénom : " + patient.getNom() + " " + patient.getPrenom(), x + 10, y + 25);
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        g2d.drawString("Age : " + (LocalDate.now().getYear() - patient.getDateDeNaissance().getYear()) + " ans", x + 10, y + 45);

        // 4. Titre
        y += 100;
        g2d.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        g2d.drawString("Ordonnance", 230, y);
        g2d.drawLine(230, y+5, 360, y+5); // Souligné

        // 5. Liste des Médicaments
        y += 50;
        g2d.setFont(new Font("Serif", Font.BOLD, 12));

        for (Prescription p : prescriptions) {
            Medicament m = medRepo.findById(p.getIdMct());
            String nomMed = (m != null) ? m.getNom() : "Médicament ID " + p.getIdMct();

            // Puce
            g2d.drawString("• " + nomMed, x + 20, y);

            // Détails (Quantité, Fréquence)
            y += 20;
            g2d.setFont(new Font("Serif", Font.ITALIC, 10));
            g2d.drawString("   Qte: " + p.getQuantite() + " | " + p.getFrequence() + " pendant " + p.getDureeEnJours() + " jours", x + 20, y);

            // Ligne suivante
            y += 30;
            g2d.setFont(new Font("Serif", Font.BOLD, 12));
        }

        // 6. Signature (Bas Droite)
        y = 700;
        g2d.setFont(new Font("Serif", Font.PLAIN, 12));
        g2d.drawString("Signature & Cachet", 400, y);
        g2d.drawRect(380, y + 10, 150, 80);

        return PAGE_EXISTS;
    }

    // --- Méthode statique pour lancer l'impression ---
    public static void imprimer(Ordonnance ord, Patient p, List<Prescription> presciptions) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new OrdonnancePrinter(ord, p, presciptions));

        // Affiche la boîte de dialogue système (Choix de l'imprimante ou PDF)
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
}