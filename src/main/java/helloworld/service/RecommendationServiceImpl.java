package helloworld.service;

import helloworld.Model.RecommendationItem;
import helloworld.Model.Item;
import helloworld.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Override
    public List<RecommendationItem> getUserRecommendations(String userId, int limit, String category) {
        List<RecommendationItem> recommendations = new ArrayList<>();
        List<Item> items;
        
        if (category != null && !category.isEmpty()) {
            // Get random items from the specified category
            items = itemRepository.findRandomItemsByCategory(category, Math.min(limit, 10));
        } else {
            // Get random items from all categories
            items = itemRepository.findRandomItems(Math.min(limit, 10));
        }
        
        // Convert Item entities to RecommendationItem DTOs
        for (int i = 0; i < Math.min(items.size(), limit); i++) {
            Item item = items.get(i);
            recommendations.add(new RecommendationItem(
                item.getItemId(),
                item.getName(),
                item.getCategory(),
                calculateRecommendationScore(i), // Simple scoring based on position
                item.getDescription()
            ));
        }
        
        return recommendations;
    }
    
    @Override
    public List<RecommendationItem> getSimilarItems(String itemId, int limit, boolean includeMetadata) {
        List<RecommendationItem> similarItems = new ArrayList<>();
        
        // Find the original item to get its category
        Item originalItem = itemRepository.findByItemId(itemId);
        
        if (originalItem != null) {
            // Get random items from the same category (excluding the original item)
            List<Item> items = itemRepository.findRandomItemsByCategory(originalItem.getCategory(), Math.min(limit + 1, 10));
            
            // Filter out the original item and convert to DTOs
            for (Item item : items) {
                if (!item.getItemId().equals(itemId) && similarItems.size() < limit) {
                    String description = includeMetadata ? 
                        item.getDescription() + " | Price: $" + (item.getPrice() != null ? item.getPrice() : "N/A") :
                        "Basic description";
                    
                    similarItems.add(new RecommendationItem(
                        item.getItemId(),
                        item.getName(),
                        item.getCategory(),
                        calculateSimilarityScore(item, originalItem),
                        description
                    ));
                }
            }
        } else {
            // If original item not found, return random items
            List<Item> items = itemRepository.findRandomItems(Math.min(limit, 10));
            for (int i = 0; i < Math.min(items.size(), limit); i++) {
                Item item = items.get(i);
                String description = includeMetadata ? 
                    item.getDescription() + " | Price: $" + (item.getPrice() != null ? item.getPrice() : "N/A") :
                    "Basic description";
                
                similarItems.add(new RecommendationItem(
                    item.getItemId(),
                    item.getName(),
                    item.getCategory(),
                    calculateSimilarityScore(item, null),
                    description
                ));
            }
        }
        
        return similarItems;
    }
    
    private Double calculateRecommendationScore(int position) {
        // Simple scoring: higher score for items earlier in the list
        return Math.max(0.1, 1.0 - (position * 0.1));
    }
    
    private Double calculateSimilarityScore(Item item, Item originalItem) {
        if (originalItem == null) {
            return new Random().nextDouble() * 0.5 + 0.3; // Random score between 0.3 and 0.8
        }
        
        // Simple similarity scoring based on category match
        double baseScore = 0.5;
        if (item.getCategory().equals(originalItem.getCategory())) {
            baseScore += 0.3;
        }
        
        // Add some randomness
        baseScore += new Random().nextDouble() * 0.2;
        
        return Math.min(1.0, baseScore);
    }
}
