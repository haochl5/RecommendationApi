# K-Means Recommendation System

This document describes the implementation of a k-means clustering-based recommendation system that replaces the previous random recommendation logic.

## Overview

The system uses k-means clustering to group users and items based on their features, then provides recommendations by finding similar users/items within the same clusters.

## Architecture

### Core Components

1. **Feature Extraction Service** (`FeatureExtractionService`)
   - Extracts features from items and users
   - Normalizes features to 0-1 range
   - Converts features to vectors for clustering

2. **Clustering Service** (`ClusteringService`)
   - Performs k-means clustering using Apache Commons Math
   - Predicts cluster assignments for new users/items
   - Calculates similarity between feature vectors

3. **Updated Recommendation Service** (`RecommendationServiceImpl`)
   - Uses cluster-based logic for recommendations
   - Falls back to random recommendations if clustering fails
   - Provides both user-based and item-based recommendations

### Database Tables

#### User Clusters Table (`user_clusters`)
- `user_id`: Unique user identifier
- `cluster_id`: Assigned cluster ID
- `purchased_items`: JSON array of purchased item IDs
- `total_order_amount`: Total amount spent by user
- `region`: User's region (1,2,3,4)
- `purchase_type_vector`: JSON array of purchase type probabilities
- `feature_vector`: JSON array of normalized features

#### Item Clusters Table (`item_clusters`)
- `item_id`: Unique item identifier
- `cluster_id`: Assigned cluster ID
- `description_vector`: JSON bag-of-words vector
- `category_encoded`: Numeric category (1,2,3,4)
- `price_normalized`: Normalized price value
- `rating`: Average rating from reviews
- `views`: Number of item views
- `feature_vector`: JSON array of normalized features

## Feature Extraction

### Item Features
- **Description Analysis**: Bag-of-words, word count, average word length
- **Category**: Encoded as 1,2,3,4 (electronics, clothing, books, home)
- **Price**: Normalized price value
- **Rating**: Simulated rating based on item characteristics
- **Views**: Simulated view count
- **Popularity**: Combined score from rating and views

### User Features
- **Account Age**: Days since account creation
- **Activity Status**: Active/inactive status
- **Purchase Behavior**: Count, total amount, average order value
- **Region**: Geographic region (1,2,3,4)
- **Purchase Frequency**: Orders per month
- **Account Activity**: Based on last login

## API Endpoints

### Recommendation Endpoints

#### Get User Recommendations
```
GET /recommendation/user/{userId}?limit=5&category=electronics
```
- Returns items recommended for a specific user
- Uses user's cluster to find similar items
- Can filter by category

#### Get Similar Items
```
GET /recommendation/item/{itemId}?limit=5&includeMetadata=true
```
- Returns items similar to the given item
- Uses item's cluster to find similar items
- Can include additional metadata (price, rating, views)

### Data Management Endpoints

#### Generate Cluster Data
```
POST /api/data/generate-clusters
```
- Generates fake data for cluster tables
- Performs k-means clustering on items and users
- Updates cluster assignments

#### Test Endpoints
```
POST /api/test/setup
GET /api/test/recommendations/user/{userId}
GET /api/test/recommendations/item/{itemId}
```

## Usage Instructions

### 1. Setup Database
Run the SQL script in `src/main/resources/db/init.sql` to create the required tables.

### 2. Generate Cluster Data
```bash
curl -X POST http://localhost:8080/api/data/generate-clusters
```

### 3. Test Recommendations
```bash
# Setup test data
curl -X POST http://localhost:8080/api/test/setup

# Get user recommendations
curl "http://localhost:8080/api/test/recommendations/user/user123?limit=5"

# Get similar items
curl "http://localhost:8080/api/test/recommendations/item/ITEM001?limit=5&includeMetadata=true"
```

## Configuration

### K-Means Parameters
- **Item Clusters**: 4 clusters
- **User Clusters**: 3 clusters
- **Max Iterations**: 100
- **Random Seed**: 42 (for reproducible results)

### Feature Normalization Ranges
- Description length: 0-1000 characters
- Word count: 0-100 words
- Price: $0-$1000
- Rating: 0-5 stars
- Views: 0-10000
- User age: 0-3650 days (10 years)

## Dependencies

The following dependencies were added to support k-means clustering:

```xml
<!-- K-Means Clustering -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>

<!-- Natural Language Processing -->
<dependency>
    <groupId>org.apache.opennlp</groupId>
    <artifactId>opennlp-tools</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Fallback Mechanism

The system includes robust fallback mechanisms:

1. **User Not in Cluster**: Falls back to random recommendations
2. **Item Not in Cluster**: Falls back to category-based random recommendations
3. **Clustering Failure**: Falls back to simple similarity scoring
4. **Feature Extraction Failure**: Uses default values

## Performance Considerations

- Feature vectors are cached in the database as JSON
- Clustering is performed offline during data generation
- Real-time recommendations use pre-computed clusters
- Fallback mechanisms ensure system reliability

## Future Enhancements

1. **Real-time Clustering**: Periodic retraining of clusters
2. **Advanced Features**: Sentiment analysis, collaborative filtering
3. **Scalability**: Distributed clustering for large datasets
4. **A/B Testing**: Compare different clustering algorithms
5. **User Feedback**: Incorporate user ratings into clustering

## Troubleshooting

### Common Issues

1. **No Recommendations**: Ensure cluster data is generated
2. **Empty Results**: Check if user/item exists in cluster tables
3. **Performance Issues**: Consider reducing feature vector dimensions
4. **Memory Issues**: Implement batch processing for large datasets

### Debug Endpoints

- `GET /api/data/status`: Check data generation service status
- `GET /api/test/recommendations/user/{userId}`: Test user recommendations
- `GET /api/test/recommendations/item/{itemId}`: Test item recommendations
