package helloworld.service;

import helloworld.Model.Item;
import helloworld.Model.ItemCluster;
import helloworld.Model.User;
import helloworld.Model.UserCluster;
import helloworld.repository.ItemRepository;
import helloworld.repository.ItemClusterRepository;
import helloworld.repository.UserRepository;
import helloworld.repository.UserClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataGenerationService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ItemClusterRepository itemClusterRepository;
    
    @Autowired
    private UserClusterRepository userClusterRepository;
    
    @Autowired
    private FeatureExtractionService featureExtractionService;
    
    @Autowired
    private ClusteringService clusteringService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random(42); // Fixed seed for reproducible results
    
    /**
     * Generate fake data for cluster tables
     */
    public void generateClusterData() {
        try {
            // Generate item clusters
            generateItemClusters();
            
            // Generate user clusters
            generateUserClusters();
            
            System.out.println("Successfully generated cluster data");
        } catch (Exception e) {
            System.err.println("Error generating cluster data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void generateItemClusters() {
        List<Item> items = itemRepository.findAll();
        if (items.isEmpty()) {
            System.out.println("No items found to generate clusters for");
            return;
        }
        
        List<ItemCluster> itemClusters = new ArrayList<>();
        
        for (Item item : items) {
            try {
                // Extract features
                Map<String, Object> features = featureExtractionService.extractItemFeatures(item);
                Map<String, Double> normalizedFeatures = featureExtractionService.normalizeFeatures(features);
                double[] featureVector = featureExtractionService.featuresToVector(normalizedFeatures);
                
                // Generate additional fake data
                Double rating = generateFakeRating(item);
                Long views = generateFakeViews(item);
                Double priceNormalized = normalizePrice(item.getPrice());
                
                // Create ItemCluster
                ItemCluster itemCluster = new ItemCluster(
                    item.getItemId(),
                    0, // Will be updated after clustering
                    generateDescriptionVector(item.getDescription()),
                    featureExtractionService.encodeCategory(item.getCategory()),
                    priceNormalized,
                    rating,
                    views,
                    objectMapper.writeValueAsString(Arrays.stream(featureVector).boxed().collect(Collectors.toList()))
                );
                
                itemClusters.add(itemCluster);
            } catch (Exception e) {
                System.err.println("Error processing item " + item.getItemId() + ": " + e.getMessage());
            }
        }
        
        // Perform k-means clustering on items
        if (!itemClusters.isEmpty()) {
            Map<String, Integer> itemToCluster = clusteringService.clusterItems(itemClusters, 4); // 4 clusters
            
            // Update cluster IDs and save
            for (ItemCluster itemCluster : itemClusters) {
                Integer clusterId = itemToCluster.get(itemCluster.getItemId());
                if (clusterId != null) {
                    itemCluster.setClusterId(clusterId);
                    itemClusterRepository.save(itemCluster);
                }
            }
            
            System.out.println("Generated " + itemClusters.size() + " item clusters");
        }
    }
    
    private void generateUserClusters() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found to generate clusters for");
            return;
        }
        
        List<UserCluster> userClusters = new ArrayList<>();
        
        for (User user : users) {
            try {
                // Generate fake user data
                List<String> purchasedItems = generateFakePurchasedItems();
                Double totalOrderAmount = generateFakeTotalOrderAmount();
                Integer region = generateFakeRegion();
                
                // Extract features
                Map<String, Object> features = featureExtractionService.extractUserFeatures(
                    user, purchasedItems, totalOrderAmount, region);
                Map<String, Double> normalizedFeatures = featureExtractionService.normalizeFeatures(features);
                double[] featureVector = featureExtractionService.featuresToVector(normalizedFeatures);
                
                // Generate purchase type vector
                Map<String, String> itemCategories = getItemCategories();
                double[] purchaseTypeVector = featureExtractionService.generatePurchaseTypeVector(
                    purchasedItems, itemCategories);
                
                // Create UserCluster
                UserCluster userCluster = new UserCluster(
                    user.getUsername(), // Using username as userId
                    0, // Will be updated after clustering
                    objectMapper.writeValueAsString(purchasedItems),
                    totalOrderAmount,
                    region,
                    objectMapper.writeValueAsString(Arrays.stream(purchaseTypeVector).boxed().collect(Collectors.toList())),
                    objectMapper.writeValueAsString(Arrays.stream(featureVector).boxed().collect(Collectors.toList()))
                );
                
                userClusters.add(userCluster);
            } catch (Exception e) {
                System.err.println("Error processing user " + user.getUsername() + ": " + e.getMessage());
            }
        }
        
        // Perform k-means clustering on users
        if (!userClusters.isEmpty()) {
            Map<String, Integer> userToCluster = clusteringService.clusterUsers(userClusters, 3); // 3 clusters
            
            // Update cluster IDs and save
            for (UserCluster userCluster : userClusters) {
                Integer clusterId = userToCluster.get(userCluster.getUserId());
                if (clusterId != null) {
                    userCluster.setClusterId(clusterId);
                    userClusterRepository.save(userCluster);
                }
            }
            
            System.out.println("Generated " + userClusters.size() + " user clusters");
        }
    }
    
    // Helper methods for generating fake data
    
    private Double generateFakeRating(Item item) {
        double baseRating = 3.0;
        
        // Higher rating for items with descriptions
        if (item.getDescription() != null && item.getDescription().length() > 50) {
            baseRating += 0.5;
        }
        
        // Higher rating for items with prices
        if (item.getPrice() != null && item.getPrice() > 0) {
            baseRating += 0.3;
        }
        
        // Add some randomness based on item ID for consistency
        Random itemRandom = new Random(item.getItemId().hashCode());
        return Math.min(5.0, Math.max(1.0, baseRating + itemRandom.nextGaussian() * 0.5));
    }
    
    private Long generateFakeViews(Item item) {
        long baseViews = 100;
        
        // More views for items with descriptions
        if (item.getDescription() != null && item.getDescription().length() > 50) {
            baseViews += 200;
        }
        
        // More views for items with prices
        if (item.getPrice() != null && item.getPrice() > 0) {
            baseViews += 100;
        }
        
        // Add randomness based on item ID for consistency
        Random itemRandom = new Random(item.getItemId().hashCode());
        return baseViews + itemRandom.nextInt(1000);
    }
    
    private Double normalizePrice(Double price) {
        if (price == null || price <= 0) return 0.0;
        // Normalize to 0-1 range (assuming max price is 2000)
        return Math.min(1.0, price / 2000.0);
    }
    
    private String generateDescriptionVector(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "[]";
        }
        
        // Simple bag-of-words representation
        Map<String, Integer> wordCount = featureExtractionService.extractTextFeatures(description);
        try {
            return objectMapper.writeValueAsString(wordCount);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private List<String> generateFakePurchasedItems() {
        List<Item> allItems = itemRepository.findAll();
        if (allItems.isEmpty()) return new ArrayList<>();
        
        // Generate 1-5 random purchases
        int numPurchases = random.nextInt(5) + 1;
        List<String> purchasedItems = new ArrayList<>();
        
        for (int i = 0; i < numPurchases; i++) {
            Item randomItem = allItems.get(random.nextInt(allItems.size()));
            if (!purchasedItems.contains(randomItem.getItemId())) {
                purchasedItems.add(randomItem.getItemId());
            }
        }
        
        return purchasedItems;
    }
    
    private Double generateFakeTotalOrderAmount() {
        // Generate amount between 50 and 2000
        return 50.0 + random.nextDouble() * 1950.0;
    }
    
    private Integer generateFakeRegion() {
        // Generate region 1-4
        return random.nextInt(4) + 1;
    }
    
    private Map<String, String> getItemCategories() {
        return itemRepository.findAll().stream()
            .collect(Collectors.toMap(Item::getItemId, Item::getCategory));
    }
}
