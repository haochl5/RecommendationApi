package helloworld.service;

import helloworld.Model.User;

public interface UserService {
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User save(User user);
    
    boolean authenticateUser(String username, String password);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
