package entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "products")
public class Product extends MarketEntity {

    @Column(name = "delivery_on")
    private Timestamp deliveryOn;

    @Column(name = "name", length = 128, nullable = true, unique = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private double price;

    @Column(length = 512, nullable = true)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDeliveryOn() {
        return deliveryOn;
    }

    public void setDeliveryOn(Timestamp deliveryOn) {
        this.deliveryOn = deliveryOn;
    }

}
