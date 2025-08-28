package helloworld.Model;

import java.util.List;

public class ItemSimilarityResponse {
    private String itemId;
    private List<RecommendationItem> similarItems;
    private int totalCount;
    
    // Constructor
    public ItemSimilarityResponse(String itemId, List<RecommendationItem> similarItems, int totalCount) {
        this.itemId = itemId;
        this.similarItems = similarItems;
        this.totalCount = totalCount;
    }
    
    // Default constructor
    public ItemSimilarityResponse() {}
    
    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    
    public List<RecommendationItem> getSimilarItems() { return similarItems; }
    public void setSimilarItems(List<RecommendationItem> similarItems) { this.similarItems = similarItems; }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
}