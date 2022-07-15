import entities.Order;
import entities.Product;
import entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

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
        try {
            transaction = session.beginTransaction();

            User user1 = new User();
            user1.setLogin("joe");
            user1.setName("Joe");

            user1.addOrder(new Order("Joe's order #1"));
            user1.addOrder(new Order("Joe's order #2"));
            user1.addOrder(new Order("Joe's order #3"));

            session.save(user1);

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

        // Get list of users with orders and print them
        List users = session.createQuery("from User").list();

        // If 'LAZY' fetch mechanism used, call 'getOrders' method initialize request to DB to
        // get orders collection. When in happened it is very important to hibernate session was opened.
        //
        // If used 'EAGER' mechanism, request for 'orders' to DB will initialize urgent after user list
        // will be received
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
