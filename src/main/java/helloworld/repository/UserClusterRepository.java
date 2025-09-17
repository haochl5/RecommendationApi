package helloworld.repository;

import helloworld.Model.UserCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserClusterRepository extends JpaRepository<UserCluster, Long> {
    
    // Find user cluster by userId
    UserCluster findByUserId(String userId);
    
    // Find all users in a specific cluster
    List<UserCluster> findByClusterId(Integer clusterId);
    
    // Find users in the same cluster as a given user
    @Query("SELECT uc FROM UserCluster uc WHERE uc.clusterId = (SELECT uc2.clusterId FROM UserCluster uc2 WHERE uc2.userId = :userId)")
    List<UserCluster> findUsersInSameCluster(@Param("userId") String userId);
    
    // Find users in the same cluster excluding the given user
    @Query("SELECT uc FROM UserCluster uc WHERE uc.clusterId = (SELECT uc2.clusterId FROM UserCluster uc2 WHERE uc2.userId = :userId) AND uc.userId != :userId")
    List<UserCluster> findOtherUsersInSameCluster(@Param("userId") String userId);
    
    // Check if user exists in cluster table
    boolean existsByUserId(String userId);
}
