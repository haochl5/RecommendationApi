package helloworld.controller;

import helloworld.Model.RecommendationItem;
import helloworld.Model.UserRecommendationResponse;
import helloworld.Model.ItemSimilarityResponse;
import helloworld.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
  @Autowired
    private RecommendationService recommendationService;
    
    @GetMapping("/user/{userId}")
    public UserRecommendationResponse getUserRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String category) {
        
        List<RecommendationItem> recommendations = 
            recommendationService.getUserRecommendations(userId, limit, category);
        
        return new UserRecommendationResponse(userId, recommendations, recommendations.size());
    }
    
    @GetMapping("/item/{itemId}")
    public ItemSimilarityResponse getSimilarItems(
            @PathVariable String itemId,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "false") boolean include_metadata) {
        
        List<RecommendationItem> similarItems = 
            recommendationService.getSimilarItems(itemId, limit, include_metadata);
        
        return new ItemSimilarityResponse(itemId, similarItems, similarItems.size());
    }
}
