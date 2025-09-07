package helloworld.repository;

import helloworld.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // Find items by category
    List<Item> findByCategory(String category);
    
    // Find items by category with limit
    @Query("SELECT i FROM Item i WHERE i.category = :category ORDER BY i.createdAt DESC")
    List<Item> findTopByCategoryOrderByCreatedAtDesc(@Param("category") String category, org.springframework.data.domain.Pageable pageable);
    
    // Find items by name containing (for search)
    List<Item> findByNameContainingIgnoreCase(String name);
    
    // Find random items (for recommendations)
    @Query(value = "SELECT * FROM items ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Item> findRandomItems(@Param("limit") int limit);
    
    // Find random items by category
    @Query(value = "SELECT * FROM items WHERE category = :category ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Item> findRandomItemsByCategory(@Param("category") String category, @Param("limit") int limit);
    
    // Find items by itemId
    Item findByItemId(String itemId);
}