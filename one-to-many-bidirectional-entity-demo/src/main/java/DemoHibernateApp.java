import entities.Order;
import entities.Product;
import entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;


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
        run();
    }

    public static void run() {
        // Initialize connection to DB
        SessionFactory factory = hibernateConfiguration().buildSessionFactory();

        // Here we open new session to DB
        Session session = factory.openSession();
        Transaction transaction = null;

        // Add new user with some orders
        User user1 = new User();
        user1.setLogin("joe");
        user1.setName("Joe");

        user1.addOrder(new Order("Joe's order #1"));
        user1.addOrder(new Order("Joe's order #2"));
        user1.addOrder(new Order("Joe's order #3"));

        try {
            transaction = session.beginTransaction();

            // 'persist(user1)' method forces Hibernate to issue INSERT new user entity
            // into database and INSERT his orders
            session.persist(user1);

            transaction.commit();
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
                exception.printStackTrace();
            }
        } finally {
            session.close();
        }

        session = factory.openSession();
        transaction = session.beginTransaction();

        // Get list of users with orders, modify and print them
        List users = session.createQuery("from User").list();

        users.forEach(new Consumer<User>() {
            @Override
            public void accept(User user) {
                System.out.println(user.getName());

                user.getOrders().forEach(new Consumer<Order>() {
                    @Override
                    public void accept(Order order) {
                        System.out.println("  " + order.getDescription());
                        order.setDescription(order.getDescription() + " - modified");
                    }
                });

            }
        });

        transaction.commit();
        session.close();

        session = factory.openSession();
        transaction = session.beginTransaction();

        // Get list of users with orders, and print them
        users = session.createQuery("from User").list();

        users.forEach(new Consumer<User>() {
            @Override
            public void accept(User user) {
                System.out.println(user.getName());

                user.getOrders().forEach(new Consumer<Order>() {
                    @Override
                    public void accept(Order order) {
                        System.out.println("  " + order.getDescription());
                    }
                });
            }
        });

        transaction.commit();
        session.close();

        // Test prevent delete user if it has orders
        final Session sessionToDelete = factory.openSession();
        try {
            sessionToDelete.beginTransaction();
            users.forEach(new Consumer<User>() {
                @Override
                public void accept(User user) {
                    sessionToDelete.remove(user);
                }
            });
            sessionToDelete.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionToDelete.close();
            session.close();
        }

        // Delete orders
        session = factory.openSession();
        transaction = session.beginTransaction();

        // Get list of users with orders, and print them
        users = session.createQuery("from User").list();
        final Session finalSession = session;
        users.forEach(new Consumer<User>() {
            @Override
            public void accept(User user) {
                // Unlink order from user and automatically delete it from DB (orphanRemoval)
                user.deleteOrders();
                finalSession.remove(user);
            }
        });

        transaction.commit();
        session.close();

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
        dbConfig.addAnnotatedClass(User.class);
        dbConfig.addAnnotatedClass(Order.class);

        // Set 'false' value for property bellow on production server!
        dbConfig.setProperty("hibernate.show_sql", "true");
        dbConfig.setProperty("hibernate.format_sql", "true");

        return dbConfig;
    }

}
