package helloworld.service;

import helloworld.Model.RecommendationItem;
import java.util.List;

public interface RecommendationService {
    List<RecommendationItem> getUserRecommendations(String userId, int limit, String category);
    List<RecommendationItem> getSimilarItems(String itemId, int limit, boolean includeMetadata);
}