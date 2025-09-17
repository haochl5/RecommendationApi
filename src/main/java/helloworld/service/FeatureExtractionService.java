package helloworld.service;

import helloworld.Model.Item;
import helloworld.Model.User;
import java.util.List;
import java.util.Map;

public interface FeatureExtractionService {
    
    /**
     * Extract features from an item for clustering
     * @param item The item to extract features from
     * @return Map containing feature name and value pairs
     */
    Map<String, Object> extractItemFeatures(Item item);
    
    /**
     * Extract features from a user for clustering
     * @param user The user to extract features from
     * @param purchasedItems List of purchased item IDs
     * @param totalOrderAmount Total amount spent by user
     * @param region User's region (1,2,3,4)
     * @return Map containing feature name and value pairs
     */
    Map<String, Object> extractUserFeatures(User user, List<String> purchasedItems, 
                                          Double totalOrderAmount, Integer region);
    
    /**
     * Normalize feature values to 0-1 range
     * @param features Map of features to normalize
     * @return Map of normalized features
     */
    Map<String, Double> normalizeFeatures(Map<String, Object> features);
    
    /**
     * Convert features to vector format for clustering
     * @param normalizedFeatures Normalized feature map
     * @return Array of double values representing the feature vector
     */
    double[] featuresToVector(Map<String, Double> normalizedFeatures);
    
    /**
     * Extract text features from item description using bag-of-words
     * @param description Item description text
     * @return Map of word frequencies
     */
    Map<String, Integer> extractTextFeatures(String description);
    
    /**
     * Encode category string to numeric value
     * @param category Category string
     * @return Encoded category (1,2,3,4)
     */
    Integer encodeCategory(String category);
    
    /**
     * Generate purchase type vector based on purchased items
     * @param purchasedItems List of purchased item IDs
     * @param itemCategories Map of itemId to category
     * @return Array of probabilities for different purchase types
     */
    double[] generatePurchaseTypeVector(List<String> purchasedItems, Map<String, String> itemCategories);
}
