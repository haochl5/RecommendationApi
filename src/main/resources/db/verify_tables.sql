-- Verification script to check if tables are created properly
-- Run this after setting up your database

USE recommendation_db;

-- Check if all required tables exist
SELECT 
    'Tables Check' as verification_type,
    CASE 
        WHEN COUNT(*) = 4 THEN 'PASS - All 4 tables exist'
        ELSE CONCAT('FAIL - Only ', COUNT(*), ' tables found (expected 4)')
    END as result
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'recommendation_db' 
AND TABLE_NAME IN ('users', 'items', 'user_clusters', 'item_clusters');

-- Show table details
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME,
    UPDATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'recommendation_db'
ORDER BY TABLE_NAME;

-- Check data counts
SELECT 'Data Count Check' as verification_type, 'Results' as result
UNION ALL
SELECT 'Users', CAST(COUNT(*) as CHAR) FROM users
UNION ALL
SELECT 'Items', CAST(COUNT(*) as CHAR) FROM items
UNION ALL
SELECT 'User Clusters', CAST(COUNT(*) as CHAR) FROM user_clusters
UNION ALL
SELECT 'Item Clusters', CAST(COUNT(*) as CHAR) FROM item_clusters;

-- Check sample data
SELECT 'Sample Data Check' as verification_type, 'Results' as result
UNION ALL
SELECT 'Users Sample', CONCAT('Found user: ', username) FROM users LIMIT 3
UNION ALL
SELECT 'Items Sample', CONCAT('Found item: ', name) FROM items LIMIT 3
UNION ALL
SELECT 'User Clusters Sample', CONCAT('User ', user_id, ' in cluster ', cluster_id) FROM user_clusters LIMIT 3
UNION ALL
SELECT 'Item Clusters Sample', CONCAT('Item ', item_id, ' in cluster ', cluster_id) FROM item_clusters LIMIT 3;

-- Check JSON columns are working
SELECT 'JSON Data Check' as verification_type, 'Results' as result
UNION ALL
SELECT 'User Clusters JSON', 
    CASE 
        WHEN JSON_VALID(purchased_items) THEN 'PASS - JSON is valid'
        ELSE 'FAIL - Invalid JSON'
    END
FROM user_clusters LIMIT 1
UNION ALL
SELECT 'Item Clusters JSON', 
    CASE 
        WHEN JSON_VALID(feature_vector) THEN 'PASS - JSON is valid'
        ELSE 'FAIL - Invalid JSON'
    END
FROM item_clusters LIMIT 1;
