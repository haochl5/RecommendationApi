package helloworld.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_clusters")
public class UserCluster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "cluster_id", nullable = false)
    private Integer clusterId;
    
    @Column(name = "purchased_items", columnDefinition = "JSON")
    private String purchasedItems; // JSON array of item IDs
    
    @Column(name = "total_order_amount")
    private Double totalOrderAmount;
    
    @Column(name = "region")
    private Integer region; // 1,2,3,4 representing different regions
    
    @Column(name = "purchase_type_vector", columnDefinition = "JSON")
    private String purchaseTypeVector; // JSON array of probabilities for different purchase types
    
    @Column(name = "feature_vector", columnDefinition = "JSON")
    private String featureVector; // JSON array of normalized features for clustering
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserCluster() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public UserCluster(String userId, Integer clusterId, String purchasedItems, 
                      Double totalOrderAmount, Integer region, String purchaseTypeVector, 
                      String featureVector) {
        this();
        this.userId = userId;
        this.clusterId = clusterId;
        this.purchasedItems = purchasedItems;
        this.totalOrderAmount = totalOrderAmount;
        this.region = region;
        this.purchaseTypeVector = purchaseTypeVector;
        this.featureVector = featureVector;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Integer getClusterId() { return clusterId; }
    public void setClusterId(Integer clusterId) { this.clusterId = clusterId; }
    
    public String getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(String purchasedItems) { this.purchasedItems = purchasedItems; }
    
    public Double getTotalOrderAmount() { return totalOrderAmount; }
    public void setTotalOrderAmount(Double totalOrderAmount) { this.totalOrderAmount = totalOrderAmount; }
    
    public Integer getRegion() { return region; }
    public void setRegion(Integer region) { this.region = region; }
    
    public String getPurchaseTypeVector() { return purchaseTypeVector; }
    public void setPurchaseTypeVector(String purchaseTypeVector) { this.purchaseTypeVector = purchaseTypeVector; }
    
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
