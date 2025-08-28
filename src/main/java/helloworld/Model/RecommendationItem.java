package helloworld.Model;

public class RecommendationItem {
    private String itemId;
    private String name;
    private String category;
    private Double score;
    private String description;
    
    // Constructor
    public RecommendationItem(String itemId, String name, String category, Double score, String description) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.score = score;
        this.description = description;
    }
    
    // Default constructor
    public RecommendationItem() {}
    
    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}