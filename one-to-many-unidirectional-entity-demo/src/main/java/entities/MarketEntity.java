package entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@MappedSuperclass
public abstract class MarketEntity {

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue
    private UUID id;

    @Column(name = "published_on")
    private Timestamp publishedOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @PrePersist
    protected void onCreate() {
        publishedOn = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Timestamp(System.currentTimeMillis());
    }

}
