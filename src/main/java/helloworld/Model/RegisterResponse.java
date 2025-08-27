package helloworld.Model;

public class RegisterResponse {
    
    private String message;
    private boolean success;
    private String username;
    private String email;
    
    // Default constructor
    public RegisterResponse() {}
    
    // Success response constructor
    public RegisterResponse(String message, boolean success, String username, String email) {
        this.message = message;
        this.success = success;
        this.username = username;
        this.email = email;
    }
    
    // Error response constructor
    public RegisterResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
