package helloworld.service;

import helloworld.Model.ItemCluster;
import helloworld.Model.UserCluster;
import java.util.List;
import java.util.Map;

public interface ClusteringService {
    
    /**
     * Perform k-means clustering on items
     * @param items List of items to cluster
     * @param k Number of clusters
     * @return Map of itemId to clusterId
     */
    Map<String, Integer> clusterItems(List<ItemCluster> items, int k);
    
    /**
     * Perform k-means clustering on users
     * @param users List of users to cluster
     * @param k Number of clusters
     * @return Map of userId to clusterId
     */
    Map<String, Integer> clusterUsers(List<UserCluster> users, int k);
    
    /**
     * Find the cluster for a new item based on its features
     * @param itemFeatures Feature vector of the item
     * @param existingClusters List of existing item clusters
     * @return Predicted cluster ID
     */
    Integer predictItemCluster(double[] itemFeatures, List<ItemCluster> existingClusters);
    
    /**
     * Find the cluster for a new user based on their features
     * @param userFeatures Feature vector of the user
     * @param existingClusters List of existing user clusters
     * @return Predicted cluster ID
     */
    Integer predictUserCluster(double[] userFeatures, List<UserCluster> existingClusters);
    
    /**
     * Calculate similarity between two feature vectors
     * @param vector1 First feature vector
     * @param vector2 Second feature vector
     * @return Similarity score (0-1)
     */
    double calculateSimilarity(double[] vector1, double[] vector2);
    
    /**
     * Retrain clusters with new data
     * @param items Updated list of items
     * @param users Updated list of users
     * @param k Number of clusters
     */
    void retrainClusters(List<ItemCluster> items, List<UserCluster> users, int k);
}
