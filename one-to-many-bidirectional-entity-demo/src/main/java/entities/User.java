package entities;

import org.hibernate.action.internal.OrphanRemovalAction;

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

    // In mappedBy we defined field 'user' from 'Order' entity
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",
            cascade = {CascadeType.PERSIST},
            orphanRemoval = true)
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
        order.setUser(this);
    }

    public Order deleteOrder(Order order) {
        this.orders.remove(order);
        order.setUser(null);
        return order;
    }

    public void deleteOrders() {
        for (Order order : this.orders) {
            order.setUser(null);
        }
        this.orders.clear();
    }

    @PreRemove
    protected void onDelete() throws PersistenceException {
        if (this.getOrders().size() > 0) {
            throw new PersistenceException("Cannot delete user till it has orders");
        }
    }

}
