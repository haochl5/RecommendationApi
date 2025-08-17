package helloworld.Model;

public class LoginResponse {
    
    private String token;
    private long expiresIn;
    private String message;
    
    // Default constructor
    public LoginResponse() {}
    
    // Constructor with fields
    public LoginResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.message = "Login successful";
    }
    
    // Constructor with message
    public LoginResponse(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
