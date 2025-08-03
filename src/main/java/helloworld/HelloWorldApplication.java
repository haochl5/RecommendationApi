package helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Hello World Spring Boot Application...");
        SpringApplication.run(HelloWorldApplication.class, args);
        System.out.println("✅ Application started successfully!");
        System.out.println("🌐 Visit: http://localhost:8080/hello");
    }
}