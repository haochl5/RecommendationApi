package helloworld.service;

import helloworld.Model.RecommendationItem;
import helloworld.Model.Item;
import helloworld.Model.ItemCluster;
import helloworld.Model.UserCluster;
import helloworld.repository.ItemRepository;
import helloworld.repository.ItemClusterRepository;
import helloworld.repository.UserClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private ItemClusterRepository itemClusterRepository;
    
    @Autowired
    private UserClusterRepository userClusterRepository;
    
    @Autowired
    private FeatureExtractionService featureExtractionService;
    
    @Autowired
    private ClusteringService clusteringService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<RecommendationItem> getUserRecommendations(String userId, int limit, String category) {
        List<RecommendationItem> recommendations = new ArrayList<>();
        
        try {
            // Get user cluster information
            UserCluster userCluster = userClusterRepository.findByUserId(userId);
            if (userCluster == null) {
                // If user not in cluster, use fallback random recommendations
                return getFallbackRecommendations(limit, category);
            }
            
            // Get items from the same cluster as the user
            List<ItemCluster> clusterItems = itemClusterRepository.findByClusterId(userCluster.getClusterId());
            
            // Filter by category if specified
            if (category != null && !category.isEmpty()) {
                Integer categoryEncoded = featureExtractionService.encodeCategory(category);
                clusterItems = clusterItems.stream()
                    .filter(item -> item.getCategoryEncoded().equals(categoryEncoded))
                    .collect(Collectors.toList());
            }
            
            // Sort by similarity score and take top items
            List<ItemCluster> sortedItems = clusterItems.stream()
                .sorted((a, b) -> Double.compare(
                    calculateItemSimilarityScore(a, userCluster),
                    calculateItemSimilarityScore(b, userCluster)
                ))
                .limit(limit)
                .collect(Collectors.toList());
            
            // Convert to RecommendationItem DTOs
            for (ItemCluster itemCluster : sortedItems) {
                Item item = itemRepository.findByItemId(itemCluster.getItemId());
                if (item != null) {
                    recommendations.add(new RecommendationItem(
                        item.getItemId(),
                        item.getName(),
                        item.getCategory(),
                        calculateItemSimilarityScore(itemCluster, userCluster),
                        item.getDescription()
                    ));
                }
            }
            
        } catch (Exception e) {
            // Fallback to random recommendations if clustering fails
            return getFallbackRecommendations(limit, category);
        }
        
        return recommendations;
    }
    
    @Override
    public List<RecommendationItem> getSimilarItems(String itemId, int limit, boolean includeMetadata) {
        List<RecommendationItem> similarItems = new ArrayList<>();
        
        try {
            // Get the original item cluster
            ItemCluster originalItemCluster = itemClusterRepository.findByItemId(itemId);
            if (originalItemCluster == null) {
                // If item not in cluster, use fallback random recommendations
                return getFallbackSimilarItems(itemId, limit, includeMetadata);
            }
            
            // Get other items from the same cluster
            List<ItemCluster> clusterItems = itemClusterRepository.findOtherItemsInSameCluster(itemId);
            
            // Sort by similarity score and take top items
            List<ItemCluster> sortedItems = clusterItems.stream()
                .sorted((a, b) -> Double.compare(
                    calculateItemToItemSimilarity(a, originalItemCluster),
                    calculateItemToItemSimilarity(b, originalItemCluster)
                ))
                .limit(limit)
                .collect(Collectors.toList());
            
            // Convert to RecommendationItem DTOs
            for (ItemCluster itemCluster : sortedItems) {
                Item item = itemRepository.findByItemId(itemCluster.getItemId());
                if (item != null) {
                    String description = includeMetadata ? 
                        item.getDescription() + " | Price: $" + (item.getPrice() != null ? item.getPrice() : "N/A") +
                        " | Rating: " + (itemCluster.getRating() != null ? String.format("%.1f", itemCluster.getRating()) : "N/A") +
                        " | Views: " + (itemCluster.getViews() != null ? itemCluster.getViews() : "N/A") :
                        "Basic description";
                    
                    similarItems.add(new RecommendationItem(
                        item.getItemId(),
                        item.getName(),
                        item.getCategory(),
                        calculateItemToItemSimilarity(itemCluster, originalItemCluster),
                        description
                    ));
                }
            }
            
        } catch (Exception e) {
            // Fallback to random recommendations if clustering fails
            return getFallbackSimilarItems(itemId, limit, includeMetadata);
        }
        
        return similarItems;
    }
    
    // Helper methods for k-means based recommendations
    
    private List<RecommendationItem> getFallbackRecommendations(int limit, String category) {
        List<Item> items;
        if (category != null && !category.isEmpty()) {
            items = itemRepository.findRandomItemsByCategory(category, Math.min(limit, 10));
        } else {
            items = itemRepository.findRandomItems(Math.min(limit, 10));
        }
        
        List<RecommendationItem> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(items.size(), limit); i++) {
            Item item = items.get(i);
            recommendations.add(new RecommendationItem(
                item.getItemId(),
                item.getName(),
                item.getCategory(),
                Math.max(0.1, 1.0 - (i * 0.1)),
                item.getDescription()
            ));
        }
        return recommendations;
    }
    
    private List<RecommendationItem> getFallbackSimilarItems(String itemId, int limit, boolean includeMetadata) {
        Item originalItem = itemRepository.findByItemId(itemId);
        List<RecommendationItem> similarItems = new ArrayList<>();
        
        if (originalItem != null) {
            List<Item> items = itemRepository.findRandomItemsByCategory(originalItem.getCategory(), Math.min(limit + 1, 10));
            for (Item item : items) {
                if (!item.getItemId().equals(itemId) && similarItems.size() < limit) {
                    String description = includeMetadata ? 
                        item.getDescription() + " | Price: $" + (item.getPrice() != null ? item.getPrice() : "N/A") :
                        "Basic description";
                    
                    similarItems.add(new RecommendationItem(
                        item.getItemId(),
                        item.getName(),
                        item.getCategory(),
                        calculateSimpleSimilarityScore(item, originalItem),
                        description
                    ));
                }
            }
        }
        return similarItems;
    }
    
    private Double calculateItemSimilarityScore(ItemCluster itemCluster, UserCluster userCluster) {
        try {
            double[] itemVector = parseFeatureVector(itemCluster.getFeatureVector());
            double[] userVector = parseFeatureVector(userCluster.getFeatureVector());
            
            if (itemVector.length > 0 && userVector.length > 0) {
                return clusteringService.calculateSimilarity(itemVector, userVector);
            }
        } catch (Exception e) {
            // Fallback to simple scoring
        }
        
        // Fallback: simple scoring based on category match
        double baseScore = 0.5;
        if (userCluster.getRegion() != null && itemCluster.getCategoryEncoded() != null) {
            // Simple heuristic: users in region 1 prefer electronics, region 2 clothing, etc.
            if (userCluster.getRegion().equals(itemCluster.getCategoryEncoded())) {
                baseScore += 0.3;
            }
        }
        
        return Math.min(1.0, baseScore + new Random().nextDouble() * 0.2);
    }
    
    private Double calculateItemToItemSimilarity(ItemCluster item1, ItemCluster item2) {
        try {
            double[] vector1 = parseFeatureVector(item1.getFeatureVector());
            double[] vector2 = parseFeatureVector(item2.getFeatureVector());
            
            if (vector1.length > 0 && vector2.length > 0) {
                return clusteringService.calculateSimilarity(vector1, vector2);
            }
        } catch (Exception e) {
            // Fallback to simple scoring
        }
        
        // Fallback: simple scoring based on category and rating
        double baseScore = 0.5;
        
        if (item1.getCategoryEncoded() != null && item2.getCategoryEncoded() != null &&
            item1.getCategoryEncoded().equals(item2.getCategoryEncoded())) {
            baseScore += 0.3;
        }
        
        if (item1.getRating() != null && item2.getRating() != null) {
            double ratingDiff = Math.abs(item1.getRating() - item2.getRating());
            baseScore += (1.0 - ratingDiff / 5.0) * 0.2;
        }
        
        return Math.min(1.0, baseScore);
    }
    
    private Double calculateSimpleSimilarityScore(Item item, Item originalItem) {
        if (originalItem == null) {
            return new Random().nextDouble() * 0.5 + 0.3;
        }
        
        double baseScore = 0.5;
        if (item.getCategory().equals(originalItem.getCategory())) {
            baseScore += 0.3;
        }
        
        baseScore += new Random().nextDouble() * 0.2;
        return Math.min(1.0, baseScore);
    }
    
    private double[] parseFeatureVector(String featureVectorJson) {
        try {
            if (featureVectorJson == null || featureVectorJson.trim().isEmpty()) {
                return new double[0];
            }
            
            List<Double> vectorList = objectMapper.readValue(featureVectorJson, new TypeReference<List<Double>>() {});
            return vectorList.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            return new double[0];
        }
    }
}
