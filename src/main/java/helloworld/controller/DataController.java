package helloworld.controller;

import helloworld.service.DataGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {
    
    @Autowired
    private DataGenerationService dataGenerationService;
    
    @PostMapping("/generate-clusters")
    public ResponseEntity<Map<String, String>> generateClusterData() {
        try {
            dataGenerationService.generateClusterData();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cluster data generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to generate cluster data: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ready");
        response.put("message", "Data generation service is available");
        
        return ResponseEntity.ok(response);
    }
}
