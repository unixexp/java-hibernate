import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/*
    Go to pgadmin tool, run query:

    SELECT * FROM pg_stat_activity
    WHERE client_addr != '127.0.0.1'
    AND client_addr IS NOT null;

    run this application and update this query result after every stage (waitBeforeRun)
    and you can see when really hibernate create connection to DB and when it closed.

    If you run this application on the same server when DB server ran, just delete
    "client_addr != '127.0.0.1'" statement.

    P.s. Hibernate start db connection when statement "factory = hibernateConfiguration().buildSessionFactory();"
    will be done. Statement "session = factory.openSession();" is not additional DB connection(session), it is
    Hibernate framework inner session (It is no db connection).

 */

public class DemoHibernateApp {

    private static String DB_HOST = "wapi.local";
    private static String DB_PORT = "5432";
    private static String DB_NAME = "markets";
    private static String DB_SCHEMA = "musician";
    private static String DB_USER = "musician";
    private static String DB_PASSWORD = "musician_pwd";

    private static SessionFactory factory;
    private static Session session;

    public static void main(String[] args) {
        debugDBConnection();
    }

    public static void debugDBConnection() {
        Util.waitBeforeRun(10, "Session factory initializing", new Util.Command() {
            @Override
            public void run() {
                factory = hibernateConfiguration().buildSessionFactory();
            }
        });

        Util.waitBeforeRun(10, "Open hibernate session", new Util.Command() {
            @Override
            public void run() {
                session = factory.openSession();
            }
        });

        Util.waitBeforeRun(30, "Close hibernate session", new Util.Command() {
            @Override
            public void run() {
                session.close();
            }
        });

        Util.waitBeforeRun(60, "Session factory will be closed", new Util.Command() {
            @Override
            public void run() {
                factory.close();
            }
        });
    }

    public static Configuration hibernateConfiguration() {
        String DB_CONNECTION_URL = new StringBuilder()
                .append("jdbc:postgresql://")
                .append(DB_HOST).append(":")
                .append(DB_PORT).append("/")
                .append(DB_NAME)
                .toString();

        Configuration dbConfig = new Configuration();
        dbConfig.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        dbConfig.setProperty("hibernate.connection.url", DB_CONNECTION_URL);
        dbConfig.setProperty("hibernate.default_schema", DB_SCHEMA);
        dbConfig.setProperty("hibernate.connection.username", DB_USER);
        dbConfig.setProperty("hibernate.connection.password", DB_PASSWORD);
        dbConfig.setProperty("hibernate.hbm2ddl.auto", "create");
        dbConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        // dbConfig.setProperty("hibernate.jdbc.time_zone", "UTC");

        // Set 'false' value for property bellow on production server!
        dbConfig.setProperty("hibernate.show_sql", "true");
        dbConfig.setProperty("hibernate.format_sql", "true");

        return dbConfig;
    }

}
