package ma.dentalTech.conf;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ma.dentalTech.conf.util.PropertiesExtractor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion optimisé avec HikariCP (Connection Pool).
 * Remplace l'ancienne implémentation "Une connexion unique" par un pool performant.
 */
public final class SessionFactory {

    private static volatile SessionFactory INSTANCE;
    private final HikariDataSource ds;

    // Constantes
    private static final String PROPS_PATH = "config/db.properties";
    private static final String URL_KEY    = "datasource.url";
    private static final String USER_KEY   = "datasource.user";
    private static final String PASS_KEY   = "datasource.password";
    private static final String DRIVER_KEY = "datasource.driver";

    private SessionFactory() {
        var properties = PropertiesExtractor.loadConfigFile(PROPS_PATH);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertiesExtractor.getPropertyValue(URL_KEY, properties));
        config.setUsername(PropertiesExtractor.getPropertyValue(USER_KEY, properties));
        config.setPassword(PropertiesExtractor.getPropertyValue(PASS_KEY, properties));
        
        String driver = PropertiesExtractor.getPropertyValue(DRIVER_KEY, properties);
        if(driver != null && !driver.isBlank()) {
            config.setDriverClassName(driver);
        }

        // Configuration du pool
        config.setMaximumPoolSize(10); // Max 10 connexions simultanées
        config.setMinimumIdle(2);      // Min 2 connexions en attente
        config.setIdleTimeout(30000);  // 30 sec d'inactivité avant fermeture
        config.setConnectionTimeout(30000);
        config.setPoolName("DentalTechPool");

        this.ds = new HikariDataSource(config);
        System.out.println("Connexion a la base de donnees etablie.");
    }

    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionFactory();
                }
            }
        }
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeConnection() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            System.out.println("Connexion a la base de donnees fermee.");
        }
    }
}
