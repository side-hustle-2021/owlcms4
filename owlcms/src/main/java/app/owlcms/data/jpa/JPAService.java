/***
 * Copyright (c) 2009-2020 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("Non-Profit OSL" 3.0)
 * License text at https://github.com/jflamy/owlcms4/blob/master/LICENSE.txt
 */
package app.owlcms.data.jpa;

import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_FACTORY;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_DRIVER;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_PASSWORD;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_URL;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_USER;
import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_REFLECTION_OPTIMIZER;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.h2.tools.Server;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import app.owlcms.Main;
import app.owlcms.data.agegroup.AgeGroup;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.category.Category;
import app.owlcms.data.competition.Competition;
import app.owlcms.data.config.Config;
import app.owlcms.data.group.Group;
import app.owlcms.data.platform.Platform;
import app.owlcms.utils.LoggerUtils;
import app.owlcms.utils.StartupUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import app.owlcms.data.customlogin.CustomUser;
import app.owlcms.data.customlogin.CustomRole;

/**
 * Class JPAService.
 */
public class JPAService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(JPAService.class);
    private static final Logger startLogger = (Logger) LoggerFactory.getLogger(Main.class);
    static {
        logger.setLevel(Level.INFO);
    }

    protected static EntityManagerFactory factory;

    /**
     * Close.
     */
    public static void close() {
        if (factory != null) {
            factory.close();
        }
        factory = null;
    }

    /**
     * Inits the database
     *
     * @param inMemory if true, start with in-memory database
     */
    public static void init(boolean inMemory, boolean reset) {
        if (factory == null) {
            factory = getFactory(inMemory, reset);
        }
    }

    public static Properties processSettings(boolean inMemory, boolean reset) throws RuntimeException {
        Properties properties;
        String schemaGeneration = reset ? "drop-and-create" : "update";
        boolean embeddedH2Server = false;

        // Environment variables (set by the operating system or container)
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String postgresHost = System.getenv("POSTGRES_HOST");
        String userName = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");

        if (dbUrl != null) {
            // explicit url provided
            if (inMemory || dbUrl.startsWith("jdbc:h2:mem")) {
                embeddedH2Server = true;
                properties = h2MemProperties(schemaGeneration);
            } else if (dbUrl.startsWith("jdbc:h2:file")) {
                embeddedH2Server = true;
                properties = h2FileProperties(schemaGeneration, dbUrl, userName, password);
            } else if (dbUrl.startsWith("jdbc:h2:")) {
                // remote h2
                embeddedH2Server = false;
                properties = h2ServerProperties(schemaGeneration, dbUrl, userName, password);
            } else if (dbUrl.startsWith("jdbc:postgres")) {
                properties = pgProperties(schemaGeneration, dbUrl, null, null, null, userName, password);
            } else {
                throw new RuntimeException("Unsupported database: " + dbUrl);
            }
        } else if (postgresHost != null) {
            // postgres container configuration
            String postgresPort = System.getenv("POSTGRES_PORT");
            String postgresDb = System.getenv("POSTGRES_DB");
            userName = System.getenv("POSTGRES_USER");
            password = System.getenv("POSTGRES_PASSWORD");
            properties = pgProperties(schemaGeneration, dbUrl, postgresHost, postgresPort, postgresDb, userName,
                    password);
        } else {
            // local h2
            embeddedH2Server = true;
            if (inMemory) {
                properties = h2MemProperties(schemaGeneration);
            } else {
                properties = h2FileProperties(schemaGeneration, dbUrl, userName, password);
            }
        }

        if (embeddedH2Server) {
            startH2EmbeddedServer();
        }

        return properties;
    }

    /**
     * Run in transaction.
     *
     * @param <T>      the generic type
     * @param function the function
     * @return the t
     */
    public static <T> T runInTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = null;

        try {
            if (factory == null) {
                logger.warn("JPAService {}",LoggerUtils.stackTrace());
            }
            entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();

            T result = function.apply(entityManager);

            entityManager.getTransaction().commit();
            return result;

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Entity class names.
     *
     * @return the list
     */
    protected static List<String> entityClassNames() {
        ImmutableList<String> vals = new ImmutableList.Builder<String>()
                .add(Group.class.getName())
                .add(Category.class.getName())
                .add(Athlete.class.getName())
                .add(Platform.class.getName())
                .add(Competition.class.getName())
                .add(AgeGroup.class.getName())
                .add(Config.class.getName())
                .add(CustomUser.class.getName())
                .add(CustomRole.class.getName())
                .build();
        return vals;
    }

    /**
     * Properties for running in memory (used for tests and demos)
     *
     * @return the properties
     */
    protected static Properties h2MemProperties(String schemaGeneration) {
        ImmutableMap<String, Object> vals = jpaProperties();
        Properties props = new Properties();
        props.putAll(vals);

        // keep the database even if all the connections have timed out
        // to turn off transactions MVCC=FALSE;MV_STORE=FALSE;LOCK_MODE=0;
        String url = "jdbc:h2:mem:owlcms;DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4";
        props.put(JPA_JDBC_URL, url);
        props.put(JPA_JDBC_USER, "sa");
        props.put(JPA_JDBC_PASSWORD, "");

        props.put(JPA_JDBC_DRIVER, org.h2.Driver.class.getName());
        props.put("javax.persistence.schema-generation.database.action", schemaGeneration);
        props.put(DIALECT, H2Dialect.class.getName());

        startLogger.info("Database: {}, inMemory={}, schema={}", url, true, schemaGeneration);
        return props;
    }

    /**
     * Gets the factory from code (without a persistance.xml file)
     *
     * @param memoryMode run from memory if true
     * @return an entity manager factory
     */
    private static EntityManagerFactory getFactory(boolean memoryMode, boolean reset) {
        Properties properties = processSettings(memoryMode, reset);

        PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl(JPAService.class.getSimpleName(),
                entityClassNames(), properties);
        Map<String, Object> configuration = new HashMap<>();

        factory = new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo),
                configuration).build();
        return factory;
    }

    private static Properties h2FileProperties(String schemaGeneration, String dbUrl, String userName,
            String password) {
        ImmutableMap<String, Object> vals = jpaProperties();
        Properties props = new Properties();
        props.putAll(vals);

        // use an explicit path as this allows connecting to the H2 server running embedded
        String url;
        String h2Options = ";DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4";
        if (dbUrl == null) {
            String databasePath = new File("database/owlcms.mv.db").getAbsolutePath();
            databasePath = databasePath.substring(0, databasePath.length() - ".mv.db".length());
            url = "jdbc:h2:file:" + databasePath + h2Options;
        } else {
            url = dbUrl.replaceAll("\\.mv\\.db", "") + h2Options;
        }
        

        startLogger.debug("Starting in directory {}", System.getProperty("user.dir"));
        props.put(JPA_JDBC_URL, url);
        props.put(JPA_JDBC_USER, userName != null ? userName : "sa");
        props.put(JPA_JDBC_PASSWORD, password != null ? password : "");

        props.put(JPA_JDBC_DRIVER, org.h2.Driver.class.getName());
        props.put(DIALECT, H2Dialect.class.getName());
        props.put("javax.persistence.schema-generation.database.action", schemaGeneration);

        startLogger.info("Database: {}, inMemory={}, schema={}", url, false, schemaGeneration);
        return props;
    }

    private static Properties h2ServerProperties(String schemaGeneration, String dbUrl, String userName,
            String password) {
        ImmutableMap<String, Object> vals = jpaProperties();
        Properties props = new Properties();
        props.putAll(vals);

        String url = dbUrl;
        props.put(JPA_JDBC_URL, url);
        startLogger.debug("Starting in directory {}", System.getProperty("user.dir"));
        props.put(JPA_JDBC_USER, userName != null ? userName : "sa");
        props.put(JPA_JDBC_PASSWORD, password != null ? password : "");

        props.put(JPA_JDBC_DRIVER, org.h2.Driver.class.getName());
        props.put(DIALECT, H2Dialect.class.getName());
        props.put("javax.persistence.schema-generation.database.action", schemaGeneration);

        startLogger.info("Database: {}, inMemory={}, schema={}", url, false, schemaGeneration);
        return props;
    }

    private static ImmutableMap<String, Object> jpaProperties() {
        String cp = HikariCPConnectionProvider.class.getCanonicalName();
        ImmutableMap<String, Object> vals = new ImmutableMap.Builder<String, Object>()
                .put(HBM2DDL_AUTO, "update")
                .put(SHOW_SQL, false)
                .put(QUERY_STARTUP_CHECKING, false).put(GENERATE_STATISTICS, false)
                .put(USE_REFLECTION_OPTIMIZER, false).put(USE_SECOND_LEVEL_CACHE, true).put(USE_QUERY_CACHE, false)
                .put(USE_STRUCTURED_CACHE, false).put(STATEMENT_BATCH_SIZE, 20)
                .put(CACHE_REGION_FACTORY, "org.hibernate.cache.jcache.JCacheRegionFactory")
                .put("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider")
                .put("hibernate.javax.cache.missing_cache_strategy", "create")
                .put("javax.persistence.sharedCache.mode", "ALL").put("hibernate.c3p0.min_size", 5)
//                .put("hibernate.c3p0.max_size", 20).put("hibernate.c3p0.acquire_increment", 5)
//                .put("hibernate.c3p0.timeout", 84200).put("hibernate.c3p0.preferredTestQuery", "SELECT 1")
//                .put("hibernate.c3p0.testConnectionOnCheckout", true).put("hibernate.c3p0.idle_test_period", 500)
                .put("hibernate.connection.provider_class",cp)
                .put("hibernate.hikari.minimumIdle", "5")
                .put("hibernate.hikari.maximumPoolSize", "10")
                .put("hibernate.hikari.idleTimeout", "300000") // 5 minutes
                .put("hibernate.hikari.maxLifetime", "600000") // 10 minutes (docker kills sockets after 15min)
                .put("hibernate.hikari.initializationFailTimeout", "60000")
                .build();
        return vals;
    }

    private static Properties pgProperties(String schemaGeneration, String dbUrl, String postgresHost,
            String postgresPort, String postgresDb, String userName, String password) {
        ImmutableMap<String, Object> vals = jpaProperties();
        Properties props = new Properties();
        props.putAll(vals);

        postgresHost = postgresHost == null ? "localhost" : postgresHost;
        postgresPort = postgresPort == null ? "5432" : postgresPort;
        postgresDb = postgresDb == null ? "owlcms" : postgresDb;

        if (postgresPort != null && postgresPort.startsWith("tcp:")) {
            postgresHost = null;
            // take the host and port from the port string
            String where = "//";
            int pos = postgresPort.indexOf(where);
            postgresPort = postgresPort.substring(pos + where.length());
        }

        // if running on Heroku, dbUrl is set in the environment
        // if running on K8S, we get a tcp string with the IP address and port
        String url = dbUrl != null ? dbUrl
                : "jdbc:postgresql://" + (postgresHost != null ? postgresHost + ":" : "") + postgresPort + "/"
                        + postgresDb;
        props.put(JPA_JDBC_URL, url);
        props.put(JPA_JDBC_USER, userName != null ? userName : "owlcms");
        props.put(JPA_JDBC_PASSWORD, password != null ? password : "db_owlcms");

        props.put(JPA_JDBC_DRIVER, org.postgresql.Driver.class.getName());
        props.put(DIALECT, org.hibernate.dialect.PostgreSQL95Dialect.class.getName());
        props.put("javax.persistence.schema-generation.database.action", schemaGeneration);

        startLogger.info("Database: {}, schema={}", url, schemaGeneration);
        return props;
    }

    /**
     * H2 can expose its embedded server on demand.
     *
     * <p>
     * Not enabled by default, protected by a feature switch
     * (<code>-DH2ServerPort=9092 or OWLCMS_H2SERVERPORT=9092</code>)
     * <p>
     * When using a tool to connect (e.g. DBVisualizer) the URL given to the tool must include the absolute path to the
     * database for example
     *
     * <pre>
     * jdbc:h2:tcp:localhost:9092/file:C:\Dev\git\owlcms4\owlcms\database;MODE=PostgreSQL
     * </pre>
     */
    private static void startH2EmbeddedServer() {
        Server tcpServer;
        try {
            String h2ServerPort = StartupUtils.getStringParam("H2ServerPort");
            if (h2ServerPort != null && Integer.parseInt(h2ServerPort) > 0) {
                tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2ServerPort, "-tcpDaemon");
                tcpServer.start();
            }
        } catch (SQLException e) {
            logger.error(LoggerUtils.stackTrace(e));
        }
    }

}
