package helloworld.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_clusters")
public class ItemCluster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private String itemId;
    
    @Column(name = "cluster_id", nullable = false)
    private Integer clusterId;
    
    @Column(name = "description_vector", columnDefinition = "JSON")
    private String descriptionVector; // Word2Vec or bag-of-words vector
    
    @Column(name = "category_encoded")
    private Integer categoryEncoded; // 1,2,3,4 representing categories
    
    @Column(name = "price_normalized")
    private Double priceNormalized; // Normalized price value
    
    @Column(name = "rating")
    private Double rating; // Average rating from reviews
    
    @Column(name = "views")
    private Long views; // Number of views
    
    @Column(name = "feature_vector", columnDefinition = "JSON")
    private String featureVector; // JSON array of normalized features for clustering
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ItemCluster() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ItemCluster(String itemId, Integer clusterId, String descriptionVector, 
                      Integer categoryEncoded, Double priceNormalized, Double rating, 
                      Long views, String featureVector) {
        this();
        this.itemId = itemId;
        this.clusterId = clusterId;
        this.descriptionVector = descriptionVector;
        this.categoryEncoded = categoryEncoded;
        this.priceNormalized = priceNormalized;
        this.rating = rating;
        this.views = views;
        this.featureVector = featureVector;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    
    public Integer getClusterId() { return clusterId; }
    public void setClusterId(Integer clusterId) { this.clusterId = clusterId; }
    
    public String getDescriptionVector() { return descriptionVector; }
    public void setDescriptionVector(String descriptionVector) { this.descriptionVector = descriptionVector; }
    
    public Integer getCategoryEncoded() { return categoryEncoded; }
    public void setCategoryEncoded(Integer categoryEncoded) { this.categoryEncoded = categoryEncoded; }
    
    public Double getPriceNormalized() { return priceNormalized; }
    public void setPriceNormalized(Double priceNormalized) { this.priceNormalized = priceNormalized; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Long getViews() { return views; }
    public void setViews(Long views) { this.views = views; }
    
    public String getFeatureVector() { return featureVector; }
    public void setFeatureVector(String featureVector) { this.featureVector = featureVector; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
