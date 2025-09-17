package helloworld.repository;

import helloworld.Model.ItemCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemClusterRepository extends JpaRepository<ItemCluster, Long> {
    
    // Find item cluster by itemId
    ItemCluster findByItemId(String itemId);
    
    // Find all items in a specific cluster
    List<ItemCluster> findByClusterId(Integer clusterId);
    
    // Find items in the same cluster as a given item
    @Query("SELECT ic FROM ItemCluster ic WHERE ic.clusterId = (SELECT ic2.clusterId FROM ItemCluster ic2 WHERE ic2.itemId = :itemId)")
    List<ItemCluster> findItemsInSameCluster(@Param("itemId") String itemId);
    
    // Find items in the same cluster excluding the given item
    @Query("SELECT ic FROM ItemCluster ic WHERE ic.clusterId = (SELECT ic2.clusterId FROM ItemCluster ic2 WHERE ic2.itemId = :itemId) AND ic.itemId != :itemId")
    List<ItemCluster> findOtherItemsInSameCluster(@Param("itemId") String itemId);
    
    // Find items by category within a cluster
    @Query("SELECT ic FROM ItemCluster ic WHERE ic.clusterId = :clusterId AND ic.categoryEncoded = :categoryEncoded")
    List<ItemCluster> findByClusterIdAndCategoryEncoded(@Param("clusterId") Integer clusterId, @Param("categoryEncoded") Integer categoryEncoded);
    
    // Find items by rating range within a cluster
    @Query("SELECT ic FROM ItemCluster ic WHERE ic.clusterId = :clusterId AND ic.rating BETWEEN :minRating AND :maxRating")
    List<ItemCluster> findByClusterIdAndRatingBetween(@Param("clusterId") Integer clusterId, @Param("minRating") Double minRating, @Param("maxRating") Double maxRating);
    
    // Check if item exists in cluster table
    boolean existsByItemId(String itemId);
    
    // Get all distinct cluster IDs
    @Query("SELECT DISTINCT ic.clusterId FROM ItemCluster ic ORDER BY ic.clusterId")
    List<Integer> findDistinctClusterIds();
}
