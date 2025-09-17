package helloworld.controller;

import helloworld.service.DataGenerationService;
import helloworld.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestRecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private DataGenerationService dataGenerationService;
    
    @GetMapping("/recommendations/user/{userId}")
    public ResponseEntity<Map<String, Object>> testUserRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String category) {
        
        try {
            var recommendations = recommendationService.getUserRecommendations(userId, limit, category);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("userId", userId);
            response.put("limit", limit);
            response.put("category", category);
            response.put("recommendations", recommendations);
            response.put("count", recommendations.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to get recommendations: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/recommendations/item/{itemId}")
    public ResponseEntity<Map<String, Object>> testItemRecommendations(
            @PathVariable String itemId,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "true") boolean includeMetadata) {
        
        try {
            var recommendations = recommendationService.getSimilarItems(itemId, limit, includeMetadata);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("itemId", itemId);
            response.put("limit", limit);
            response.put("includeMetadata", includeMetadata);
            response.put("recommendations", recommendations);
            response.put("count", recommendations.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to get similar items: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/setup")
    public ResponseEntity<Map<String, String>> setupTestData() {
        try {
            // Generate cluster data
            dataGenerationService.generateClusterData();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Test data setup completed. You can now test recommendations.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to setup test data: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
