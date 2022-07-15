package entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends MarketEntity {

    @Column(name = "login", length = 128, nullable = true, unique = false)
    private String login;

    @Column(name = "name", length = 128, nullable = true, unique = false)
    private String name;

    // CascadeType.ALL is necessary if Uni-directional mapping using
    // When 'User' entity saving, also it save all added 'Order' entities
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Order> orders = new ArrayList<>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}
