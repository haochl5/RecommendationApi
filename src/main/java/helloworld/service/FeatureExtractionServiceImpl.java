package helloworld.service;

import helloworld.Model.Item;
import helloworld.Model.User;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FeatureExtractionServiceImpl implements FeatureExtractionService {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");
    private static final Map<String, Integer> CATEGORY_ENCODING = Map.of(
        "electronics", 1,
        "clothing", 2,
        "books", 3,
        "home", 4
    );
    
    @Override
    public Map<String, Object> extractItemFeatures(Item item) {
        Map<String, Object> features = new HashMap<>();
        
        // Description features (bag-of-words)
        Map<String, Integer> textFeatures = extractTextFeatures(item.getDescription());
        features.put("description_length", item.getDescription() != null ? item.getDescription().length() : 0);
        features.put("word_count", textFeatures.size());
        features.put("avg_word_length", calculateAverageWordLength(textFeatures));
        
        // Category encoding
        features.put("category_encoded", encodeCategory(item.getCategory()));
        
        // Price features
        features.put("price", item.getPrice() != null ? item.getPrice() : 0.0);
        features.put("has_price", item.getPrice() != null && item.getPrice() > 0 ? 1.0 : 0.0);
        
        // Rating (simulated - in real app this would come from reviews)
        features.put("rating", generateSimulatedRating(item));
        
        // Views (simulated - in real app this would be tracked)
        features.put("views", generateSimulatedViews(item));
        
        // Popularity score based on views and rating
        features.put("popularity", calculatePopularityScore(features));
        
        return features;
    }
    
    @Override
    public Map<String, Object> extractUserFeatures(User user, List<String> purchasedItems, 
                                                 Double totalOrderAmount, Integer region) {
        Map<String, Object> features = new HashMap<>();
        
        // Basic user features
        features.put("user_age_days", calculateUserAgeInDays(user));
        features.put("is_active", user.isActive() ? 1.0 : 0.0);
        
        // Purchase behavior features
        features.put("purchase_count", purchasedItems.size());
        features.put("total_order_amount", totalOrderAmount != null ? totalOrderAmount : 0.0);
        features.put("avg_order_value", purchasedItems.size() > 0 ? 
                    (totalOrderAmount != null ? totalOrderAmount / purchasedItems.size() : 0.0) : 0.0);
        
        // Region encoding
        features.put("region", region != null ? region : 1);
        
        // Purchase frequency (orders per month)
        features.put("purchase_frequency", calculatePurchaseFrequency(user, purchasedItems.size()));
        
        // Account activity
        features.put("account_activity", calculateAccountActivity(user));
        
        return features;
    }
    
    @Override
    public Map<String, Double> normalizeFeatures(Map<String, Object> features) {
        Map<String, Double> normalized = new HashMap<>();
        
        // Define normalization ranges for different features
        Map<String, Double[]> normalizationRanges = Map.of(
            "description_length", new Double[]{0.0, 1000.0},
            "word_count", new Double[]{0.0, 100.0},
            "avg_word_length", new Double[]{0.0, 20.0},
            "category_encoded", new Double[]{1.0, 4.0},
            "price", new Double[]{0.0, 1000.0},
            "rating", new Double[]{0.0, 5.0},
            "views", new Double[]{0.0, 10000.0},
            "popularity", new Double[]{0.0, 1.0},
            "user_age_days", new Double[]{0.0, 3650.0}, // 10 years
            "purchase_count", new Double[]{0.0, 100.0},
            "total_order_amount", new Double[]{0.0, 10000.0},
            "avg_order_value", new Double[]{0.0, 1000.0},
            "region", new Double[]{1.0, 4.0},
            "purchase_frequency", new Double[]{0.0, 10.0},
            "account_activity", new Double[]{0.0, 1.0}
        );
        
        for (Map.Entry<String, Object> entry : features.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                Double[] range = normalizationRanges.get(key);
                
                if (range != null) {
                    // Min-max normalization
                    double normalizedValue = (numValue - range[0]) / (range[1] - range[0]);
                    normalizedValue = Math.max(0.0, Math.min(1.0, normalizedValue)); // Clamp to [0,1]
                    normalized.put(key, normalizedValue);
                } else {
                    normalized.put(key, numValue);
                }
            }
        }
        
        return normalized;
    }
    
    @Override
    public double[] featuresToVector(Map<String, Double> normalizedFeatures) {
        // Define the order of features in the vector
        String[] featureOrder = {
            "description_length", "word_count", "avg_word_length", "category_encoded",
            "price", "rating", "views", "popularity", "user_age_days", "is_active",
            "purchase_count", "total_order_amount", "avg_order_value", "region",
            "purchase_frequency", "account_activity"
        };
        
        double[] vector = new double[featureOrder.length];
        for (int i = 0; i < featureOrder.length; i++) {
            vector[i] = normalizedFeatures.getOrDefault(featureOrder[i], 0.0);
        }
        
        return vector;
    }
    
    @Override
    public Map<String, Integer> extractTextFeatures(String description) {
        if (description == null || description.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = WORD_PATTERN.matcher(description.toLowerCase()).results()
            .map(match -> match.group())
            .toArray(String[]::new);
        
        for (String word : words) {
            if (word.length() > 2) { // Ignore very short words
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        
        return wordCount;
    }
    
    @Override
    public Integer encodeCategory(String category) {
        return CATEGORY_ENCODING.getOrDefault(category.toLowerCase(), 1);
    }
    
    @Override
    public double[] generatePurchaseTypeVector(List<String> purchasedItems, Map<String, String> itemCategories) {
        // Initialize vector for 4 purchase types (electronics, clothing, books, home)
        double[] vector = new double[4];
        
        if (purchasedItems.isEmpty()) {
            return vector;
        }
        
        // Count purchases by category
        int[] categoryCounts = new int[4];
        for (String itemId : purchasedItems) {
            String category = itemCategories.get(itemId);
            if (category != null) {
                Integer encoded = encodeCategory(category);
                if (encoded >= 1 && encoded <= 4) {
                    categoryCounts[encoded - 1]++;
                }
            }
        }
        
        // Convert counts to probabilities
        int totalPurchases = purchasedItems.size();
        for (int i = 0; i < 4; i++) {
            vector[i] = totalPurchases > 0 ? (double) categoryCounts[i] / totalPurchases : 0.0;
        }
        
        return vector;
    }
    
    // Helper methods
    private double calculateAverageWordLength(Map<String, Integer> textFeatures) {
        if (textFeatures.isEmpty()) return 0.0;
        
        int totalLength = textFeatures.keySet().stream()
            .mapToInt(String::length)
            .sum();
        return (double) totalLength / textFeatures.size();
    }
    
    private double generateSimulatedRating(Item item) {
        // Simulate rating based on item characteristics
        Random random = new Random(item.getItemId().hashCode());
        double baseRating = 3.0;
        
        // Higher rating for items with descriptions
        if (item.getDescription() != null && item.getDescription().length() > 50) {
            baseRating += 0.5;
        }
        
        // Higher rating for items with prices
        if (item.getPrice() != null && item.getPrice() > 0) {
            baseRating += 0.3;
        }
        
        return Math.min(5.0, Math.max(1.0, baseRating + random.nextGaussian() * 0.5));
    }
    
    private long generateSimulatedViews(Item item) {
        // Simulate views based on item characteristics
        Random random = new Random(item.getItemId().hashCode());
        long baseViews = 100;
        
        // More views for items with descriptions
        if (item.getDescription() != null && item.getDescription().length() > 50) {
            baseViews += 200;
        }
        
        // More views for items with prices
        if (item.getPrice() != null && item.getPrice() > 0) {
            baseViews += 100;
        }
        
        return baseViews + random.nextInt(1000);
    }
    
    private double calculatePopularityScore(Map<String, Object> features) {
        double rating = ((Number) features.getOrDefault("rating", 0.0)).doubleValue();
        long views = ((Number) features.getOrDefault("views", 0L)).longValue();
        
        // Normalize and combine rating and views
        double normalizedRating = rating / 5.0;
        double normalizedViews = Math.min(1.0, views / 1000.0);
        
        return (normalizedRating * 0.6 + normalizedViews * 0.4);
    }
    
    private long calculateUserAgeInDays(User user) {
        return java.time.Duration.between(user.getCreatedAt(), java.time.LocalDateTime.now()).toDays();
    }
    
    private double calculatePurchaseFrequency(User user, int purchaseCount) {
        long daysSinceCreation = calculateUserAgeInDays(user);
        if (daysSinceCreation == 0) return 0.0;
        
        return (double) purchaseCount / (daysSinceCreation / 30.0); // Purchases per month
    }
    
    private double calculateAccountActivity(User user) {
        if (user.getLastLogin() == null) return 0.0;
        
        long daysSinceLastLogin = java.time.Duration.between(user.getLastLogin(), java.time.LocalDateTime.now()).toDays();
        return Math.max(0.0, 1.0 - (daysSinceLastLogin / 30.0)); // Activity score based on recent login
    }
}
