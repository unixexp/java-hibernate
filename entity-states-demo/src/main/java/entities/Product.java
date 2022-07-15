package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue
    private UUID id;

    @Column(name = "published_on")
    private Timestamp publishedOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    @Column(name = "delivery_on")
    private Timestamp deliveryOn;

    @Column(name = "name", length = 128, nullable = true, unique = true)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private double price;

    @Column(length = 512, nullable = true)
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Timestamp getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(Timestamp publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Timestamp getDeliveryOn() {
        return deliveryOn;
    }

    public void setDeliveryOn(Timestamp deliveryOn) {
        this.deliveryOn = deliveryOn;
    }

    @PrePersist
    protected void onCreate() {
        publishedOn = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Timestamp(System.currentTimeMillis());
    }
}
