package entities;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends MarketEntity {

    @Column(name = "description", length = 128, nullable = true, unique = false)
    private String description;

    public Order(){}

    public Order(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
