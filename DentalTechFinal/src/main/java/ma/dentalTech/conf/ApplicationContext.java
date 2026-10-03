package ma.dentalTech.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// --- Imports Module Patient (Reflection) ---
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

// --- Imports Module RDV ---
import ma.dentalTech.repository.modules.rdvPatient.api.RDVRepository;
import ma.dentalTech.repository.modules.rdvPatient.impl.mySQL.RDVRepositoryImpl;
import ma.dentalTech.service.modules.rdvPatient.api.RDVService;
import ma.dentalTech.service.modules.rdvPatient.impl.RDVServiceImpl;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.api.RDVController;
import ma.dentalTech.mvc.controllers.modules.rdvPatient.impl.RDVControllerImpl;

// --- Imports Module Ordonnance ---
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.ordonnance.api.PrescriptionRepository;
import ma.dentalTech.repository.modules.ordonnance.impl.mySQL.OrdonnanceRepositoryImpl;
import ma.dentalTech.repository.modules.ordonnance.impl.mySQL.PrescriptionRepositoryImpl;
import ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService;
import ma.dentalTech.service.modules.ordonnance.impl.OrdonnanceServiceImpl;
import ma.dentalTech.mvc.controllers.modules.ordonnance.api.OrdonnanceController;
import ma.dentalTech.mvc.controllers.modules.ordonnance.impl.OrdonnanceControllerImpl;

public class ApplicationContext {

    // --- Partie Singleton ---
    private static ApplicationContext instance;

    // --- Partie Dynamique (Code du Prof pour Patient) ---
    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    // --- Partie Statique (Tes Modules : RDV & Ordonnance) ---
    // RDV
    private final RDVRepository rdvRepository;
    private final RDVService rdvService;

    // Ordonnance
    private final OrdonnanceRepository ordRepository;
    private final PrescriptionRepository prescRepository;
    private final OrdonnanceService ordService;

    // Ajout du champ Controller
    private final RDVController rdvController;

    private final OrdonnanceController ordController;

    // =========================================================
    // CONSTRUCTEUR UNIQUE (Privé pour Singleton)
    // =========================================================
    private ApplicationContext() {
        // -----------------------------------------------------
        // 1. CHARGEMENT DYNAMIQUE (Beans.properties - Patient)
        // -----------------------------------------------------
        loadBeansFromProperties();

        // -----------------------------------------------------
        // 2. CHARGEMENT MANUEL (Module RDV)
        // -----------------------------------------------------
        this.rdvRepository = new RDVRepositoryImpl();
        this.rdvService = new RDVServiceImpl(this.rdvRepository);

        // -----------------------------------------------------
        // 3. CHARGEMENT MANUEL (Module Ordonnance)
        // -----------------------------------------------------
        this.ordRepository = new OrdonnanceRepositoryImpl();
        this.prescRepository = new PrescriptionRepositoryImpl();
        this.ordService = new OrdonnanceServiceImpl(this.ordRepository, this.prescRepository);

        // --- 3. Initialisation du Controller (Injection du Service) ---
        this.rdvController = new RDVControllerImpl(this.rdvService);

        // --- Init Controller Ordonnance ---
        this.ordController = new OrdonnanceControllerImpl(this.ordService);

    }

    // =========================================================
    // MÉTHODE SINGLETON
    // =========================================================
    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    // =========================================================
    // GETTERS POUR TES SERVICES (RDV & Ordonnance)
    // =========================================================

    public RDVService getRdvService() {
        return rdvService;
    }

    public OrdonnanceService getOrdService() {
        return ordService;
    }

    // Getter
    public RDVController getRdvController() {
        return rdvController;
    }

    public OrdonnanceController getOrdController() {
        return ordController;
    }
    // =========================================================
    // MÉTHODES UTILITAIRES (Partie Prof)
    // =========================================================

    private void loadBeansFromProperties() {
        var configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/beans.properties");

        if (configFile != null) {
            Properties properties = new Properties();
            try {
                properties.load(configFile);
                String daoClassName = properties.getProperty("patientRepo");
                String servClassName = properties.getProperty("patientService");
                String ctrlClassName = properties.getProperty("patientController");

                // Instanciation via Réflexion
                if (daoClassName != null && servClassName != null && ctrlClassName != null) {
                    Class<?> cRepository = Class.forName(daoClassName);
                    PatientRepository repository = (PatientRepository) cRepository.getDeclaredConstructor().newInstance();

                    Class<?> cService = Class.forName(servClassName);
                    PatientService service = (PatientService) cService.getDeclaredConstructor(PatientRepository.class).newInstance(repository);

                    Class<?> cController = Class.forName(ctrlClassName);
                    PatientController controller = (PatientController) cController.getDeclaredConstructor(PatientService.class).newInstance(service);

                    // Stockage
                    context.put(PatientRepository.class, repository);
                    context.put(PatientService.class, service);
                    context.put(PatientController.class, controller);

                    contextByName.put("patientDao", repository);
                    contextByName.put("patientService", service);
                    contextByName.put("patientController", controller);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Attention : Le fichier beans.properties est introuvable ou incomplet.");
        }
    }

    /**
     * Retourne un composant bean en fonction de son nom (String).
     */
    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    /**
     * Retourne un composant bean en fonction de sa classe.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(context.get(beanClass));
    }


}