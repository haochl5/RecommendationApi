package helloworld.service;

import helloworld.Model.ItemCluster;
import helloworld.Model.UserCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClusteringServiceImpl implements ClusteringService {
    
    @Autowired
    private FeatureExtractionService featureExtractionService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int MAX_ITERATIONS = 100;
    private final int RANDOM_SEED = 42;
    
    @Override
    public Map<String, Integer> clusterItems(List<ItemCluster> items, int k) {
        if (items.isEmpty() || k <= 0) {
            return new HashMap<>();
        }
        
        // Extract feature vectors
        List<DoublePoint> points = new ArrayList<>();
        Map<String, double[]> itemVectors = new HashMap<>();
        
        for (ItemCluster item : items) {
            try {
                double[] vector = parseFeatureVector(item.getFeatureVector());
                if (vector != null && vector.length > 0) {
                    points.add(new DoublePoint(vector));
                    itemVectors.put(item.getItemId(), vector);
                }
            } catch (Exception e) {
                // Skip items with invalid feature vectors
                continue;
            }
        }
        
        if (points.isEmpty()) {
            return new HashMap<>();
        }
        
        // Perform k-means clustering
        RandomGenerator randomGenerator = new Well19937c(RANDOM_SEED);
        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(
            Math.min(k, points.size()), MAX_ITERATIONS, new EuclideanDistance(), randomGenerator
        );
        
        List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);
        
        // Map items to clusters
        Map<String, Integer> itemToCluster = new HashMap<>();
        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<DoublePoint> cluster = clusters.get(i);
            for (DoublePoint point : cluster.getPoints()) {
                // Find the item that corresponds to this point
                for (Map.Entry<String, double[]> entry : itemVectors.entrySet()) {
                    if (Arrays.equals(entry.getValue(), point.getPoint())) {
                        itemToCluster.put(entry.getKey(), i);
                        break;
                    }
                }
            }
        }
        
        return itemToCluster;
    }
    
    @Override
    public Map<String, Integer> clusterUsers(List<UserCluster> users, int k) {
        if (users.isEmpty() || k <= 0) {
            return new HashMap<>();
        }
        
        // Extract feature vectors
        List<DoublePoint> points = new ArrayList<>();
        Map<String, double[]> userVectors = new HashMap<>();
        
        for (UserCluster user : users) {
            try {
                double[] vector = parseFeatureVector(user.getFeatureVector());
                if (vector != null && vector.length > 0) {
                    points.add(new DoublePoint(vector));
                    userVectors.put(user.getUserId(), vector);
                }
            } catch (Exception e) {
                // Skip users with invalid feature vectors
                continue;
            }
        }
        
        if (points.isEmpty()) {
            return new HashMap<>();
        }
        
        // Perform k-means clustering
        RandomGenerator randomGenerator = new Well19937c(RANDOM_SEED);
        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(
            Math.min(k, points.size()), MAX_ITERATIONS, new EuclideanDistance(), randomGenerator
        );
        
        List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);
        
        // Map users to clusters
        Map<String, Integer> userToCluster = new HashMap<>();
        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<DoublePoint> cluster = clusters.get(i);
            for (DoublePoint point : cluster.getPoints()) {
                // Find the user that corresponds to this point
                for (Map.Entry<String, double[]> entry : userVectors.entrySet()) {
                    if (Arrays.equals(entry.getValue(), point.getPoint())) {
                        userToCluster.put(entry.getKey(), i);
                        break;
                    }
                }
            }
        }
        
        return userToCluster;
    }
    
    @Override
    public Integer predictItemCluster(double[] itemFeatures, List<ItemCluster> existingClusters) {
        if (existingClusters.isEmpty() || itemFeatures == null || itemFeatures.length == 0) {
            return 0; // Default cluster
        }
        
        // Calculate centroids for each existing cluster
        Map<Integer, double[]> centroids = calculateItemCentroids(existingClusters);
        
        // Find the closest centroid
        double minDistance = Double.MAX_VALUE;
        Integer bestCluster = 0;
        
        for (Map.Entry<Integer, double[]> entry : centroids.entrySet()) {
            double distance = calculateEuclideanDistance(itemFeatures, entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                bestCluster = entry.getKey();
            }
        }
        
        return bestCluster;
    }
    
    @Override
    public Integer predictUserCluster(double[] userFeatures, List<UserCluster> existingClusters) {
        if (existingClusters.isEmpty() || userFeatures == null || userFeatures.length == 0) {
            return 0; // Default cluster
        }
        
        // Calculate centroids for each existing cluster
        Map<Integer, double[]> centroids = calculateUserCentroids(existingClusters);
        
        // Find the closest centroid
        double minDistance = Double.MAX_VALUE;
        Integer bestCluster = 0;
        
        for (Map.Entry<Integer, double[]> entry : centroids.entrySet()) {
            double distance = calculateEuclideanDistance(userFeatures, entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                bestCluster = entry.getKey();
            }
        }
        
        return bestCluster;
    }
    
    @Override
    public double calculateSimilarity(double[] vector1, double[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length != vector2.length) {
            return 0.0;
        }
        
        // Calculate cosine similarity
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    @Override
    public void retrainClusters(List<ItemCluster> items, List<UserCluster> users, int k) {
        // This method would be called periodically to retrain clusters
        // For now, we'll just log that retraining is needed
        System.out.println("Retraining clusters with " + items.size() + " items and " + users.size() + " users");
        
        // In a real implementation, this would:
        // 1. Clear existing cluster assignments
        // 2. Re-run clustering on all data
        // 3. Update cluster assignments in database
        // 4. Update cluster centroids
    }
    
    // Helper methods
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
    
    private Map<Integer, double[]> calculateItemCentroids(List<ItemCluster> clusters) {
        Map<Integer, List<double[]>> clusterVectors = new HashMap<>();
        
        // Group vectors by cluster
        for (ItemCluster cluster : clusters) {
            try {
                double[] vector = parseFeatureVector(cluster.getFeatureVector());
                if (vector.length > 0) {
                    clusterVectors.computeIfAbsent(cluster.getClusterId(), k -> new ArrayList<>()).add(vector);
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        // Calculate centroids
        Map<Integer, double[]> centroids = new HashMap<>();
        for (Map.Entry<Integer, List<double[]>> entry : clusterVectors.entrySet()) {
            centroids.put(entry.getKey(), calculateCentroid(entry.getValue()));
        }
        
        return centroids;
    }
    
    private Map<Integer, double[]> calculateUserCentroids(List<UserCluster> clusters) {
        Map<Integer, List<double[]>> clusterVectors = new HashMap<>();
        
        // Group vectors by cluster
        for (UserCluster cluster : clusters) {
            try {
                double[] vector = parseFeatureVector(cluster.getFeatureVector());
                if (vector.length > 0) {
                    clusterVectors.computeIfAbsent(cluster.getClusterId(), k -> new ArrayList<>()).add(vector);
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        // Calculate centroids
        Map<Integer, double[]> centroids = new HashMap<>();
        for (Map.Entry<Integer, List<double[]>> entry : clusterVectors.entrySet()) {
            centroids.put(entry.getKey(), calculateCentroid(entry.getValue()));
        }
        
        return centroids;
    }
    
    private double[] calculateCentroid(List<double[]> vectors) {
        if (vectors.isEmpty()) {
            return new double[0];
        }
        
        int dimensions = vectors.get(0).length;
        double[] centroid = new double[dimensions];
        
        for (double[] vector : vectors) {
            for (int i = 0; i < dimensions; i++) {
                centroid[i] += vector[i];
            }
        }
        
        for (int i = 0; i < dimensions; i++) {
            centroid[i] /= vectors.size();
        }
        
        return centroid;
    }
    
    private double calculateEuclideanDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            return Double.MAX_VALUE;
        }
        
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
}
