package helloworld.controller;

import helloworld.Model.LoginRequest;
import helloworld.Model.LoginResponse;
import helloworld.Model.LogoutResponse;
import helloworld.Utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            long expiresIn = jwtUtil.getExpiration();
            
            LoginResponse response = new LoginResponse(token, expiresIn);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        try {
            // Get the token from the Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // In a real application, you might want to add the token to a blacklist
                // or implement a more sophisticated logout mechanism
                
                // Clear the security context
                SecurityContextHolder.clearContext();
                
                LogoutResponse response = new LogoutResponse("Successfully logged out", true);
                return ResponseEntity.ok(response);
            } else {
                LogoutResponse response = new LogoutResponse("No valid token provided", false);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            LogoutResponse response = new LogoutResponse("Error during logout", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                if (username != null && jwtUtil.validateToken(token, username)) {
                    return ResponseEntity.ok("Token is valid for user: " + username);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
                }
            } else {
                return ResponseEntity.badRequest().body("No token provided");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
