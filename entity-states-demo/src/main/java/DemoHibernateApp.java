import entities.Product;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;


public class DemoHibernateApp {

    private static String DB_HOST = "wapi.local";
    private static String DB_PORT = "5432";
    private static String DB_NAME = "markets";
    private static String DB_SCHEMA = "musician";
    private static String DB_USER = "musician";
    private static String DB_PASSWORD = "musician_pwd";

    public static void main(String[] args) {
        SessionFactory factory = hibernateConfiguration().buildSessionFactory();

        String productName = "Fender American Professional II Stratocaster";
        Product product = new Product();
        product.setName(productName);
        product.setPrice(1302);
        product.setDeliveryOn(Util.dateStringToTimestamp("2022-02-01"));

        log("\n------------------------------------- Try to create new product ------------------------------------\n");

        log("Open session");
        Session session = factory.openSession();

        log("Begin transaction");
        session.beginTransaction();

        log(String.format("Create product '%s", productName));
        session.persist(product);

        session.getTransaction().commit();
        log("Transaction committed");
        log(String.format("Product '%s' has been created with id %s", productName, product.getId()));

        session.close();
        log("Session closed.");

        log("\n---------------------------- Try to update product after session closed ----------------------------\n");

        log("Open session");
        session = factory.openSession();

        log("Begin transaction");
        session.beginTransaction();

        try {
            product.setPrice(500);
            session.persist(product);
            session.getTransaction().commit();
            log("Transaction committed");
        } catch (PersistenceException e) {
            if (session.getTransaction() != null)
                session.getTransaction().rollback();
            if (e.getMessage().indexOf("detached entity") != -1) {
                log(String.format("\n!!!!!!! Entity '%s' has 'detached' state after session close.\n" +
                        "We have load it from database or use merge method to set it state to 'persistent'\n",
                        productName));
            } else {
                e.printStackTrace();
            }
        } finally {
            session.close();
            log("Session closed.");
        }

        log("\n----- Try to update product after session closed (Use 'merge() method to reattach entity'  ---------\n");

        log("Open session");
        session = factory.openSession();
        session.merge(product);

        log("Begin transaction");
        session.beginTransaction();

        log("Check 'product' entity state by session.contains(product) method");
        if (session.contains(product)) {
            log("\n!!!!!!!'product' entity was not detached after session closed. Check your code.");
        } else {
            log("Ok, 'product' entity was detached from session after that was closed.");
            log("Try to reattach 'product' to hibernate session context by using 'merge()' method");
            log("The same result we can get by session.get(product) method or session.createQuery(..) for product");
            product = (Product) session.merge(product);
        }

        product.setPrice(300);
        session.persist(product);
        session.getTransaction().commit();
        log("Transaction committed");

        log(String.format("\nNow '%s' has updated price '%.2f'\n", productName, product.getPrice()));

        log("\n--------------------- Try to update product in currency session again ------------------------------\n");

        if (session.contains(product)) {
            log("Entity has 'persist/attached' state in current session before session will no be closed or\n" +
                    "entity will not be set to 'detached' state manually by 'evict()' method.\n" +
                    "So we don't use 'update()' or 'persist()' methods to update.\nJust set new price by setter " +
                    "method and commit transaction.");
        } else {
            log("\n!!!!!!!'product' entity was detached but current session wan not be closed. Check your code.");
        }

        session.beginTransaction();
        product.setPrice(250);
        session.getTransaction().commit();

        log(String.format("\nNow '%s' has updated price '%.2f'\n", productName, product.getPrice()));

        session.close();
        log("Session closed.");

        log("\n--------In this example we try to add new product with non-unique name (remove old first)-----------\n");

        log("Open session");
        session = factory.openSession();

        log("Begin transaction");
        session.beginTransaction();

        Product newProduct = new Product();
        newProduct.setName(productName);
        newProduct.setPrice(1302);
        newProduct.setDeliveryOn(Util.dateStringToTimestamp("2022-02-01"));

        try {
            log("Set to remove product from database:\n");
            List<Product> products = session.createQuery("from Product", Product.class).getResultList();
            for (Product p : products) {
                log("Planned to remove - " + p.getName());
                session.remove(p);
            }

            log("Planned to add - " + newProduct.getName());
            session.persist(newProduct);

            session.getTransaction().commit();
            log("Transaction committed");
        } catch (PersistenceException e) {
            if (session.getTransaction() != null)
                session.getTransaction().rollback();
            if (e.getMessage().indexOf("ConstraintViolationException") != -1) {
                log("\nWe received ConstraintViolationException although said to hibernate to delete old product!");
                log("It happens because DELETE statements are executed right at the end of the flush while\n" +
                        "the INSERT statements are executed towards the beginning.\n\n" +
                        "One way to work around this issue is to manual flush the\n" +
                        "Persistence Context after the remove operation. Let's do it!");
            } else {
                e.printStackTrace();
            }
        } finally {
            session.close();
            log("Session closed.");
        }

        log("\n--------Try to add new product with non-unique name (remove old first with manual flush())----------\n");

        log("Open session");
        session = factory.openSession();

        log("Begin transaction");
        session.beginTransaction();

        log("Set to remove product from database:\n");
        List<Product> products = session.createQuery("from Product", Product.class).getResultList();
        for (Product p : products) {
            log("Planned to remove - " + p.getName());
            session.remove(p);
        }

        log("Manual run flush() session to accept remove statements");
        session.flush();

        newProduct = new Product();
        newProduct.setName(productName);
        newProduct.setPrice(1302);
        newProduct.setDeliveryOn(Util.dateStringToTimestamp("2022-02-01"));
        log("Planned to add - " + newProduct.getName());
        session.persist(newProduct);

        session.getTransaction().commit();
        log("Transaction committed");

        session.close();
        log("Session closed.");

        log("\nTIP! In reality, you are better off updating the existing entity instead of\n" +
                "removing and reinserting it back with the same business key");

        factory.close();
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

        dbConfig.addAnnotatedClass(Product.class);

        // Set 'false' value for property bellow on production server!
        dbConfig.setProperty("hibernate.show_sql", "true");
        dbConfig.setProperty("hibernate.format_sql", "true");

        return dbConfig;
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static void doInNewSession(Command command) {

    }

    private interface Command<T> {

        public T call();

    }

}
