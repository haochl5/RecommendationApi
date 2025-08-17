package helloworld.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "This is a protected endpoint. You are authenticated!";
    }
    
    @GetMapping("/user-info")
    public String userInfo() {
        return "User information endpoint - requires authentication";
    }
}
