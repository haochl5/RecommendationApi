package helloworld.config;

import helloworld.Model.Item;
import helloworld.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no items exist
        if (itemRepository.count() == 0) {
            initializeSampleData();
        }
    }
    
    private void initializeSampleData() {
        // Electronics items
        itemRepository.save(new Item("laptop-001", "MacBook Pro 16-inch", "electronics", 
            "High-performance laptop with M2 chip, perfect for professionals", 2499.99, 
            "https://example.com/macbook.jpg"));
        
        itemRepository.save(new Item("laptop-002", "Dell XPS 13", "electronics", 
            "Ultrabook with stunning display and long battery life", 1299.99, 
            "https://example.com/dell-xps.jpg"));
        
        itemRepository.save(new Item("phone-001", "iPhone 15 Pro", "electronics", 
            "Latest iPhone with titanium design and advanced camera system", 999.99, 
            "https://example.com/iphone15.jpg"));
        
        itemRepository.save(new Item("phone-002", "Samsung Galaxy S24", "electronics", 
            "Android flagship with AI-powered features and excellent camera", 899.99, 
            "https://example.com/galaxy-s24.jpg"));
        
        itemRepository.save(new Item("headphones-001", "Sony WH-1000XM5", "electronics", 
            "Premium noise-canceling headphones with exceptional sound quality", 399.99, 
            "https://example.com/sony-headphones.jpg"));
        
        // Books
        itemRepository.save(new Item("book-001", "Clean Code", "books", 
            "A Handbook of Agile Software Craftsmanship by Robert C. Martin", 29.99, 
            "https://example.com/clean-code.jpg"));
        
        itemRepository.save(new Item("book-002", "Design Patterns", "books", 
            "Elements of Reusable Object-Oriented Software by Gang of Four", 39.99, 
            "https://example.com/design-patterns.jpg"));
        
        itemRepository.save(new Item("book-003", "The Pragmatic Programmer", "books", 
            "Your Journey to Mastery by David Thomas and Andrew Hunt", 34.99, 
            "https://example.com/pragmatic-programmer.jpg"));
        
        // Clothing
        itemRepository.save(new Item("shirt-001", "Classic Cotton T-Shirt", "clothing", 
            "Comfortable 100% cotton t-shirt in various colors", 19.99, 
            "https://example.com/cotton-tshirt.jpg"));
        
        itemRepository.save(new Item("jeans-001", "Slim Fit Jeans", "clothing", 
            "Premium denim jeans with modern slim fit design", 79.99, 
            "https://example.com/slim-jeans.jpg"));
        
        itemRepository.save(new Item("jacket-001", "Leather Jacket", "clothing", 
            "Genuine leather jacket with classic biker style", 199.99, 
            "https://example.com/leather-jacket.jpg"));
        
        // Home & Garden
        itemRepository.save(new Item("lamp-001", "Modern Table Lamp", "home", 
            "Sleek LED table lamp with adjustable brightness", 89.99, 
            "https://example.com/table-lamp.jpg"));
        
        itemRepository.save(new Item("chair-001", "Ergonomic Office Chair", "home", 
            "Comfortable office chair with lumbar support and adjustable height", 299.99, 
            "https://example.com/office-chair.jpg"));
        
        itemRepository.save(new Item("plant-001", "Fiddle Leaf Fig Plant", "home", 
            "Large indoor plant perfect for home decoration", 49.99, 
            "https://example.com/fiddle-leaf.jpg"));
        
        System.out.println("Sample data initialized successfully!");
    }
}