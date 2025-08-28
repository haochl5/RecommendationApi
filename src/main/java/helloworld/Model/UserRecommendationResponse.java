package helloworld.Model;

import java.util.List;

public class UserRecommendationResponse {
    private String userId;
    private List<RecommendationItem> recommendations;
    private int totalCount;
    
    // Constructor
    public UserRecommendationResponse(String userId, List<RecommendationItem> recommendations, int totalCount) {
        this.userId = userId;
        this.recommendations = recommendations;
        this.totalCount = totalCount;
    }
    
    // Default constructor
    public UserRecommendationResponse() {}
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public List<RecommendationItem> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationItem> recommendations) { this.recommendations = recommendations; }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
}